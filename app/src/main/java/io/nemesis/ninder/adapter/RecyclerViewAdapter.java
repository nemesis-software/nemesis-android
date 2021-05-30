package io.nemesis.ninder.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import androidx.recyclerview.widget.RecyclerView;
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
import io.nemesis.ninder.activity.ProductActivity;
import io.nemesis.ninder.logic.ProductWrapper;
import io.nemesis.ninder.model.Product;

/**
 * Created by hristo.stoyanov on 02-Dec-16.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> implements Filterable {
    private List<ProductWrapper> products;
    private List<ProductWrapper> initial_products;
    private Context context;
    private ProductWrapper product;

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
    public void onBindViewHolder(final CustomViewHolder holder,int position) {
        //holder.setIsRecyclable(false);
        product = products.get(position);
        if(product.getDiscountedPrice()!=null) {
            holder.product_price.setText(product.getDiscountedPrice().getFormattedValue());
            holder.product_discounted_price.setText(product.getPrice().getFormattedValue());
            holder.product_discounted_price.setPaintFlags(holder.product_discounted_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.discount_label.setVisibility(View.VISIBLE);
        }
        else {
            holder.product_price.setText(product.getPrice().getFormattedValue());
            holder.product_discounted_price.setText("");
            holder.discount_label.setVisibility(View.INVISIBLE);
        }
        if(product.getPhoto()!=null)
        Picasso.with(context).load(product.getPhoto().getUrl()).into(holder.productImage);
        ToggleFavImage(holder.product_fav_button,product.getProduct().getFavourite());

        holder.product_fav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton fav_button = (ImageButton) view;
                Product current = products.get(holder.getAdapterPosition()).getProduct();
                boolean fav = current.getFavourite();
                ToggleFavImage(fav_button, !fav);
                current.setFavourite(!fav);
            }
        });

    }
    private void ToggleFavImage(ImageButton fav_button, boolean is_fav){
        if(is_fav){
            fav_button.setImageDrawable(context.getDrawable(R.drawable.heart_full));
            fav_button.setImageTintList(context.getResources().getColorStateList(R.color.red));
        }
        else {
            fav_button.setImageDrawable(context.getDrawable(R.drawable.icon_heart));
            fav_button.setImageTintList(null);
        }
    }

    @Override
    public int getItemCount() {
        if(products!=null)
        return products.size();
        else return 0;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<ProductWrapper> filtered_results = new ArrayList<>();
                if (constraint != null){
                    assert initial_products != null;
                    if(initial_products.size()>0 ){
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
    public void ClearFilter(){
        products = initial_products;
        notifyDataSetChanged();
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
            Intent new_intent = new Intent(context, ProductActivity.class);
            new_intent.putExtra(ProductActivity.EXTRA_ITEM,products.get(getAdapterPosition()).getProduct());
            context.startActivity(new_intent);
        }
    }
}
