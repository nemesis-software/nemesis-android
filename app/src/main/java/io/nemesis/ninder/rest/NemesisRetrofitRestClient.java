package io.nemesis.ninder.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.util.Log;

import com.moczul.ok2curl.CurlInterceptor;
import com.moczul.ok2curl.logger.Loggable;

import io.nemesis.ninder.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author ivanpetkov
 * @since 11/24/15
 */
public class NemesisRetrofitRestClient {

    private static RestApi sRestApi = null;

    private NemesisRetrofitRestClient() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(new CurlInterceptor(new Loggable() {
            @Override
            public void log(String message) {
                Log.v("Ok2Curl", message);
            }
        }));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.REST_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(httpClient.build())
                .build();
        sRestApi = retrofit.create(RestApi.class);
    }

    public static RestApi getApiService() {
        if (sRestApi == null) {
            new NemesisRetrofitRestClient();
        }
        return sRestApi;
    }
}
