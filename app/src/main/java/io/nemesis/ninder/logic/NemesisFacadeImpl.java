package io.nemesis.ninder.logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.nemesis.ninder.logic.model.Product;
import io.nemesis.ninder.logic.model.VariantOption;
import io.nemesis.ninder.logic.rest.NemesisRetrofitRestClient;
import io.nemesis.ninder.util.StringUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author Philip
 * @since 2015-11-23
 */
public class NemesisFacadeImpl implements ProductFacade {

    private static final String QUERY_PAGE_INDEX = "page";
    private static final String QUERY_PAGE_SIZE = "size";
    private static final String QUERY_PRODUCT_ID = "productId";
    private static final String QUERY_USER_ID = "userId";

    private static final String TEST_USER_ID = "paranoiabla@gmail.com";

    private static final int DEFAULT_PRODUCT_PAGE_SIZE = 8;
    private static final int DEFAULT_PRODUCT_PAGE_NUMBER = 0;

    private final Context mContext;
    // TODO will i need to create 2 instances. One for retrieving data and one to add to wishlist in order not to block execution queues?
    private final NemesisRetrofitRestClient retrofitRestClient;

//    int dummyCount = 0;

    public NemesisFacadeImpl(Context context) {
        mContext = context.getApplicationContext();
        retrofitRestClient = new NemesisRetrofitRestClient(mContext);
    }

    @Override
    public List<Product> getProducts(int size, int page) {

        Map<String, String> query = new HashMap<>();
        query.put(QUERY_PAGE_INDEX, String.valueOf(page));
        query.put(QUERY_PAGE_SIZE, String.valueOf(size));

        return retrofitRestClient.getApiService().getProductList(query);
    }

    @Override
    public void getProductsAsync(int size, int page, final AsyncCallback callback) {

        Map<String, String> query = new HashMap<>();
        query.put(QUERY_PAGE_INDEX, String.valueOf(page));
        query.put(QUERY_PAGE_SIZE, String.valueOf(size));

        retrofitRestClient.getApiService().getProductListAsync(query, new Callback<List<Product>>() {
            @Override
            public void success(List<Product> products, Response response) {
                if (response.getStatus() == 200) {
                    if (null != callback) {
                        if (products.size() == 0) {
                            callback.onFail(new EndOfQueueException("End of queue reached"));
                        } else {
                            callback.onSuccess(products);
                        }
                    }
                } else {
                    if (null != callback) {
                        callback.onFail(new RuntimeException("bad response: " + response.getStatus()));
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (null != callback) {
                    callback.onFail(error);
                }
            }
        });
    }

    @Override
    public void like(Product product, VariantOption variant) {
        Toast.makeText(mContext, "Like", Toast.LENGTH_SHORT).show();
        addToWishlist(product, variant);
    }

    @Override
    public void dislike(Product product, VariantOption variant) {
        // XXX adopt https://dev.mysql.com/doc/refman/5.7/en/blackhole-storage-engine.html
        Toast.makeText(mContext, "Dislike", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addToWishlist(final Product product, final VariantOption variant) {
        Toast.makeText(mContext, "addToWishlist", Toast.LENGTH_SHORT).show();

        if (null == variant || StringUtils.isEmpty(variant.getCode())) {

            retrofitRestClient.getApiService().getProductDetailAsync(product.getUrl(), new Callback<Product>() {
                @Override
                public void success(Product product, Response response) {

                    if (response.getStatus() == 200) {
                        String code = product.getVariantOptions().get(0).getCode();

                        retrofitRestClient.getApiService().addToWishlistAsync(code, TEST_USER_ID, new Callback<Void>() {
                            @Override
                            public void success(Void aVoid, Response response) {
                                // don't care
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                // notify
                                Log.e("add to wishlist:", error.getMessage());
                            }
                        });
                    } else {
                        Log.e("addToWishlist", "bad response: " + response.getStatus());
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });
        } else {
            String code = variant.getCode();
            retrofitRestClient.getApiService().addToWishlistAsync(code, TEST_USER_ID, new Callback<Void>() {
                @Override
                public void success(Void aVoid, Response response) {
                    // don't care
                }

                @Override
                public void failure(RetrofitError error) {
                    // notify
                    Log.e("add to wishlist:", error.getMessage());
                }
            });
        }
    }
}
