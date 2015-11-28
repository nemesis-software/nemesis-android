package io.nemesis.ninder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import java.util.List;

import io.nemesis.ninder.NinderApplication;
import io.nemesis.ninder.R;
import io.nemesis.ninder.adapter.GalleryPageAdapter;
import io.nemesis.ninder.logger.TLog;
import io.nemesis.ninder.logic.NemesisFacadeImpl;
import io.nemesis.ninder.logic.ProductFacade;
import io.nemesis.ninder.logic.ProductWrapper;
import io.nemesis.ninder.logic.model.Image;
import io.nemesis.ninder.logic.model.Product;

public class ProductActivity extends Activity {
    public static final String EXTRA_ITEM;

    static {
        String paramPrefix = ProductActivity.class.getName();

        EXTRA_ITEM = String.format("%s:%s", paramPrefix, Product.class.getSimpleName()).toUpperCase();
    }

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
                finishAfterTransition();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Product product = getIntent().getParcelableExtra(EXTRA_ITEM);
        if (product != null) {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName(product.getName())
                    .putContentId(product.getUrl())
            );

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finishAfterTransition();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // init activity_product with selected product
    private void initProductView(ProductWrapper product) {
        List<Image> galleryImages = product.getGalleryImages();
        TLog.d("initProductView:galleryImages.size(): " + galleryImages.size());
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
