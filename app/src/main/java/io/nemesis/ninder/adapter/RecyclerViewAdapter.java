package io.nemesis.ninder.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.nemesis.ninder.R;
import io.nemesis.ninder.logic.ProductWrapper;
import io.nemesis.ninder.logic.model.Price;

/**
 * Created by hristo.stoyanov on 02-Dec-16.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> implements Filterable {
    private List<ProductWrapper> products;
    private List<ProductWrapper> initial_products;
    private Context context;

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
        holder.product_price.setText(products.get(position).getPrice().getFormattedValue());
        if(products.get(position).getDiscountedPrice()!=null) {
            holder.product_discounted_price.setText(products.get(position).getDiscountedPrice().getFormattedValue());
            holder.product_discounted_price.setPaintFlags(holder.product_discounted_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else holder.product_discounted_price.setText("");
        if(products.get(position).getPhoto()!=null)
        Picasso.with(context).load(products.get(position).getPhoto().getUrl()).into(holder.productImage);
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
                            if (p.getName().toLowerCase().contains(constraint.toString()))filtered_results.add(p);
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

        CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            product_price = (TextView) itemView.findViewById(R.id.product_price);
            product_discounted_price = (TextView) itemView.findViewById(R.id.product_discounted_price);
            productImage = (ImageView) itemView.findViewById(R.id.product_image);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "Clicked Position = " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
        }
    }
}
