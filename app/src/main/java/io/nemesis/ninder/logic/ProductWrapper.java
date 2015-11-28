package io.nemesis.ninder.logic;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.nemesis.ninder.logic.model.Image;
import io.nemesis.ninder.logic.model.Price;
import io.nemesis.ninder.logic.model.Product;
import io.nemesis.ninder.logic.model.VariantOption;

/**
 * @author ivanpetkov
 * @since 11/27/15
 */
public class ProductWrapper {

    public static class ProductState {
        private static final String TAG = "ProductState";
        private volatile int status;
        private Product data;
        private final List<ProductFacade.EnquiryCallback> callbacks = new ArrayList<>();
        private Exception lastError = new Exception("unknown error");
        private Object lock = new Object();

        public synchronized void addCallback(ProductFacade.EnquiryCallback observer) {
            if (status == 1) {
                observer.onSuccess(data);
            } else if (status == -1) {
                observer.onFail(lastError);
            } else {
                callbacks.add(observer);
            }
        }

        public synchronized int getStatus() {
            return status;
        }

        public synchronized void onDetailsFetched(Product product) {
            status = 1;
            this.data = product;
            notifySuccess();
        }

        public synchronized void onDetailsFetchFailed(Exception err) {
            status = -1;
            this.lastError = err;
            notifyFail();
        }

        public synchronized void onEnquiry() {
            status = 0;
        }

        private synchronized void notifyFail() {
            for (ProductFacade.EnquiryCallback cb : callbacks) {
                cb.onFail(lastError);
            }
            callbacks.clear();
        }

        private synchronized void notifySuccess() {
            for (ProductFacade.EnquiryCallback cb : callbacks) {
                cb.onSuccess(data);
            }
            callbacks.clear();
        }
    }

    private final NemesisFacadeImpl api;
    private volatile Product pojo;
    private volatile List<Image> galleryImages;
    private volatile Image photo;

    private Object lock = new Object();

    ProductWrapper(Product product, NemesisFacadeImpl api) {
        this.pojo = product;
        this.api = api;

        this.galleryImages = new ArrayList<>();
        int size = pojo.getImages().size();

        this.photo = (0 != size) ? pojo.getImages().get(size -1) : null;

        if (!hasDetails()) {
            enquireDetails(null);
        } else {
            sortImages();
        }
    }

    public List<Image> getGalleryImages() {
        synchronized (lock) {
            return galleryImages.subList(0, galleryImages.size());
        }
    }

    public Image getPhoto() {
        return photo;
    }

    public String getName() {
        return pojo.getName();
    }

    public String getDescription() {
        return pojo.getDescription();
    }

    public String getVariantType() {
        return pojo.getVariantType();
    }

    public String getCode() {
        VariantOption variantOption = pojo.getVariantOptions().get(0);
        String expand = null != variantOption ? variantOption.getCode() : null;

        return null != expand ? expand : pojo.getUid();
    }

    public Price getPrice() {
        VariantOption variantOption = pojo.getVariantOptions().get(0);
        Price expand = null != variantOption ? variantOption.getPrice() : null;

        return null != expand ? expand : pojo.getPrice();
    }

    public Price getDiscountedPrice() {
        VariantOption variantOption = pojo.getVariantOptions().get(0);
        Price expand = null != variantOption ? variantOption.getDiscountedPrice() : null;

        return null != expand ? expand : pojo.getDiscountedPrice();
    }

    public Product getProduct() {
        return pojo;
    }

    public boolean hasDetails() {
        return null != pojo.getVariantType() && null != pojo.getVariantOptions() && pojo.getVariantOptions().size() > 0;
    }

    private void sortImages() {
        synchronized (lock) {
            galleryImages.clear();
            List<Image> images = pojo.getImages();
            for (Image img : images) {
                if ("gallery".equalsIgnoreCase(img.getImageType()) && "product".equalsIgnoreCase(img.getFormat())) {
                    galleryImages.add(img);
                } else if ("photo".equalsIgnoreCase(img.getFormat())) {
                    photo = img;
                }
            }

            List<Image> variaztionImages = pojo.getVariantOptions().get(0).getImages();
            for (Image img : variaztionImages) {
                if ("gallery".equalsIgnoreCase(img.getImageType()) && "product".equalsIgnoreCase(img.getFormat())) {
                    galleryImages.add(img);
                } else if ("photo".equalsIgnoreCase(img.getFormat())) {
                    photo = img;
                }
            }
        }
    }

    public void enquireDetails(final ProductFacade.EnquiryCallback callback) {
        api.enquireAsync(pojo, new ProductFacade.EnquiryCallback() {
            @Override
            public void onSuccess(Product product) {
                pojo = product;
                sortImages();
                if (null != callback) {
                    callback.onSuccess(product);
                }
            }

            @Override
            public void onFail(Exception e) {
                if (null != callback) {
                    callback.onFail(e);
                }
            }
        });
    }
}
