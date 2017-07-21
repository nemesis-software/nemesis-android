package io.nemesis.ninder.rest;


import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import io.nemesis.ninder.model.Product;
import io.nemesis.ninder.model.ProductEntity;
import io.nemesis.ninder.rest.data.UpdatePasswordData;
import okhttp3.RequestBody;
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

public interface RestApi {

    @GET("facade/auth")
    Call<Void> loginAsync(@Header("X-Nemesis-Username") String email, @Header("X-Nemesis-Password") String password);

    @GET("api/search/autocomplete")
    Call<List<Product>> autoComplete(@Query("term") String term);

    @GET("facade/customer/current")
    Call<Void> getAccountInfo(@Header("X-Auth-Token") String token);

    @GET("api/c/womens")
    Call<List<Product>> getProductListAsync(@QueryMap Map<String, String> query);

    @GET("facade/cart/current")
    Call<String> getCart(@Header("X-Auth-Token") String token);

    @PUT("facade/checkout/deliveryAddress")
    Call<String> saveDeliveryAddress(@Header("X-Auth-Token") String token, @Body JsonObject json);

    @FormUrlEncoded
    @POST("facade/checkout/createPaymentInfo")
    Call<String> savePaymentDetails(@Header("X-Auth-Token") String token, @Body JsonObject json);


    @GET("facade/product/{productCode}")
    Call<ProductEntity> getProductDetailAsync(@Path(value="productCode") String url);

    @FormUrlEncoded
    @POST("facade/wishlist/entry/add")
    Call<Void> addToWishlistAsync(@Field("wishlistCode") String wishlistCode,
                                  @Field("productCode") String productCode,
                                  @Field("desired") int desired,
                                  @Field("comment") String comment);

    @POST("facade/customer/updatePassword")
    @Headers({"Content-Type: application/json;charset=UTF-8",
            "Accept: application/json;charset=UTF-8"})
    Call<String> updatePassword(@Body UpdatePasswordData data);

}
