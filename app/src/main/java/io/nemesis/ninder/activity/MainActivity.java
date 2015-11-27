package io.nemesis.ninder.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.nemesis.ninder.NinderApplication;
import io.nemesis.ninder.R;
import io.nemesis.ninder.logic.ProductFacade;
import io.nemesis.ninder.logic.ProductWrapper;
import io.nemesis.ninder.logic.model.Product;

public class MainActivity extends Activity {
    private SwipeFlingAdapterView flingContainer;
    private CardAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(false);
        }

        //add the view via xml or programmatically
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.swipecards);
        flingContainer.bringToFront();

        mAdapter = new CardAdapter();

        //set the listener and the adapter
        flingContainer.setAdapter(mAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                mAdapter.pop();
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
                if (flingContainer.isEnabled())
                    info();
            }
        });

        ImageButton btnNope = (ImageButton) findViewById(R.id.button_nope);
        btnNope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flingContainer.getTopCardListener().selectLeft();
            }
        });

        ImageButton btnLike = (ImageButton) findViewById(R.id.button_like);
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flingContainer.getTopCardListener().selectRight();
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

    private void dislike(ProductWrapper product) {
        ((NinderApplication) getApplication()).getProductFacade().dislike(product.getProduct(), null);
    }

    private void info() {
        // XXX view info for the top item in the queue
        // TODO: 11/27/15 Product wrapper
        Product item = mAdapter.getItem(0).getProduct();
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra(ProductActivity.EXTRA_ITEM, item);

        //create transition
        ActivityOptions options = ActivityOptions.
                makeSceneTransitionAnimation(this, flingContainer, getString(R.string.transition_name));

        startActivity(intent, options.toBundle());
    }

    private void like(ProductWrapper product) {
        ((NinderApplication) getApplication()).getProductFacade().like(product.getProduct(), null);
    }

    private class CardAdapter extends BaseAdapter {

        final ArrayList<ProductWrapper> list = new ArrayList<>();
        private int badgeNumber = 0;
        private final int badgeSize = 10;
        boolean endOfQueueReached = false;

        public CardAdapter() {
            addMoreData();
        }

        public void addMoreData() {
            //TODO update the size and the page
            ((NinderApplication) getApplication()).getProductFacade().getProductsAsync(badgeSize, badgeNumber,
                    new ProductFacade.AsyncCallback() {
                        @Override
                        public void onSuccess(final List<ProductWrapper> products) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    list.addAll(products);
                                    notifyDataSetChanged();
                                    badgeNumber++;
                                }
                            });
//                            list.addAll(products);
//                            notifyDataSetChanged();
//                            badgeNumber++;
                        }

                        @Override
                        public void onFail(final Exception e) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (e instanceof ProductFacade.EndOfQueueException) {
                                        endOfQueueReached = true;
                                        flingContainer.setEnabled(false);
                                    }
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
//                            if (e instanceof ProductFacade.EndOfQueueException) {
//                                endOfQueueReached = true;
//                                flingContainer.setEnabled(false);
//                            }
//                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
                viewHolder.product_subname = (TextView) findViewById(R.id.product_item_sub_name);
                viewHolder.product_itemname = (TextView) findViewById(R.id.product_item_name);
//                viewHolder.label_like = rowView.findViewById(R.id.item_swipe_like_indicator);
//                viewHolder.label_dislike = rowView.findViewById(R.id.item_swipe_dislike_indicator);
                viewHolder.image = (ImageView) rowView.findViewById(R.id.image_product);

                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            ProductWrapper item = getItem(position);
            holder.product_itemname.setText(item.getName());
            holder.product_itemname.setAlpha(new Float(100));
            holder.product_subname.setText(item.getVariantType());
            holder.product_subname.setAlpha(new Float(100));

            Picasso picasso = Picasso.with(getApplicationContext());
            picasso.cancelRequest(holder.image);

            // XXX from mail conversations we know the initial call
            // will return two images and the large one will be second
            // TODO: 11/26/15 iterate the images and find the one we need in case the model changes

            picasso.load(item.getPhoto().getUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.image_err_placeholder)
                    .into(holder.image);

            //reset old data
//            holder.label_like.setAlpha(0f);
//            holder.label_dislike.setAlpha(0f);

            return rowView;
        }

        class ViewHolder {
            public TextView product_subname;
            public TextView product_itemname;
            public View label_like;
            public View label_dislike;
            public ImageView image;
        }
    }
}
