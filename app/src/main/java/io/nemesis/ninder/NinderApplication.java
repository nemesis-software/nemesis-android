package io.nemesis.ninder;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;
import io.nemesis.ninder.logger.TLog;
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
        Fabric.with(this, new Crashlytics());

        initLogger();

        //init stuff here
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.loggingEnabled(false);
        Picasso.setSingletonInstance(builder.build());

        mProductFacade = new NemesisFacadeImpl(this);
    }

    public ProductFacade getProductFacade() {
        return mProductFacade;
    }

    private void initLogger() {
        final boolean loggerEnabled;
        final TLog.Type logLevel;
        if (BuildConfig.DEBUG) {
            loggerEnabled = true;
            logLevel = TLog.Type.VERBOSE;
        } else {
            loggerEnabled = true;
            logLevel = TLog.Type.INFO;
        }

        TLog.updateConfiguration(
                new TLog.TLogConfiguration(
                        "Ninder",
                        loggerEnabled,
                        true, true, // generate tags and additional message info
                        logLevel,
                        new TLog.TLogMessageSink() {
                            @Override
                            public void sinkLogMessage(final int priority, final String tag, final String message) {
                                if (Crashlytics.getInstance() != null) {
                                    Crashlytics.log(priority, tag, message);
                                } else {
                                    Log.println(priority, tag, message);
                                }
                            }
                        }
                )
        );
    }
}