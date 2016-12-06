package io.nemesis.ninder.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.nemesis.ninder.R;
import io.nemesis.ninder.logic.ProductWrapper;

/**
 * Created by hristo.stoyanov on 02-Dec-16.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>{
    private List<ProductWrapper> products;
    private Context context;

    public RecyclerViewAdapter(Context context, List<ProductWrapper> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
        return new CustomViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.productName.setText(products.get(position).getName());
        if(products.get(position).getPhoto()!=null)
        Picasso.with(context).load(products.get(position).getPhoto().getUrl()).into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView productName;
        ImageView productImage;

        CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            productImage = (ImageView) itemView.findViewById(R.id.product_image);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "Clicked Position = " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
        }
    }
}
