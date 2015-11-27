package io.nemesis.ninder.logic;

import java.util.List;

import io.nemesis.ninder.logic.model.Product;
import io.nemesis.ninder.logic.model.VariantOption;

/**
 * @author Philip
 * @since 2015-11-23
 */
public interface ProductFacade {

    class EndOfQueueException extends Exception {
        public EndOfQueueException(String details) {
            super(details);
        }
    }

    interface AsyncCallback {
        /**
         * only called when {@link #getProductsAsync} fetched new data
         * @param products data retrieved by the call
         */
        void onSuccess(List<ProductWrapper> products);

        /**
         * called when {@link #getProductsAsync} fetched no data, regardless the cause
         * @param e Exception object containing the cause of the fail.
         *          if this is instance of EndOfQueueException, callers exceeded the range of items they can view
         *          and going further incrementing the starting index will never return new content.
         */
        void onFail(Exception e);
    }

    interface EnquiryCallback {
        /**
         * only called when {@link #enquireAsync(Product, EnquiryCallback)} fetched new data
         * @param products data retrieved by the call
         */
        void onSuccess(Product products);

        /**
         * called when {@link #enquireAsync(Product, EnquiryCallback)} fetched no data, regardless the cause
         * @param e Exception object containing the cause of the fail.
         */
        void onFail(Exception e);
    }

    /**
     * This call is synchronous and will block. In order to retrieve items in background use {@link #getProductsAsync(int, int, AsyncCallback)}
     *
     * Retrieves a Product list of size up to {@code size} Products
     * from the configured endpoint. If no products can be retrieved
     * this method should throw {@link EndOfQueueException} containing the cause of the error
     *
     * @param size the maximum number of items to be retrieved
     *
     * @param page the range of items to retrieve. In the format:
     *             start from {@code page} * {@code size} and get next {@code size} items
     *
     * @return List of retrieved items.
     */
    List<Product> getProducts(int size, int page);

    /**
     * Retrieves a Product list of size up to {@code size} Products
     * from the configured endpoint at background. Once the method completes the provided
     * {@code callback} will be called with the result.
     *
     * @param callback called when method completes its execution,
     *                 this should be called on the main thread
     *
     * @param size the maximum number of items to be retrieved
     *
     * @param page the range of items to retrieve. In the format:
     *             start from {@code page} * {@code size} and get next {@code size} items
     */
    void getProductsAsync(int size, int page, AsyncCallback callback);

    /**
     * The user approved your product! Take advantage of his interests...
     *
     * @param product Product
     * @param variant Products Variation
     */
    void like(Product product, VariantOption variant);

    /**
     * The user disapproved your product! Do not bother him with more of the same.
     *
     * @param product Product
     * @param variant Products Variation
     */
    void dislike(Product product, VariantOption variant);

    /**
     * Add a product to users wishlist
     *
     * @param product Product
     * @param variant Products Variation
     */
    void addToWishlist(Product product, VariantOption variant);

    /**
     * retrieve additional details about a product
     * @param product
     */
    Product enquire(Product product);

    /**
     * retrieve additional details about a product
     * @param product
     */
    void enquireAsync(Product product, EnquiryCallback callback);
}
