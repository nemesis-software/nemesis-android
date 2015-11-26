package io.nemesis.ninder.logic.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bobi on 11/23/2015.
 */
public class VariantOptionQualifier implements Parcelable {

    private String qualifier;
    private String name;
    private String value;
    private Object image;
    private Object colorHex;

    // start Parcelable
    // TODO: 11/25/15 image and colorHex
    protected VariantOptionQualifier(Parcel in) {
        qualifier = in.readString();
        name = in.readString();
        value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(qualifier);
        dest.writeString(name);
        dest.writeString(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VariantOptionQualifier> CREATOR = new Creator<VariantOptionQualifier>() {
        @Override
        public VariantOptionQualifier createFromParcel(Parcel in) {
            return new VariantOptionQualifier(in);
        }

        @Override
        public VariantOptionQualifier[] newArray(int size) {
            return new VariantOptionQualifier[size];
        }
    };
    // end Parcelable
    /**
     * @return The qualifier
     */
    public String getQualifier() {
        return qualifier;
    }

    /**
     * @param qualifier The qualifier
     */
    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return The image
     */
    public Object getImage() {
        return image;
    }

    /**
     * @param image The image
     */
    public void setImage(Object image) {
        this.image = image;
    }

    /**
     * @return The colorHex
     */
    public Object getColorHex() {
        return colorHex;
    }

    /**
     * @param colorHex The colorHex
     */
    public void setColorHex(Object colorHex) {
        this.colorHex = colorHex;
    }

}