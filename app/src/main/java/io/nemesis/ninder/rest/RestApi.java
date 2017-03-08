package io.nemesis.ninder.rest;


import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import io.nemesis.ninder.model.Product;
import io.nemesis.ninder.model.ProductEntity;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

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
    @GET("api/login")
    Call<Void> loginAsync(@Header("X-Nemesis-Username") String email, @Header("X-Nemesis-Password") String password);

    @GET("api/search/autocomplete")
    Call<List<Product>> autoComplete(@Query("term") String term);

    @GET("api/my-account/profile")
    Call<Void> getAccountInfo(@Header("X-Auth-Token") String token);
    @Headers({
            "Accept: application/json;charset=UTF-8",
            "Content-Type: application/json;charset=UTF-8"
    })

    @GET("api/c/womens")
    Call<List<Product>> getProductListAsync(@QueryMap Map<String, String> query);

    @GET("api/cart")
    Call<String> getCart(@Header("X-Auth-Token") String token);

    @Headers({
            "Accept: application/json;charset=UTF-8",
            "Content-Type: application/json;charset=UTF-8"
    })
    @POST("api/checkout/createDeliveryAddress")
    Call<String> saveDeliveryAddress(@Header("X-Auth-Token") String token, @Body JsonObject json);

    @Headers({
            "Accept: application/json;charset=UTF-8",
            "Content-Type: application/json;charset=UTF-8"
    })
    @POST("api/checkout/createPaymentDetails")
    Call<String> savePaymentDetails(@Header("X-Auth-Token") String token, @Body JsonObject json);


    @GET("api{productURL}")
    Call<ProductEntity> getProductDetailAsync(@Path(value="productURL", encoded=false) String url);

    // XXX service methods: Must have either a return type or Call as last argument.
    // XXX java.lang.NullPointerException: Attempt to invoke interface method
    // 'void retrofit2.Call.failure(retrofit2.RetrofitError)' on a null object reference
    // asys if the caller does not care about the result of the call
    //@FormUrlEncoded
    @FormUrlEncoded
    @POST("wishlist/entry/add")
    Call<Void> addToWishlistAsync(@Header("X-Auth-Token") String token, @Field("productCode") String productCode, @Field("userId") String userId);

    @Headers({
            "Accept: application/json;charset=UTF-8",
            "Content-Type: application/json;charset=UTF-8"
    })
    @PUT("api/my-account/update-password")
    Call<String> updatePassword(@Header("X-Auth-Token") String token,@Body JsonObject json);
}
