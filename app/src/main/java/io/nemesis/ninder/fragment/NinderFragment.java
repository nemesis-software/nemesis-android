package io.nemesis.ninder.fragment;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import io.nemesis.ninder.NinderApplication;
import io.nemesis.ninder.R;
import io.nemesis.ninder.activity.ProductActivity;
import io.nemesis.ninder.adapter.CardAdapter;
import io.nemesis.ninder.logger.TLog;
import io.nemesis.ninder.logic.ProductWrapper;
import io.nemesis.ninder.model.Product;

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
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_ninder, container, false);

        // text info
        productNameTextView = (TextView) rootView.findViewById(R.id.product_item_name);
        productCategoryTextView = (TextView) rootView.findViewById(R.id.product_item_sub_name);
        noDataTextView = (TextView) rootView.findViewById(R.id.noDataTextViewId);

        //add the view via xml or programmatically
        flingContainer = (SwipeFlingAdapterView) rootView.findViewById(R.id.swipecards);
        flingContainer.bringToFront();
        flingContainer.refreshDrawableState();

        //final View textContainerView = rootView.findViewById(R.id.titles_container);
//        textContainerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                flingContainer.setPadding(
//                        flingContainer.getPaddingLeft(), flingContainer.getPaddingTop(),
//                        flingContainer.getPaddingRight(), flingContainer.getHeight() - textContainerView.getTop()
//                );
//                Log.d("Padding",""+flingContainer.getPaddingBottom()+flingContainer.getPaddingRight()+flingContainer.getPaddingLeft()+flingContainer.getPaddingBottom());
//            }
//        });
        mAdapter = new CardAdapter(getActivity());
        //showNoDataMessage(false);
        if (!mAdapter.isEmpty()) {
            showNoDataMessage(false);

            ProductWrapper item = mAdapter.getItem(0);
            productNameTextView.setText(item.getName());
            productCategoryTextView.setText(item.getCategory());

            productCategoryTextView.setAlpha(1.0f);
            productNameTextView.setAlpha(1.0f);
        }
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
        if (mAdapter != null && !mAdapter.isEndOfQueueReached()) {
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
            Intent intent = new Intent(getContext(), ProductActivity.class);
            intent.putExtra(ProductActivity.EXTRA_ITEM, item);
            startActivity(intent);
        } else {
            TLog.d("No content available to show info");
        }
    }
    private void like(ProductWrapper product) {
        ((NinderApplication) getActivity().getApplication()).getProductFacade().like(product);
    }


}
