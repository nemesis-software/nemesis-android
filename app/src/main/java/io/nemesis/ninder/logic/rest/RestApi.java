package io.nemesis.ninder.logic.rest;

import java.util.List;
import java.util.Map;

import io.nemesis.ninder.logic.model.Product;
import io.nemesis.ninder.logic.model.ProductEntity;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * @author Philip
 * @since 2015-11-24
 */
public interface RestApi {
    // XXX Retrofit supports synchronous and asynchronous request execution.
    // Users define the concrete execution by setting a return type (synchronous) or not (asynchronous) to service methods.

    @Headers({
            "Accept: application/json;charset=UTF-8",
            "Content-Type: application/json;charset=UTF-8"
    })
    @GET("/api/login")
    void loginAsync(@Header("X-Nemesis-Username") String email, @Header("X-Nemesis-Password") String password, retrofit.Callback<Void> callback);

    @GET("/api/c/womens")
    List<Product> getProductList();

    @GET("/api/c/womens")
    List<Product> getProductList(@QueryMap Map<String, String> query);

    @GET("/api/c/womens")
    void getProductListAsync(retrofit.Callback<List<Product>> callback);

    @GET("/api/c/womens")
    void getProductListAsync(@QueryMap Map<String, String> query, retrofit.Callback<List<Product>> callback);

    @GET("/api/{productURL}")
    ProductEntity getProductDetail(@Path(value="productURL", encode=false) String url);

    @GET("/api{productURL}")
    void getProductDetailAsync(@Path(value="productURL", encode=false) String url, retrofit.Callback<ProductEntity> callback);

    // XXX service methods: Must have either a return type or Callback as last argument.
    // XXX java.lang.NullPointerException: Attempt to invoke interface method
    // 'void retrofit.Callback.failure(retrofit.RetrofitError)' on a null object reference
    // async methods cannot set null callbacks if the caller does not care about the result of the call
    @FormUrlEncoded
    @POST("/wishlist/entry/add")
    void addToWishlistAsync(@Field("productCode") String productCode, @Field("userId") String userId, retrofit.Callback<Void> callback);
}
