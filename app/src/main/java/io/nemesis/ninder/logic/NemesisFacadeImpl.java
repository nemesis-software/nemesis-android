package io.nemesis.ninder.logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    //    private static final String TEST_USER_ID = "paranoiabla@gmail.com";
    private static final String TEST_USER_ID = "ipetkov@insitex.com";

    private static final int DEFAULT_PRODUCT_PAGE_SIZE = 8;
    private static final int DEFAULT_PRODUCT_PAGE_NUMBER = 0;

    private final ConcurrentHashMap<String, ProductWrapper.ProductState> enquiries;

    private final Context mContext;
    // TODO will i need to create 2 instances. One for retrieving data and one to add to wishlist in order not to block execution queues?
    private final NemesisRetrofitRestClient retrofitRestClient;
    private final NemesisRetrofitRestClient retrofitTestRestClient;

    public NemesisFacadeImpl(Context context) {
        mContext = context.getApplicationContext();
        retrofitRestClient = new NemesisRetrofitRestClient(mContext);
        retrofitTestRestClient = new NemesisRetrofitRestClient(mContext, true);
        enquiries = new ConcurrentHashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> getProducts(int size, int page) {

        Map<String, String> query = new HashMap<>();
        query.put(QUERY_PAGE_INDEX, String.valueOf(page));
        query.put(QUERY_PAGE_SIZE, String.valueOf(size));

        return retrofitRestClient.getApiService().getProductList(query);
    }

    /**
     * {@inheritDoc}
     */
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
                            ArrayList<ProductWrapper> arrayList = new ArrayList<>();
                            for (Product p : products) {
                                arrayList.add(new ProductWrapper(p, NemesisFacadeImpl.this));
                            }
                            callback.onSuccess(arrayList);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void like(Product product, VariantOption variant) {
        List<VariantOption> variantOptions = product.getVariantOptions();
        addToWishlist(product, null != variantOptions ? variantOptions.get(0) : null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dislike(Product product, VariantOption variant) {
        if (null != product.getUid())
            enquiries.remove(product.getUid());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addToWishlist(final Product product, final VariantOption variant) {
        Toast.makeText(mContext, "addToWishlist", Toast.LENGTH_SHORT).show();

        if (null != variant && !StringUtils.isEmpty(variant.getCode())) {
            retrofitTestRestClient.getApiService().addToWishlistAsync(variant.getCode(), TEST_USER_ID, new Callback<Void>() {
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
            List<VariantOption> variantOptions = product.getVariantOptions();
            VariantOption v0 = (null != variantOptions) ? product.getVariantOptions().get(0) : null;

            if (null != v0 && !StringUtils.isEmpty(v0.getCode())) {
                retrofitRestClient.getApiService().addToWishlistAsync(v0.getCode(), TEST_USER_ID, new Callback<Void>() {
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
                enquireAsync(product, new EnquiryCallback() {
                    @Override
                    public void onSuccess(Product prod) {
                        String code = prod.getVariantOptions().get(0).getCode();

                        retrofitTestRestClient.getApiService().addToWishlistAsync(code, TEST_USER_ID, new Callback<Void>() {
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

                    @Override
                    public void onFail(Exception e) {
                        Log.e("addToWishlist", e.getMessage());
                    }
                });
            }
        }
    }

    @Deprecated
    /**
     * only Use {@link #enquireAsync(Product, EnquiryCallback)}
     *
     * {@inheritDoc}
     */
    @Override
    public Product enquire(Product product) {
        // TODO verify that product.uid and productDetail.uid mach
        // TODO verify that product does have uid value
        Product productDetail = retrofitRestClient.getApiService().getProductDetail(product.getUrl());
//        enquiries.put(product.getUid(), productDetail);
        return productDetail;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enquireAsync(final Product product, final EnquiryCallback callback) {

        ProductWrapper.ProductState state = enquiries.get(product.getUid());
        if (null == state) {
            // create state and add
            state = new ProductWrapper.ProductState();
            enquiries.put(product.getUid(), state);
        } else {
            if (null != callback) {
                state.addCallback(callback);
            }
            if(state.getStatus() == 1)
                return;
        }

        state.onEnquiry();
        retrofitRestClient.getApiService().getProductDetailAsync(product.getUrl(), new Callback<Product>() {
            @Override
            public void success(Product prod, Response response) {
                ProductWrapper.ProductState productState = enquiries.get(product.getUid());
                if (null != productState) {
                    if (response.getStatus() == 200) {
                        productState.onDetailsFetched(prod);
                    } else {
                        productState.onDetailsFetchFailed(new Exception("Bad response code:" + response.getStatus()));
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProductWrapper.ProductState productState = enquiries.get(product.getUid());
                if (null != productState) {
                    productState.onDetailsFetchFailed(error);
                }
            }
        });
    }

    public ProductWrapper wrap(Product product) {
        return new ProductWrapper(product, this);
    }
}
