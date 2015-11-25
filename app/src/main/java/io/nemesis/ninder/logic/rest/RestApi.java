package io.nemesis.ninder.logic.rest;

import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;

import io.nemesis.ninder.logic.model.Product;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * @author Philip
 * @since 2015-11-24
 */
public interface RestApi {


//    @GET("/users/{user}")      //here is the other url part.best way is to start using /
//    public void getFeed(@Path("user") String user,Callback<gitmodel> response);     //string user is for passing values from edittext for eg: user=basil2style,google
//    //response is the response from the server which is now in the POJO

    // XXX Retrofit supports synchronous and asynchronous request execution.
    // Users define the concrete execution by setting a return type (synchronous) or not (asynchronous) to service methods.

    @GET("/api/c/womens")
//    @GET("/sal")
    List<Product> getProductList();

    @GET("/api/c/womens")
    List<Product> getProductList(@QueryMap Map<String, String> query);

    @GET("/api/c/womens")
//    @GET("/sal")
    void getProductListAsync(retrofit.Callback<List<Product>> callback);

    @GET("/api/c/womens")
    void getProductListAsync(@QueryMap Map<String, String> query, retrofit.Callback<List<Product>> callback);

    @GET("/api/{productURL}")
    Product getProductDetail(@Path("productURL") String url);

    @GET("/api/{productURL}")
    void getProductDetailAsync(@Path("productURL") String url, retrofit.Callback<Product> callback);

    @FormUrlEncoded
    @POST("/wishlist/entry/add")
    void addToWishlist(@QueryMap Map<String, String> query);

    @FormUrlEncoded
    @POST("/wishlist/entry/add")
    void addToWishlistAsync(@QueryMap Map<String, String> query, retrofit.Callback<Void> callback);
}
