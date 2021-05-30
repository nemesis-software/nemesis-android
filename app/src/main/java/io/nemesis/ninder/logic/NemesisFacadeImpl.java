package io.nemesis.ninder.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.nemesis.ninder.R;
import io.nemesis.ninder.logger.TLog;
import io.nemesis.ninder.model.Product;
import io.nemesis.ninder.model.ProductEntity;
import io.nemesis.ninder.model.ProductFacetSearchPageDto;
import io.nemesis.ninder.model.SearchHit;
import io.nemesis.ninder.model.VariantOption;
import io.nemesis.ninder.model.Variation;
import io.nemesis.ninder.rest.NemesisRetrofitRestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Philip
 * @since 2015-11-23
 */
public class NemesisFacadeImpl implements ProductFacade {

    private static final String QUERY_PAGE_INDEX = "page";
    private static final String QUERY_PAGE_SIZE = "size";
    private static final String TAG = "RestApi";
    private final String testUserId;

    private final ConcurrentHashMap<String, ProductWrapper.ProductState> enquiries;

    private final Context mContext;
    // TODO will i need to create 2 instances. One for retrieving data and one to add to wishlist in order not to block execution queues?
    private final NemesisRetrofitRestClient retrofitRestClient;

    public NemesisFacadeImpl(Context context) {
        mContext = context.getApplicationContext();
        testUserId = context.getString(R.string.rest_api_test_user);

        retrofitRestClient = new NemesisRetrofitRestClient(mContext, mContext.getString(R.string.rest_api_base_url));
        enquiries = new ConcurrentHashMap<>();
    }

