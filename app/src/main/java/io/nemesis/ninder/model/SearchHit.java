package io.nemesis.ninder.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class SearchHit implements Parcelable {

    private Map<String, Object> properties;

    private Map<String, Object> highlightedProperties;

    protected SearchHit(Parcel in) {
        properties = in.readHashMap(Map.class.getClassLoader());
    }

    public static final Creator<SearchHit> CREATOR = new Creator<SearchHit>() {
        @Override
        public SearchHit createFromParcel(Parcel in) {
            return new SearchHit(in);
        }

        @Override
        public SearchHit[] newArray(int size) {
            return new SearchHit[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeMap(properties);
    }

    // end Parcelable


    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map<String, Object> getHighlightedProperties() {
        return highlightedProperties;
    }

    public void setHighlightedProperties(Map<String, Object> highlightedProperties) {
        this.highlightedProperties = highlightedProperties;
    }
}
