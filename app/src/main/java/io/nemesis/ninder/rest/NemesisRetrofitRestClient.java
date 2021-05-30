package io.nemesis.ninder.rest;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.nemesis.ninder.model.Product;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author ivanpetkov
 * @since 11/24/15
 */
public class NemesisRetrofitRestClient {

    private class ItemTypeAdapterFactory implements TypeAdapterFactory {

        public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {

            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

            String s = type.toString();
            if (Product.class.getName().equalsIgnoreCase(s) || "java.util.List<io.nemesis.ninder.model.Product>".equalsIgnoreCase(s)) {
                return new TypeAdapter<T>() {

                    public void write(JsonWriter out, T value) throws IOException {
                        delegate.write(out, value);
                    }

                    public T read(JsonReader in) throws IOException {
                        JsonElement jsonElement = elementAdapter.read(in);
                        jsonElement = resolveRoot(jsonElement);
                        return delegate.fromJsonTree(jsonElement);
                    }

                    private JsonElement resolveRoot(final JsonElement jsonElement) {
                        if (jsonElement.isJsonObject()) {
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            if (jsonObject.has("content") && jsonObject.get("content").isJsonArray()) {
                                return jsonObject.get("content");
                            }
//                            else if (jsonObject.has("product") && jsonObject.get("product").isJsonObject()) {
//                                return jsonObject.get("product");
//                            }
//                            else if (jsonObject.has("cmsPage") && jsonObject.get("cmsPage").isJsonObject()) {
//                                return resolveRoot(jsonObject.get("cmsPage"));
//                            }
                            else if (jsonObject.has("page") && jsonObject.get("page").isJsonObject()) {
                                return resolveRoot(jsonObject.get("page"));
                            }
                        }
                        return jsonElement;
                    }

                }.nullSafe();
            }

            return new TypeAdapter<T>() {

                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                public T read(JsonReader in) throws IOException {
                    return delegate.read(in);
                }
            }.nullSafe();
        }
    }

    private final RestApi apiService;

    public NemesisRetrofitRestClient(final Context context, String baseUrl) {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        try {

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            System.out.println("CheckClientTrusted");
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            System.out.println("CheckServerTrusted");
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            System.out.println("getAcceptedIssuers");
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            httpClient.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    System.out.println("verify");
                    return true;
                }
            });

            Retrofit adapter = new Retrofit.Builder()
                    //.setLogLevel(RestAdapter.LogLevel.HEADERS)
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();

            apiService = adapter.create(RestApi.class);
        } catch (Exception ex) {
            throw new RuntimeException("Error: " + ex.getMessage());
        }
    }

    public RestApi getApiService() {
        return apiService;
    }
}
