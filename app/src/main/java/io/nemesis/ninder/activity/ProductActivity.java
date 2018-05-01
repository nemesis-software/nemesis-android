package io.nemesis.ninder.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.robohorse.pagerbullet.PagerBullet;

import java.util.List;

import io.nemesis.ninder.NinderApplication;
import io.nemesis.ninder.R;
import io.nemesis.ninder.adapter.GalleryPageAdapter;
import io.nemesis.ninder.logger.TLog;
import io.nemesis.ninder.logic.NemesisFacadeImpl;
import io.nemesis.ninder.logic.ProductFacade;
import io.nemesis.ninder.logic.ProductWrapper;
import io.nemesis.ninder.model.Image;
import io.nemesis.ninder.model.Product;
import io.nemesis.ninder.model.ProductEntity;

public class ProductActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM;
    private Toolbar mToolbar;

    static {
        String paramPrefix = ProductActivity.class.getName();
        EXTRA_ITEM = String.format("%s:%s", paramPrefix, Product.class.getSimpleName()).toUpperCase();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        InitializeToolbar();
    }
    private void InitializeToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setTitle(R.string.product_details);
        setSupportActionBar(mToolbar);
    }

    @Override
    public void onStart() {
        super.onStart();
        Product product = getIntent().getParcelableExtra(EXTRA_ITEM);
        if (product != null) {
//            Answers.getInstance().logContentView(new ContentViewEvent()
//                    .putContentName(product.getName())
//                    .putContentId(product.getUrl())
//            );

            ProductFacade productFacade = ((NinderApplication) getApplication()).getProductFacade();
            if (productFacade instanceof NemesisFacadeImpl) {
                final ProductWrapper wrapped = ((NemesisFacadeImpl) productFacade).wrap(product);
                if (wrapped.hasDetails()) {
                    initProductView(wrapped);
                } else {
                    wrapped.enquireDetails(new ProductFacade.EnquiryCallback() {
                        @Override
                        public void onSuccess(Product products) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                initProductView(wrapped);
                                }
                            });
                        }

                        @Override
                        public void onFail(Exception e) {
                            // show notification
                        }
                    });
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // init activity_product with selected product
    private void initProductView(ProductWrapper product) {
        List<Image> galleryImages = product.getGalleryImages();
        GalleryPageAdapter galleryPageAdapter = new GalleryPageAdapter(getSupportFragmentManager(), product.getGalleryImages());
        PagerBullet productViewPager = (PagerBullet) findViewById(R.id.pager);
        productViewPager.setAdapter(galleryPageAdapter);
        productViewPager.setTextSeparatorOffset(5);
        productViewPager.setIndicatorTintColorScheme(getResources().getColor(R.color.red), getResources().getColor(R.color.grey));
        if(galleryPageAdapter.getCount()<2) productViewPager.setIndicatorVisibility(false);

        TextView productNameView = (TextView) findViewById(R.id.product_name);
        productNameView.setText(product.getName());

        TextView productSubNameView = (TextView) findViewById(R.id.product_sub_name);
        productSubNameView.setText(product.getCategory());

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
