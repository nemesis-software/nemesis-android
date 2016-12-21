package io.nemesis.ninder.logic;

import com.google.gson.JsonObject;

import java.util.List;

import io.nemesis.ninder.model.Product;
import io.nemesis.ninder.model.ProductEntity;
import io.nemesis.ninder.model.VariantOption;

/**
 * @author Philip
 * @since 2015-11-23
 */
public interface ProductFacade {

    class EndOfQueueException extends Exception {
        EndOfQueueException(String details) {
            super(details);
        }
    }

    interface AsyncCallback<T> {
        /**
         * only called when {@link #getProductsAsync} fetched new data
         * @param items data retrieved by the call
         */
        void onSuccess(T items);

        /**
         * called when {@link #getProductsAsync} fetched no data, regardless the cause
         * @param t Throwable object containing the cause of the fail.
         *          if this is instance of EndOfQueueException, callers exceeded the range of items they can view
         *          and going further incrementing the starting index will never return new content.
         */
        void onFail(Throwable t);
    }

    interface EnquiryCallback {
        /**
         * only called when {@link #enquireAsync(Product, EnquiryCallback)} fetched new data
         * @param products data retrieved by the call
         */
        void onSuccess(ProductEntity products);

        /**
         * called when {@link #enquireAsync(Product, EnquiryCallback)} fetched no data, regardless the cause
         * @param e Exception object containing the cause of the fail.
         */
        void onFail(Exception e);
    }

    /**
     * Login using the credentials provided
     * {@code callback} will be called with the result.
     *
     * @param callback called when method completes its execution,
     *                 this should be called on the main thread
     *
     * @param email email
     *
     * @param password password
     */
    void loginAsync(String email, String password, AsyncCallback<Void> callback);

    /**
     * Initiate a search with the term provided to return a list of autocomplete items.
     * {@code callback} will be called with the result.
     *
     * @param callback called when method completes its execution,
     *                 this should be called on the main thread
     *
     * @param term term
     */
    void autoComplete(String term, AsyncCallback<List<Product>> callback);


    /**
     * Initiate a search with the term provided to return a list of autocomplete items.
     * {@code callback} will be called with the result.
     *
     * @param callback called when method completes its execution,
     *                 this should be called on the main thread
     *
     */
    void getAccountInfo(AsyncCallback<Void> callback);
    
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
    void getProductsAsync(int size, int page, AsyncCallback<List<ProductWrapper>> callback);
    /**
     * The user approved your product! Take advantage of his interests...
     *
     * @param product Product
     */
    void like(ProductWrapper product);

    /**
     * The user disapproved your product! Do not bother him with more of the same.
     *
     * @param product Product
     * @param variant Products Variation
     */
    void dislike(ProductWrapper product, VariantOption variant);

    /**
     * Add a product to users wishlist
     *
     * @param product Product
     */
    void addToWishlist(ProductWrapper product);

    /**
     * retrieve additional details about a product
     * @param product
     */
    void enquireAsync(Product product, EnquiryCallback callback);

    void savePaymentDetails(JsonObject object,AsyncCallback<String> callback);
    void saveDeliveryAddress(JsonObject object,AsyncCallback<String> callback);
    void getCart(AsyncCallback<String> callback);
    void updatePassword(JsonObject object, AsyncCallback<String> callback);
}
