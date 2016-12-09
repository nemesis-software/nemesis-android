package io.nemesis.ninder.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.nemesis.ninder.R;
import io.nemesis.ninder.activity.ListActivity;
import io.nemesis.ninder.fragment.ProductFragment;
import io.nemesis.ninder.fragment.RecyclerViewFragment;
import io.nemesis.ninder.logic.ProductWrapper;
import io.nemesis.ninder.logic.model.Product;

/**
 * Created by hristo.stoyanov on 02-Dec-16.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> implements Filterable {
    private List<ProductWrapper> products;
    private List<ProductWrapper> initial_products;
    private Context context;
    private boolean fav_button_pressed = false;

    public RecyclerViewAdapter(Context context, List<ProductWrapper> products) {
        this.context = context;
        this.products = products;
        this.initial_products = products;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
        return new CustomViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final ProductWrapper product = products.get(position);
        if(product.getDiscountedPrice()!=null) {
            holder.product_price.setText(product.getDiscountedPrice().getFormattedValue());
            holder.product_discounted_price.setText(product.getPrice().getFormattedValue());
            holder.product_discounted_price.setPaintFlags(holder.product_discounted_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            holder.product_price.setText(product.getPrice().getFormattedValue());
            holder.product_discounted_price.setText("");
            holder.discount_label.setVisibility(View.GONE);
        }
        if(product.getPhoto()!=null)
        Picasso.with(context).load(product.getPhoto().getUrl()).into(holder.productImage);
        final ImageButton product_fav_button = holder.product_fav_button;
        product_fav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean is_favourite = product.getFavourite();
                if(!is_favourite){
                    product_fav_button.setImageDrawable(context.getDrawable(R.drawable.heart_full));
                    product_fav_button.setImageTintList(context.getResources().getColorStateList(R.color.red));
                }
                else {
                    product_fav_button.setImageDrawable(context.getDrawable(R.drawable.icon_heart));
                    product_fav_button.setImageTintList(null);
                }
                product.setFavourite(!is_favourite);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<ProductWrapper> filtered_results = new ArrayList<>();
                if (constraint != null){
                    if(initial_products !=null & initial_products.size()>0 ){
                        for ( final ProductWrapper p :initial_products) {
                            if (p.getName().toLowerCase().contains(constraint.toString().toLowerCase()))filtered_results.add(p);
                        }
                    }
                    oReturn.values = filtered_results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                products = (List<ProductWrapper>)results.values;
                notifyDataSetChanged();

            }
        };
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView product_price;
        ImageView productImage;
        TextView product_discounted_price;
        ImageButton product_fav_button;
        RelativeLayout discount_label;

        CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            product_price = (TextView) itemView.findViewById(R.id.product_price);
            product_discounted_price = (TextView) itemView.findViewById(R.id.product_discounted_price);
            productImage = (ImageView) itemView.findViewById(R.id.product_image);
            product_fav_button = (ImageButton) itemView.findViewById(R.id.product_fav_button);
            discount_label = (RelativeLayout) itemView.findViewById(R.id.discount_label);
        }

        @Override
        public void onClick(View view) {
            ProductFragment productFragment = new ProductFragment();
            Bundle args = new Bundle();
            args.putParcelable(ProductFragment.EXTRA_ITEM,products.get(getAdapterPosition()).getProduct());
            productFragment.setArguments(args);
            FragmentTransaction transaction =((ListActivity)context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_placeholder, productFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