    @Override
    public void loginAsync(String email, String password, final AsyncCallback<Void> callback) {
        retrofitRestClient.getApiService().loginAsync(email, password).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    storeToken(response.headers().get("X-Nemesis-Token"));
                    callback.onSuccess(response.body());
                }
                else callback.onFail(new Throwable(response.message()));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFail(t);
            }
        });
    }

    @Override
    public void autoComplete(String term, final AsyncCallback<ProductFacetSearchPageDto> callback) {

        Map<String, String> query = new HashMap<>();
        query.put(QUERY_PAGE_INDEX, "0");
        query.put(QUERY_PAGE_SIZE, "10");
        query.put("searchType", "product");
        query.put("queryName", "autocomplete");
        query.put("q", term);

        retrofitRestClient.getApiService().autoComplete(query).enqueue(new Callback<ProductFacetSearchPageDto>() {
            @Override
            public void onResponse(Call<ProductFacetSearchPageDto> call, Response<ProductFacetSearchPageDto> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFail(new Exception(response.code()+response.message()));
                }
            }

            @Override
            public void onFailure(Call<ProductFacetSearchPageDto> call, Throwable t) {
                callback.onFail(t);
            }
        });
    }


    @Override
    public void getAccountInfo(final AsyncCallback<Void> callback) {
        String token = readToken();
        retrofitRestClient.getApiService().getAccountInfo(token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFail(new Exception(response.code() + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFail(t);
            }
        });
    }

    @Override
    public void getCart(final AsyncCallback<String> callback){
        retrofitRestClient.getApiService().getCart(readToken()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFail(new Exception(response.code() + response.message()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFail(t);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getProductsAsync(int size, int page, final AsyncCallback callback) {
        Map<String, String> query = new HashMap<>();
        query.put(QUERY_PAGE_INDEX, String.valueOf(page));
        query.put(QUERY_PAGE_SIZE, String.valueOf(size));
        query.put("categoryCode", "womens");
        query.put("queryName", "productSearch");
        query.put("projection", "io.nemesis.platform.module.commerce.facade.search.dto.ProductFacetSearchPageDtoDefinition");

        retrofitRestClient.getApiService().getProductListAsync(query).enqueue( new Callback<ProductFacetSearchPageDto>() {
            @Override
            public void onResponse(Call<ProductFacetSearchPageDto> call, Response<ProductFacetSearchPageDto> response) {
                if (response.isSuccessful()) {
                    if (null != callback) {
                        if (response.body().getContent().size() == 0) {
                            callback.onFail(new EndOfQueueException("End of queue reached"));
                        } else {
                            ArrayList<ProductWrapper> arrayList = new ArrayList<>();
                            for (SearchHit p : response.body().getContent()) {
                                arrayList.add(new ProductWrapper(p, NemesisFacadeImpl.this));
                            }
                            callback.onSuccess(arrayList);
                        }
                    }
                } else {
                    if (null != callback) {
                        callback.onFail(new RuntimeException("bad response: " + response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductFacetSearchPageDto> call, Throwable t) {
                if (null != callback) {
                    callback.onFail(t);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void like(ProductWrapper product) {
        if (null == product || null == product.getProduct()) return;
        addToWishlist(product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dislike(ProductWrapper product, VariantOption variant) {
        if (null == product || null == product.getProduct() || null == product.getProduct().getCode()) return;
            enquiries.remove(product.getProduct().getCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addToWishlist(final ProductWrapper wrapper) {
        if (wrapper == null || wrapper.getProduct() == null) {
            throw new IllegalArgumentException();
        }
        Product prod = wrapper.getProduct();

        TLog.d("addToWishlist: " + prod.getUrl());

        enquireAsync(prod, new EnquiryCallback() {
            @Override
            public void onSuccess(Product entity) {
                addToWishList(entity);
            }

            @Override
            public void onFail(Exception e) {
                TLog.e("ERROR on product details call:", e);
            }
        });
    }

    private void addToWishList(final Product variant) {
        if (variant == null) {
            throw new IllegalArgumentException();
        }

        final String code = variant.getCode();
        if (TextUtils.isEmpty(code)) {
            TLog.w("variant code isEmpty. variant=" + variant);
            return;
        }
        String token = readToken();

        retrofitRestClient.getApiService().addToWishlistAsync(token,code,null).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                TLog.d("added to wishlist");
                Answers.getInstance().logAddToCart(new AddToCartEvent()
                        .putItemId(variant.getCode())
                );
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                TLog.e("cannot add to wishlist:", t);

            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enquireAsync(final Product product, final EnquiryCallback callback) {
        ProductWrapper.ProductState state = enquiries.get(product.getCode());
        if (null == state) {
            // create state and add
            state = new ProductWrapper.ProductState();
            enquiries.put(product.getCode(), state);
        } else {
            if (null != callback) {
                state.addCallback(callback);
            }
            if(state.getStatus() == 1)
                return;
        }

        state.onEnquiry();
        retrofitRestClient.getApiService().getProductDetailAsync(product.getCode(), "io.nemesis.platform.module.commerce.facade.order.dto.CartEntryProductDtoDefinition").enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                ProductWrapper.ProductState productState = enquiries.get(product.getCode());
                if (null != productState) {
                    if (response.isSuccessful()) {
                        productState.onDetailsFetched(response.body());
                    } else {
                        productState.onDetailsFetchFailed(new Exception("Bad response code:" + response.message()));
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<Product> call,Throwable t) {
                ProductWrapper.ProductState productState = enquiries.get(product.getCode());
                if (null != productState) {
                    productState.onDetailsFetchFailed(new Exception(t));
                } else {
                }
            }
        });
    }
    @Override
    public void savePaymentDetails(JsonObject json, final AsyncCallback<String> callback){
        retrofitRestClient.getApiService().savePaymentDetails(readToken(),json).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFail(new Exception(response.code() + response.message()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFail(t);
            }
        });
    }
    @Override
    public void saveDeliveryAddress(JsonObject json, final AsyncCallback<String> callback){
        retrofitRestClient.getApiService().saveDeliveryAddress(readToken(),json).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFail(new Exception(response.code() + response.message()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFail(t);
            }
        });
    }
    @Override
    public void updatePassword(JsonObject json, final AsyncCallback<String> callback){
        retrofitRestClient.getApiService().updatePassword(readToken(),json).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFail(new Exception(response.code() + response.message()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFail(t);
            }
        });
    }

    private void storeToken(String token){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(mContext.getString(R.string.token), token);
        editor.apply();
    }

    private String readToken(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPref.getString(mContext.getString(R.string.token),null);
    }

    public ProductWrapper wrap(Product product) {
        return new ProductWrapper(product, this);
    }
}
