package io.nemesis.ninder.logic;

import java.util.ArrayList;
import java.util.List;

import io.nemesis.ninder.model.Category;
import io.nemesis.ninder.model.Image;
import io.nemesis.ninder.model.Price;
import io.nemesis.ninder.model.Product;
import io.nemesis.ninder.model.ProductEntity;
import io.nemesis.ninder.model.VariantOption;
import io.nemesis.ninder.model.Variation;

/**
 * @author ivanpetkov
 * @since 11/27/15
 */
public class ProductWrapper {

    public static class ProductState {

        private volatile int status;
        private ProductEntity data;
        private final List<ProductFacade.EnquiryCallback> callbacks = new ArrayList<>();
        private Exception lastError = new Exception("unknown error");
        private Object lock = new Object();

        public void addCallback(ProductFacade.EnquiryCallback observer) {
            synchronized (lock) {
                if (status == 1) {
                    observer.onSuccess(data);
                } else if (status == -1) {
                    observer.onFail(lastError);
                } else {
                    callbacks.add(observer);
                }
            }
        }

        public int getStatus() {
            return status;
        }

        private synchronized void updateStatus(int newStatus) {
            this.status = newStatus;
        }

        public synchronized void onDetailsFetched(ProductEntity entity) {
            synchronized (lock) {
                updateStatus(1);
                this.data = entity;
                notifySuccess();
            }
        }

        public synchronized void onDetailsFetchFailed(Exception err) {
            synchronized (lock) {
                updateStatus(-1);
                this.lastError = err;
                notifyFail();
            }
        }

        public void onEnquiry() {
            updateStatus(0);
        }

        private void notifyFail() {
            ProductFacade.EnquiryCallback[] enquiryCallbacks = null;
            synchronized (lock) {
                enquiryCallbacks = new ProductFacade.EnquiryCallback[callbacks.size()];
                callbacks.toArray(enquiryCallbacks);
                callbacks.clear();
            }

            for (ProductFacade.EnquiryCallback cb : enquiryCallbacks) {
                cb.onFail(lastError);
            }
        }

        private void notifySuccess() {
            ProductFacade.EnquiryCallback[] enquiryCallbacks = null;
            synchronized (lock) {
                enquiryCallbacks = new ProductFacade.EnquiryCallback[callbacks.size()];
                callbacks.toArray(enquiryCallbacks);
                callbacks.clear();
            }

            for (ProductFacade.EnquiryCallback cb : enquiryCallbacks) {
                cb.onSuccess(data);
            }
        }
    }

    private final NemesisFacadeImpl api;
    private volatile Product pojo;
    private List<Variation> variations;
    private volatile List<Image> galleryImages;
    private volatile Image photo;
    private boolean is_favourite = false;

    private Object lock = new Object();

    ProductWrapper(Product product, NemesisFacadeImpl api) {
        this.pojo = product;
        this.api = api;

        this.galleryImages = new ArrayList<>();
        if(pojo.getImages()!=null) {
            int size = pojo.getImages().size();

            this.photo = (0 != size) ? pojo.getImages().get(size - 1) : null;
        }
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

    public boolean getFavourite() {
        return is_favourite;
    }
    public void setFavourite(boolean is_favourite) {
        this.is_favourite = is_favourite;
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

    public String getCategory() {
        List<Category> categories = this.pojo.getCategories();
        if (categories != null && !categories.isEmpty()) {
            Category category = categories.get(0);
            return category.getName();
        }

        return null;
    }

    public String getVariantType() {
        return pojo.getVariantType();
    }

    public String getCode() {
        List<VariantOption> variantOptions = pojo.getVariantOptions();
        if (variantOptions != null && !variantOptions.isEmpty()) {
            VariantOption variantOption = variantOptions.get(0);
            return null != variantOption ? variantOption.getCode() : null;
        }

        return null;
    }

    public Price getPrice() {
        Price expand = null;
        List<VariantOption> variantOptions = pojo.getVariantOptions();
        if (variantOptions != null && !variantOptions.isEmpty()) {
            VariantOption variantOption = pojo.getVariantOptions().get(0);
            expand = null != variantOption ? variantOption.getPrice() : null;
        }

        return null != expand ? expand : pojo.getPrice();
    }

    public Price getDiscountedPrice() {
        Price expand = null;
        List<VariantOption> variantOptions = pojo.getVariantOptions();
        if (variantOptions != null && !variantOptions.isEmpty()) {
            VariantOption variantOption = pojo.getVariantOptions().get(0);
            expand = null != variantOption ? variantOption.getDiscountedPrice() : null;
        }

        return null != expand ? expand : pojo.getDiscountedPrice();
    }

    public Product getProduct() {
        return pojo;
    }

    public boolean hasDetails() {
        return variations != null && !variations.isEmpty();
//        return null != pojo.getVariantType() && null != pojo.getVariantOptions() && pojo.getVariantOptions().size() > 0;
    }

    public List<Variation> getVariations() {
        return variations;
    }

    private void sortImages() {
        synchronized (lock) {
            galleryImages.clear();

            List<VariantOption> variantOptions = pojo.getVariantOptions();
            if (variantOptions != null && !variantOptions.isEmpty()) {
                VariantOption variantOption = variantOptions.get(0);
                List<Image> variantImages = variantOption.getImages();
                if (variantImages != null && !variantImages.isEmpty()) {
                    for (Image img : variantImages) {
                        if ("gallery".equalsIgnoreCase(img.getImageType()) && "product".equalsIgnoreCase(img.getFormat())) {
                            galleryImages.add(img);
                        } else if ("photo".equalsIgnoreCase(img.getFormat())) {
                            photo = img;
                        }
                    }
                }
            }

            boolean addImages = galleryImages.isEmpty();
            boolean addPhoto = photo != null;

            List<Image> images = pojo.getImages();
            if (images != null && !images.isEmpty()) {
                for (Image img : images) {
                    if ("gallery".equalsIgnoreCase(img.getImageType()) && "product".equalsIgnoreCase(img.getFormat())) {
                        if (addImages) {
                            galleryImages.add(img);
                        }
                    } else if ("photo".equalsIgnoreCase(img.getFormat())) {
                        if (addPhoto) {
                            photo = img;
                        }
                    }
                }
            }
        }
    }

    public void enquireDetails(final ProductFacade.EnquiryCallback callback) {
        api.enquireAsync(pojo, new ProductFacade.EnquiryCallback() {
            @Override
            public void onSuccess(ProductEntity product) {
                pojo = product.getProduct();
                variations = product.getVariants();
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
