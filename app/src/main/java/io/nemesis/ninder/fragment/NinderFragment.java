package io.nemesis.ninder.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.nemesis.ninder.NinderApplication;
import io.nemesis.ninder.R;
import io.nemesis.ninder.logger.TLog;
import io.nemesis.ninder.logic.ProductFacade;
import io.nemesis.ninder.logic.ProductWrapper;
import io.nemesis.ninder.logic.model.Image;
import io.nemesis.ninder.logic.model.Product;

public class NinderFragment extends Fragment {
    private SwipeFlingAdapterView flingContainer;
    private CardAdapter mAdapter;
    private TextView productNameTextView;
    private TextView productCategoryTextView;
    private TextView noDataTextView;

    public NinderFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_main, container, false);

        // text info
        productNameTextView = (TextView) rootView.findViewById(R.id.product_item_name);
        productCategoryTextView = (TextView) rootView.findViewById(R.id.product_item_sub_name);
        noDataTextView = (TextView) rootView.findViewById(R.id.noDataTextViewId);

        //add the view via xml or programmatically
        flingContainer = (SwipeFlingAdapterView) rootView.findViewById(R.id.swipecards);
        flingContainer.bringToFront();

        final View textContainerView = rootView.findViewById(R.id.titles_container);
        textContainerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                flingContainer.setPadding(
                        flingContainer.getPaddingLeft(), flingContainer.getPaddingTop(),
                        flingContainer.getPaddingRight(), flingContainer.getHeight() - textContainerView.getTop()
                );
            }
        });

        mAdapter = new CardAdapter();
        showNoDataMessage(true);
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                if (!mAdapter.isEmpty()) {
                    showNoDataMessage(false);

                    ProductWrapper item = mAdapter.getItem(0);
                    productNameTextView.setText(item.getName());
                    productCategoryTextView.setText(item.getCategory());

                    productCategoryTextView.setAlpha(1.0f);
                    productNameTextView.setAlpha(1.0f);
                } else {
                    showNoDataMessage(true);
                }
            }

            @Override
            public void onInvalidated() {

            }
        });

        //set the listener and the adapter
        flingContainer.setAdapter(mAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                mAdapter.pop();
                if (!mAdapter.isEmpty()) {
                    showNoDataMessage(false);
                    ProductWrapper item = mAdapter.getItem(0);

                    productNameTextView.setText(item.getName());
                    productCategoryTextView.setText(item.getCategory());

                    productCategoryTextView.setAlpha(1.0f);
                    productNameTextView.setAlpha(1.0f);
                } else {
                    showNoDataMessage(true);
                }
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                if (dataObject instanceof ProductWrapper) {
                    dislike(((ProductWrapper) dataObject));
                }
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                if (dataObject instanceof ProductWrapper) {
                    like(((ProductWrapper) dataObject));
                }
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                if (itemsInAdapter < 6 && !mAdapter.isEndOfQueueReached()) // min adapter stack from SwipeFlingAdapterView configuration
                    mAdapter.addMoreData();
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                // scrollProgressPercent -> between [-1, 1] -> -1 left, 0 center, 1 right
                View view = flingContainer.getSelectedView();

                if (view != null) {
                    // 0 - the view is completely transparent
                    // 1 - the view is completely opaque

                    float alpha = 1 - Math.abs(scrollProgressPercent);

                    rootView.findViewById(R.id.product_item_name).setAlpha(alpha);
                    rootView.findViewById(R.id.product_item_sub_name).setAlpha(alpha);
                }
            }
        });

        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                if (flingContainer.isEnabled()) {
                    info();
                }
            }
        });

        ImageButton btnNope = (ImageButton) rootView.findViewById(R.id.button_nope);
        btnNope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flingContainer.getSelectedView() != null) {
                    flingContainer.getTopCardListener().selectLeft();
                } else {
                    TLog.d("no active product view, cannot dislike");
                }
            }
        });

        ImageButton btnLike = (ImageButton) rootView.findViewById(R.id.button_like);
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flingContainer.getSelectedView() != null) {
                    flingContainer.getTopCardListener().selectRight();
                } else {
                    TLog.d("no active product view, cannot like");
                }
            }
        });

        ImageButton btnInfo = (ImageButton) rootView.findViewById(R.id.button_info);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info();
            }
        });
        return rootView;
    }

    private void showNoDataMessage(boolean noData) {
        noDataTextView.setVisibility(noData ? View.VISIBLE : View.INVISIBLE);
        if (mAdapter != null && !mAdapter.endOfQueueReached) {
            noDataTextView.setText(this.getString(R.string.loading_products));
        } else {
            noDataTextView.setText(this.getString(R.string.end_of_product_queue_notification_text));
        }
        flingContainer.setVisibility(noData ? View.INVISIBLE : View.VISIBLE);
        productNameTextView.setVisibility(noData ? View.INVISIBLE : View.VISIBLE);
        productCategoryTextView.setVisibility(noData ? View.INVISIBLE : View.VISIBLE);
    }

    private void dislike(ProductWrapper product) {
        ((NinderApplication) getActivity().getApplication()).getProductFacade().dislike(product, null);
    }

    private void info() {
        // XXX view info for the top item in the queue
        if (!mAdapter.isEmpty()) {
            Product item = mAdapter.getItem(0).getProduct();
            // TODO: Fragment
//            Intent intent = new Intent(this, ProductActivity.class);
//            intent.putExtra(ProductActivity.EXTRA_ITEM, item);
//
//            //create transition
//            ActivityOptions options = ActivityOptions.
//                    makeSceneTransitionAnimation(this, flingContainer, getString(R.string.transition_name));
//
//            startActivity(intent, options.toBundle());
        } else {
            TLog.d("No content available to show info");
        }
    }

    private void like(ProductWrapper product) {
        ((NinderApplication) getActivity().getApplication()).getProductFacade().like(product);
    }

    private class CardAdapter extends BaseAdapter {

        final ArrayList<ProductWrapper> list = new ArrayList<>();
        private int batchNumber = 0;
        private final int batchSize = 10;
        private volatile boolean endOfQueueReached = false;

        public CardAdapter() {
            addMoreData();
        }

        public void addMoreData() {
            //TODO update the size and the page
            ((NinderApplication) getActivity().getApplication()).getProductFacade().getProductsAsync(batchSize, batchNumber,
                    new ProductFacade.AsyncCallback<ProductWrapper>() {
                        @Override
                        public void onSuccess(final List<ProductWrapper> products) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    list.addAll(products);
                                    if (list.isEmpty()) {
                                        endOfQueueReached = true;
                                    } else {
                                        endOfQueueReached = false;
                                    }
                                    notifyDataSetChanged();

                                    batchNumber++;
                                }
                            });
                        }

                        @Override
                        public void onFail(final Exception e) {
                            // on any exception show, no data message -> handles connectivity errors
                            endOfQueueReached = true;
                            if (list.isEmpty()) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showNoDataMessage(true);
                                    }
                                });
                            }
                        }
                    });
        }

        public boolean isEndOfQueueReached() {
            return endOfQueueReached;
        }

        public void pop() {
            //remove first
            list.remove(0);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public ProductWrapper getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                rowView = inflater.inflate(R.layout.item_card, parent, false);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.image = (ImageView) rowView.findViewById(R.id.image_product);

                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            ProductWrapper item = getItem(position);

            Picasso picasso = Picasso.with(getContext());
            picasso.cancelRequest(holder.image);

            // XXX from mail conversations we know the initial call
            // will return two images and the large one will be second
            // TODO: 11/26/15 iterate the images and find the one we need in case the model changes

            Image photo = item.getPhoto();
            if (photo != null && !TextUtils.isEmpty(photo.getUrl())) {
                picasso.load(photo.getUrl())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.image_err_placeholder)
                        .into(holder.image);
            } else {
                holder.image.setImageResource(R.drawable.placeholder);
            }

            return rowView;
        }

        class ViewHolder {
            public ImageView image;
        }
    }
}
