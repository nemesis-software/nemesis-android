package io.nemesis.ninder;

import android.app.Application;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import io.nemesis.ninder.logic.NemesisFacadeImpl;
import io.nemesis.ninder.logic.ProductFacade;

/**
 * @author Philip
 * @since 2015-11-22
 */
public class NinderApplication extends Application {

    private ProductFacade mProductFacade;

    @Override
    public void onCreate() {
        super.onCreate();

        //init stuff here
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.loggingEnabled(true);
        Picasso.setSingletonInstance(builder.build());

        mProductFacade = new NemesisFacadeImpl(this);
    }

    public ProductFacade getProductFacade() {
        return mProductFacade;
    }
}