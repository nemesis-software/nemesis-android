package io.nemesis.ninder.logic.rest;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.nemesis.ninder.R;
import io.nemesis.ninder.logic.model.Product;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

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
            if (Product.class.getName().equalsIgnoreCase(s) || "java.util.List<io.nemesis.ninder.logic.model.Product>".equalsIgnoreCase(s)) {
                return new TypeAdapter<T>() {

                    public void write(JsonWriter out, T value) throws IOException {
                        delegate.write(out, value);
                    }

                    public T read(JsonReader in) throws IOException {

                        JsonElement jsonElement = elementAdapter.read(in);
                        if (jsonElement.isJsonObject()) {
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            if (jsonObject.has("content") && jsonObject.get("content").isJsonArray()) {
                                jsonElement = jsonObject.get("content");
                            } else if (jsonObject.has("product") && jsonObject.get("product").isJsonObject()) {
                                jsonElement = jsonObject.get("product");
                            }
                        }

                        return delegate.fromJsonTree(jsonElement);
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


        ExecutorService executorService = Executors.newFixedThreadPool(6);
        ExecutorService executorService1 = Executors.newFixedThreadPool(1);

        RestAdapter adapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
//                .setEndpoint(context.getString(R.string.rest_api_base_url))
                .setEndpoint(baseUrl)
                .setConverter(new GsonConverter(gson))
                .setExecutors(executorService, executorService1)
                .build();

        apiService = adapter.create(RestApi.class);
    }

    public RestApi getApiService() {
        return apiService;
    }
}
