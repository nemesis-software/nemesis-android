package io.nemesis.ninder.logic;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.nemesis.ninder.logic.model.Product;
import io.nemesis.ninder.logic.model.VariantOption;

/**
 * @author Philip
 * @since 2015-11-23
 */
public class NemesisFacadeImpl implements ProductFacade {

    private Context mContext;

    int dummyCount = 0;

    public NemesisFacadeImpl(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public List<Product> getProducts(int size, int page) {
        List<Product> list = new ArrayList<>();

        //dummy
        Product p = new Product();
        p.setName("Test" + dummyCount++);
        list.add(p);
        p = new Product();
        p.setName("Test" + dummyCount++);
        list.add(p);
        p = new Product();
        p.setName("Test" + dummyCount++);
        list.add(p);
        p = new Product();
        p.setName("Test" + dummyCount++);
        list.add(p);
        //

        return list;
    }

    @Override
    public void like(Product product, VariantOption variant) {
        Toast.makeText(mContext, "Like", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dislike(Product product, VariantOption variant) {
        Toast.makeText(mContext, "Dislike", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void addToWishlist(Product product, VariantOption variant) {
        Toast.makeText(mContext, "addToWishlist", Toast.LENGTH_SHORT).show();
    }
}
