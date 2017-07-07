package io.nemesis.ninder.rest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import io.nemesis.ninder.model.Product;

/**
 * Created by hristo.stoyanov on 07-Jul-17.
 */


public class ItemTypeAdapterFactory implements TypeAdapterFactory {

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
