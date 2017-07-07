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
    private final String testUserId;

    private final ConcurrentHashMap<String, ProductWrapper.ProductState> enquiries;
    private final Context mContext;

    public NemesisFacadeImpl(Context context) {
        mContext = context.getApplicationContext();
        testUserId = context.getString(R.string.rest_api_test_user);
        enquiries = new ConcurrentHashMap<>();
    }

    @Override
    public void loginAsync(String email, String password, final AsyncCallback<Void> callback) {
        NemesisRetrofitRestClient.getApiService().loginAsync(email, password).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    storeToken(response.headers().get("x-auth-token"));
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
    public void autoComplete(String term, final AsyncCallback<List<Product>> callback) {
        NemesisRetrofitRestClient.getApiService().autoComplete(term).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFail(new Exception(response.code()+response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                callback.onFail(t);
            }
        });
    }


    @Override
    public void getAccountInfo(final AsyncCallback<Void> callback) {
        String token = readToken();
        NemesisRetrofitRestClient.getApiService().getAccountInfo(token).enqueue(new Callback<Void>() {
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
        NemesisRetrofitRestClient.getApiService().getCart(readToken()).enqueue(new Callback<String>() {
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

        NemesisRetrofitRestClient.getApiService().getProductListAsync(query).enqueue( new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    if (null != callback) {
                        if (response.body().size() == 0) {
                            callback.onFail(new EndOfQueueException("End of queue reached"));
                        } else {
                            ArrayList<ProductWrapper> arrayList = new ArrayList<>();
                            for (Product p : response.body()) {
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
            public void onFailure(Call<List<Product>> call, Throwable t) {
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

        List<Variation> variations = wrapper.getVariations();
        Variation variation = null != variations && !variations.isEmpty() ? variations.get(0) : null;

        if (variation != null) {
            addToWishList(variation);
        } else {
            enquireAsync(prod, new EnquiryCallback() {
                @Override
                public void onSuccess(ProductEntity entity) {
                    List<Variation> variations = entity.getVariants();
                    Variation variation = null != variations && !variations.isEmpty() ? variations.get(0) : null;

                    if (variation != null) {
                        addToWishList(variation);
                    } else {
                        Product product = entity.getProduct();
                        TLog.w("missing variant for product: " + product.getUrl());
                    }
                }

                @Override
                public void onFail(Exception e) {
                    TLog.e("ERROR on product details call:", e);
                }
            });
        }
    }

    private void addToWishList(final Variation variant) {
        if (variant == null) {
            throw new IllegalArgumentException();
        }

        final String code = variant.getCode();
        if (TextUtils.isEmpty(code)) {
            TLog.w("variant code isEmpty. variant=" + variant);
            return;
        }
        String token = readToken();

        NemesisRetrofitRestClient.getApiService().addToWishlistAsync(token,code,testUserId).enqueue(new Callback<Void>() {
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
        NemesisRetrofitRestClient.getApiService().getProductDetailAsync(product.getUrl()).enqueue(new Callback<ProductEntity>() {
            @Override
            public void onResponse(Call<ProductEntity> call, Response<ProductEntity> response) {
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
            public void onFailure(Call<ProductEntity> call,Throwable t) {
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
        NemesisRetrofitRestClient.getApiService().savePaymentDetails(readToken(),json).enqueue(new Callback<String>() {
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
        NemesisRetrofitRestClient.getApiService().saveDeliveryAddress(readToken(),json).enqueue(new Callback<String>() {
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
        NemesisRetrofitRestClient.getApiService().updatePassword(readToken(),json).enqueue(new Callback<String>() {
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
