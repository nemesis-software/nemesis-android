package io.nemesis.ninder.logic;

import java.util.List;

import io.nemesis.ninder.logic.model.Product;
import io.nemesis.ninder.logic.model.VariantOption;

/**
 * @author Philip
 * @since 2015-11-23
 */
public interface ProductFacade {

    interface AsyncCallback {
        void onSuccess(List<Product> products);
        void onFail(Exception e);
    }

    public List<Product> getProducts(int size, int page);

    void getProductsAsync(int size, int page, AsyncCallback callback);

    public void like(Product product, VariantOption variant);

    public void dislike(Product product, VariantOption variant);

    public void addToWishlist(Product product, VariantOption variant);

}
