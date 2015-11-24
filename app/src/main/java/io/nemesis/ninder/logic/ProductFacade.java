package io.nemesis.ninder.logic;

import java.util.List;

import io.nemesis.ninder.logic.model.Product;
import io.nemesis.ninder.logic.model.VariantOption;

/**
 * @author Philip
 * @since 2015-11-23
 */
public interface ProductFacade {

    public List<Product> getProducts(int size, int page);

    public void like(Product product, VariantOption variant);

    public void dislike(Product product, VariantOption variant);

    public void addToWishlist(Product product, VariantOption variant);

}
