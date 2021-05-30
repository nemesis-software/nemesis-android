package io.nemesis.ninder.rest;


import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import io.nemesis.ninder.model.Product;
import io.nemesis.ninder.model.ProductEntity;
import io.nemesis.ninder.model.ProductFacetSearchPageDto;
import io.nemesis.ninder.model.SearchHit;
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
    @GET("facade/auth")
    Call<Void> loginAsync(@Header("X-Nemesis-Username") String email, @Header("X-Nemesis-Password") String password);

    @GET("facade/search/autocomplete")
    Call<ProductFacetSearchPageDto> autoComplete(@QueryMap Map<String, String> query);

    @GET("api/my-account/profile")
    Call<Void> getAccountInfo(@Header("X-Nemesis-Token") String token);

    @Headers({
            "Accept: application/json;charset=UTF-8",
            "Content-Type: application/json;charset=UTF-8"
    })
    @GET("facade/search")
    Call<ProductFacetSearchPageDto> getProductListAsync(@QueryMap Map<String, String> query);

    @GET("facade/bundle/cart/current")
    Call<String> getCart(@Header("X-Nemesis-Token") String token);

    @Headers({
            "Accept: application/json;charset=UTF-8",
            "Content-Type: application/json;charset=UTF-8"
    })
    @PUT("facade/checkout/deliveryAddress")
    Call<String> saveDeliveryAddress(@Header("X-Nemesis-Token") String token, @Body JsonObject json);

    @Headers({
            "Accept: application/json;charset=UTF-8",
            "Content-Type: application/json;charset=UTF-8"
    })
    @PUT("facade/checkout/paymentDetails")
    Call<String> savePaymentDetails(@Header("X-Nemesis-Token") String token, @Body JsonObject json);


    @GET("facade/product/{productCode}")
    Call<Product> getProductDetailAsync(@Path(value="productCode") String productCode, @Query("projection") String projection);

    // XXX service methods: Must have either a return type or Call as last argument.
    // XXX java.lang.NullPointerException: Attempt to invoke interface method
    // 'void retrofit2.Call.failure(retrofit2.RetrofitError)' on a null object reference
    // asys if the caller does not care about the result of the call
    //@FormUrlEncoded
    @POST("facade/wishlist/entry/add")
    Call<Void> addToWishlistAsync(@Header("X-Nemesis-Token") String token, @Query("productCode") String productCode, @Query("wishlistCode") String wishlistCode);

    @Headers({
            "Accept: application/json;charset=UTF-8",
            "Content-Type: application/json;charset=UTF-8"
    })
    @POST("facade/customer/update-password")
    Call<String> updatePassword(@Header("X-Nemesis-Token") String token,@Body JsonObject json);
}
