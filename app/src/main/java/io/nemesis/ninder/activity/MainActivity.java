package io.nemesis.ninder.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
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
import io.nemesis.ninder.logic.model.Product;

public class MainActivity extends Activity {
    private SwipeFlingAdapterView flingContainer;
    private CardAdapter mAdapter;
    private TextView productNameTextView;
    private TextView productCategoryTextView;
    private TextView noDataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // text info
        productNameTextView = (TextView) findViewById(R.id.product_item_name);
        productCategoryTextView = (TextView) findViewById(R.id.product_item_sub_name);
        noDataTextView = (TextView) findViewById(R.id.noDataTextViewId);

        //add the view via xml or programmatically
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.swipecards);
        flingContainer.bringToFront();

        final View textContainerView = findViewById(R.id.titles_container);
        textContainerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                flingContainer.setPadding(
                        flingContainer.getPaddingLeft(), flingContainer.getPaddingTop(),
                        flingContainer.getPaddingRight(), flingContainer.getHeight() - textContainerView.getTop()
                );
            }
        });

        showNoDataMessage(true);

        mAdapter = new CardAdapter();
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

                    findViewById(R.id.product_item_name).setAlpha(alpha);
                    findViewById(R.id.product_item_sub_name).setAlpha(alpha);
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

        ImageButton btnNope = (ImageButton) findViewById(R.id.button_nope);
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

        ImageButton btnLike = (ImageButton) findViewById(R.id.button_like);
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

        ImageButton btnInfo = (ImageButton) findViewById(R.id.button_info);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info();
            }
        });
    }

    private void showNoDataMessage(boolean noData) {
        noDataTextView.setVisibility(noData ? View.VISIBLE : View.INVISIBLE);
        flingContainer.setVisibility(noData ? View.INVISIBLE : View.VISIBLE);
        productNameTextView.setVisibility(noData ? View.INVISIBLE : View.VISIBLE);
        productCategoryTextView.setVisibility(noData ? View.INVISIBLE : View.VISIBLE);
    }

    private void dislike(ProductWrapper product) {
        ((NinderApplication) getApplication()).getProductFacade().dislike(product.getProduct(), null);
    }

    private void info() {
        // XXX view info for the top item in the queue
        // TODO: 11/27/15 Product wrapper
        if (!mAdapter.isEmpty()) {
            Product item = mAdapter.getItem(0).getProduct();
            Intent intent = new Intent(this, ProductActivity.class);
            intent.putExtra(ProductActivity.EXTRA_ITEM, item);

            //create transition
            ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(this, flingContainer, getString(R.string.transition_name));

            startActivity(intent, options.toBundle());
        } else {
            TLog.d("No content available to show info");
        }
    }

    private void like(ProductWrapper product) {
        ((NinderApplication) getApplication()).getProductFacade().like(product.getProduct());
    }

    private class CardAdapter extends BaseAdapter {

        final ArrayList<ProductWrapper> list = new ArrayList<>();
        private int batchNumber = 0;
        private final int batchSize = 10;
        boolean endOfQueueReached = false;

        public CardAdapter() {
            addMoreData();
        }

        public void addMoreData() {
            //TODO update the size and the page
            ((NinderApplication) getApplication()).getProductFacade().getProductsAsync(batchSize, batchNumber,
                    new ProductFacade.AsyncCallback() {
                        @Override
                        public void onSuccess(final List<ProductWrapper> products) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    list.addAll(products);
                                    notifyDataSetChanged();
                                    batchNumber++;
                                }
                            });
                        }

                        @Override
                        public void onFail(final Exception e) {
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
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                rowView = inflater.inflate(R.layout.item_card, parent, false);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.image = (ImageView) rowView.findViewById(R.id.image_product);

                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            ProductWrapper item = getItem(position);

            Picasso picasso = Picasso.with(getApplicationContext());
            picasso.cancelRequest(holder.image);

            // XXX from mail conversations we know the initial call
            // will return two images and the large one will be second
            // TODO: 11/26/15 iterate the images and find the one we need in case the model changes

            picasso.load(item.getPhoto().getUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.image_err_placeholder)
                    .into(holder.image);

            return rowView;
        }

        class ViewHolder {
            public ImageView image;
        }
    }
}
