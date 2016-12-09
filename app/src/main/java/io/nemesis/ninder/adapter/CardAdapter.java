package io.nemesis.ninder.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.nemesis.ninder.NinderApplication;
import io.nemesis.ninder.R;
import io.nemesis.ninder.logic.ProductFacade;
import io.nemesis.ninder.logic.ProductWrapper;
import io.nemesis.ninder.logic.model.Image;

/**
 * Created by hristo.stoyanov on 09-Dec-16.
 */

public class CardAdapter extends BaseAdapter {

    private static ArrayList<ProductWrapper> list;
    private int batchNumber = 0;
    private final int batchSize = 10;
    private volatile boolean endOfQueueReached = false;
    private Activity mActivity;

    public CardAdapter(Activity activity) {
        this.mActivity = activity;
        if(list==null)
        list = new ArrayList<>();
    }
    public void addMoreData() {

        //TODO update the size and the page
        ((NinderApplication) mActivity.getApplication()).getProductFacade().getProductsAsync(batchSize, batchNumber,
                new ProductFacade.AsyncCallback<ProductWrapper>() {
                    @Override
                    public void onSuccess(final List<ProductWrapper> products) {
                        mActivity.runOnUiThread(new Runnable() {
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
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //showNoDataMessage(true);
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
            LayoutInflater inflater = mActivity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_card, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) rowView.findViewById(R.id.image_product);

            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        ProductWrapper item = getItem(position);

        Picasso picasso = Picasso.with(mActivity);
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