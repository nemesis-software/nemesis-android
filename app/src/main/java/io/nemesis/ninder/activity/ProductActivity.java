package io.nemesis.ninder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import io.nemesis.ninder.NinderApplication;
import io.nemesis.ninder.R;
import io.nemesis.ninder.adapter.GalleryPageAdapter;
import io.nemesis.ninder.logic.NemesisFacadeImpl;
import io.nemesis.ninder.logic.ProductFacade;
import io.nemesis.ninder.logic.ProductWrapper;
import io.nemesis.ninder.logic.model.Product;

public class ProductActivity extends Activity {

    public static final String EXTRA_ITEM = "item";

    private ImageButton btnCheckmark;
    private GalleryPageAdapter galleryPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        btnCheckmark = (ImageButton) findViewById(R.id.button_checkmark);
        btnCheckmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Product product = (Product)getIntent().getParcelableExtra(EXTRA_ITEM);
        if (product != null) {
            ProductFacade productFacade = ((NinderApplication) getApplication()).getProductFacade();
            if (productFacade instanceof NemesisFacadeImpl) {
                final ProductWrapper wrapped = ((NemesisFacadeImpl) productFacade).wrap(product);
                wrapped.enquireDetails(new ProductFacade.EnquiryCallback() {
                    @Override
                    public void onSuccess(Product products) {
                        initProductView(wrapped);
                    }

                    @Override
                    public void onFail(Exception e) {
                        // show notification
                    }
                });
            } else {
                initProductView(product);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // init activity_product with selected product
    private void initProductView(Product product) {
        galleryPageAdapter = new GalleryPageAdapter(getFragmentManager(), product.getImages());

        ViewPager productViewPager = (ViewPager) findViewById(R.id.pager);
        productViewPager.setAdapter(galleryPageAdapter);

        TextView productNameView = (TextView) findViewById(R.id.product_name);
        productNameView.setText(product.getName());

        TextView productSubNameView = (TextView) findViewById(R.id.product_sub_name);
        productSubNameView.setText(product.getVariantType());

        TextView productPriceView = (TextView) findViewById(R.id.product_price);
        productPriceView.setText(product.getPrice().getFormattedValue());

        if (product.getDiscountedPrice() != null) {
            TextView productDiscountedPriceView = (TextView) findViewById(R.id.product_discounted_price);
            String sale = getString(R.string.label_sale) + " " + product.getDiscountedPrice().getFormattedValue();
            productDiscountedPriceView.setText(sale);
        }

        if (product.getAverageRating() != null) {
            TextView productRatingView = (TextView) findViewById(R.id.rating);
            productRatingView.setText(product.getAverageRating().toString());
        }

        TextView productDescriptionView = (TextView) findViewById(R.id.product_description);
        productDescriptionView.setText(Html.fromHtml(product.getDescription()));
    }

    // init activity_product with selected product
    private void initProductView(ProductWrapper product) {
        galleryPageAdapter = new GalleryPageAdapter(getFragmentManager(), product.getGalleryImages());

        ViewPager productViewPager = (ViewPager) findViewById(R.id.pager);
        productViewPager.setAdapter(galleryPageAdapter);

        TextView productNameView = (TextView) findViewById(R.id.product_name);
        productNameView.setText(product.getName());

        TextView productSubNameView = (TextView) findViewById(R.id.product_sub_name);
        productSubNameView.setText(product.getVariantType());

        TextView productPriceView = (TextView) findViewById(R.id.product_price);
        productPriceView.setText(product.getPrice().getFormattedValue());

        if (product.getDiscountedPrice() != null) {
            TextView productDiscountedPriceView = (TextView) findViewById(R.id.product_discounted_price);
            String sale = getString(R.string.label_sale) + " " + product.getDiscountedPrice().getFormattedValue();
            productDiscountedPriceView.setText(sale);
        }

        TextView productDescriptionView = (TextView) findViewById(R.id.product_description);
        productDescriptionView.setText(Html.fromHtml(product.getDescription()));
    }
}
