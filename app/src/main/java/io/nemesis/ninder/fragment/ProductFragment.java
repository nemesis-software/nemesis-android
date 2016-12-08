package io.nemesis.ninder.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.robohorse.pagerbullet.PagerBullet;

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
import io.nemesis.ninder.logic.model.ProductEntity;

public class ProductFragment extends Fragment {

    public ProductFragment(){
    }

    public static final String EXTRA_ITEM;

    static {
        String paramPrefix = ProductFragment.class.getName();
        EXTRA_ITEM = String.format("%s:%s", paramPrefix, Product.class.getSimpleName()).toUpperCase();
    }

    private ImageButton btnCheckmark;
    private GalleryPageAdapter galleryPageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();

        Product product = getArguments().getParcelable(EXTRA_ITEM);
        if (product != null) {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName(product.getName())
                    .putContentId(product.getUrl())
            );

            ProductFacade productFacade = ((NinderApplication) getActivity().getApplication()).getProductFacade();
            if (productFacade instanceof NemesisFacadeImpl) {
                final ProductWrapper wrapped = ((NemesisFacadeImpl) productFacade).wrap(product);
                if (wrapped.hasDetails()) {
                    initProductView(wrapped);
                } else {
                    wrapped.enquireDetails(new ProductFacade.EnquiryCallback() {
                        @Override
                        public void onSuccess(ProductEntity products) {
                            getActivity().runOnUiThread(new Runnable() {
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

    // init fragment_product with selected product
    private void initProductView(ProductWrapper product) {
        List<Image> galleryImages = product.getGalleryImages();
        TLog.d("initProductView:galleryImages.size(): " + galleryImages.size());
        galleryPageAdapter = new GalleryPageAdapter(getFragmentManager(), product.getGalleryImages());

        PagerBullet productViewPager = (PagerBullet) getView().findViewById(R.id.pager);
        productViewPager.setAdapter(galleryPageAdapter);
        productViewPager.setTextSeparatorOffset(5);
        productViewPager.setIndicatorTintColorScheme(getContext().getResources().getColor(R.color.red),getContext().getResources().getColor(R.color.grey));

        TextView productNameView = (TextView) getView().findViewById(R.id.product_name);
        productNameView.setText(product.getName());

        TextView productSubNameView = (TextView) getView().findViewById(R.id.product_sub_name);
        productSubNameView.setText(product.getCategory());

        TextView productPriceView = (TextView) getView().findViewById(R.id.product_price);
        productPriceView.setText(product.getPrice().getFormattedValue());

        if (product.getDiscountedPrice() != null) {
            TextView productDiscountedPriceView = (TextView) getView().findViewById(R.id.product_discounted_price);
            String sale = getString(R.string.label_sale) + " " + product.getDiscountedPrice().getFormattedValue();
            productDiscountedPriceView.setText(sale);
        }

        TextView productDescriptionView = (TextView) getView().findViewById(R.id.product_description);
        productDescriptionView.setText(Html.fromHtml(product.getDescription()));
    }
}
