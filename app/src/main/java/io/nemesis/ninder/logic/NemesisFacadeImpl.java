package io.nemesis.ninder.logic;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.nemesis.ninder.R;
import io.nemesis.ninder.logger.TLog;
import io.nemesis.ninder.logic.model.Product;
import io.nemesis.ninder.logic.model.VariantOption;
import io.nemesis.ninder.logic.rest.NemesisRetrofitRestClient;
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

    @Deprecated
    /**
     * only Use {@link #getProductsAsync(int, int, AsyncCallback)}
     *
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
    public void like(Product product) {
        addToWishlist(product);
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
    public void addToWishlist(final Product product) {
        if (product == null) {
            throw new IllegalArgumentException();
        }

        TLog.d("addToWishlist: " + product.getUrl());

        List<VariantOption> variantOptions = product.getVariantOptions();
        VariantOption variant = variantOptions != null && !variantOptions.isEmpty() ? variantOptions.get(0) : null;

        if (variant != null) {
            addToWishList(variant);
        } else {
            enquireAsync(product, new EnquiryCallback() {
                @Override
                public void onSuccess(Product prod) {
                    List<VariantOption> variantOptions = prod.getVariantOptions();
                    VariantOption variant = variantOptions != null && !variantOptions.isEmpty() ? variantOptions.get(0) : null;
                    if (variant != null) {
                        addToWishList(variant);
                    } else {
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

    private void addToWishList(final VariantOption variant) {
        if (variant == null) {
            throw new IllegalArgumentException();
        }

        final String code = variant.getCode();
        if (TextUtils.isEmpty(code)) {
            TLog.w("variant code isEmpty. variant=" + variant);
            return;
        }

        retrofitRestClient.getApiService().addToWishlistAsync(code, testUserId, new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                TLog.d("added to wishlist");
                Answers.getInstance().logAddToCart(new AddToCartEvent()
                        .putItemId(variant.getCode())
                );
            }

            @Override
            public void failure(RetrofitError error) {
                // notify
                TLog.e("cannot add to wishlist:", error);
            }
        });
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
                } else {
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProductWrapper.ProductState productState = enquiries.get(product.getUid());
                if (null != productState) {
                    productState.onDetailsFetchFailed(error);
                } else {
                }
            }
        });
    }

    public ProductWrapper wrap(Product product) {
        return new ProductWrapper(product, this);
    }
}
