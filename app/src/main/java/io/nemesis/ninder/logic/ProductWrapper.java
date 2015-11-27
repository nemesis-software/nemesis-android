package io.nemesis.ninder.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

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
        private int status;
        private Product data;
        private List<ProductFacade.EnquiryCallback> observers = new ArrayList<>();
        private Exception lastError;

        public void addObserver(ProductFacade.EnquiryCallback callback) {
            if (status == 1) {
                callback.onSuccess(data);
            } else if (status == -1) {
                callback.onFail(lastError);
            }
        }

        public void onDetailsFetched(Product product) {
            status = 1;
            this.data = product;
            notifySuccess();
        }

        public void onDetailsFetchFailed(Exception err) {
            status = -1;
            this.lastError = err;
            notifyFail();
        }

        public void onEnquiry() {
            status = 0;
        }

        private void notifyFail() {
            for (ProductFacade.EnquiryCallback cb : observers) {
                cb.onFail(lastError);
            }
        }

        private void notifySuccess() {
            for (ProductFacade.EnquiryCallback cb : observers) {
                cb.onSuccess(data);
            }
        }
    }

    private final NemesisFacadeImpl api;
    private volatile Product pojo;
    private volatile List<Image> galleryImages;
    private volatile Image photo;

    ProductWrapper(Product product, NemesisFacadeImpl api) {
        this.pojo = product;
        this.api = api;

        this.galleryImages = new ArrayList<>();
        int size = pojo.getImages().size();

//        for (Image image : item.getImages()) {
//            if ("picture".equalsIgnoreCase(image.getFormat())) {
//                imgUrl = image.getUrl();
//                break;
//            }
//        }

        this.photo = (0 != size) ? pojo.getImages().get(size -1) : null;

        if (!hasDetails()) {
            enquireDetails();
        }
    }

    public List<Image> getGalleryImages() {
        return galleryImages;
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
        return null != pojo.getVariantType();
    }

    private void sortImages() {
        List<Image> images = pojo.getImages();
        for (Image img : images) {
            if ("gallery".equalsIgnoreCase(img.getFormat())) {
                galleryImages.add(img);
            } else if ("photo".equalsIgnoreCase(img.getFormat())) {
                photo = img;
            }
        }

        List<Image> variaztionImages = pojo.getVariantOptions().get(0).getImages();
        for (Image img : variaztionImages) {
            if ("gallery".equalsIgnoreCase(img.getFormat())) {
                galleryImages.add(img);
            } else if ("photo".equalsIgnoreCase(img.getFormat())) {
                photo = img;
            }
        }
    }

    private void enquireDetails() {
        api.enquireAsync(pojo, new ProductFacade.EnquiryCallback() {
            @Override
            public void onSuccess(Product product) {
                pojo = product;
                sortImages();
//                setChanged();
//                notifyObservers();
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }
}
