package io.nemesis.ninder.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

import io.nemesis.ninder.NinderApplication;
import io.nemesis.ninder.R;
import io.nemesis.ninder.logic.model.Product;

public class MainActivity extends Activity {


    private ImageButton btnNope;
    private ImageButton btnLike;
    private ImageButton btnInfo;
    private SwipeFlingAdapterView flingContainer;
    private CardAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add the view via xml or programmatically
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.swipecards);

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
                dislike();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                like();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                mAdapter.addMoreData();
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                if (view != null) {
                    view.findViewById(R.id.item_swipe_dislike_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                    view.findViewById(R.id.item_swipe_like_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
                }
            }
        });

        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                info();
            }
        });


        btnNope = (ImageButton) findViewById(R.id.button_nope);
        btnNope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flingContainer.getTopCardListener().selectLeft();
            }
        });

        btnLike = (ImageButton) findViewById(R.id.button_like);
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flingContainer.getTopCardListener().selectRight();
            }
        });

        btnInfo = (ImageButton) findViewById(R.id.button_info);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void dislike() {
        ((NinderApplication) getApplication()).getProductFacade().dislike(null, null);
    }

    private void info() {
        Intent intent = new Intent(this, ProductActivity.class);
        startActivity(intent);
    }

    private void like() {
        ((NinderApplication) getApplication()).getProductFacade().like(null, null);
    }

    private class CardAdapter extends BaseAdapter {

        ArrayList<Product> list = new ArrayList<Product>();

        public CardAdapter() {
            addMoreData();
        }

        public void addMoreData() {
            //TODO update the size and the page
            list.addAll(((NinderApplication) getApplication()).getProductFacade().getProducts(10, 0));
            notifyDataSetChanged();
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
        public Product getItem(int position) {
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
                viewHolder.text = (TextView) rowView.findViewById(R.id.label_product);
                viewHolder.label_like = rowView.findViewById(R.id.item_swipe_like_indicator);
                viewHolder.label_dislike = rowView.findViewById(R.id.item_swipe_dislike_indicator);

                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            String s = getItem(position).getName();
            holder.text.setText(s);
            //reset old data
            holder.label_like.setAlpha(0f);
            holder.label_dislike.setAlpha(0f);


            return rowView;
        }

        class ViewHolder {
            public TextView text;
            public View label_like;
            public View label_dislike;
        }
    }
}
