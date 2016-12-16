package io.nemesis.ninder.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bobi on 11/23/2015.
 */
public class Image implements Parcelable {

    private String imageType;
    private String format;
    private String name;
    private String url;
    private String altText;
    private String galleryIndex;
    private int width;
    private int height;

    // start Parcelable
    protected Image(Parcel in) {
        imageType = in.readString();
        format = in.readString();
        name = in.readString();
        url = in.readString();
        altText = in.readString();
        galleryIndex = in.readString();
        width = in.readInt();
        height = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageType);
        dest.writeString(format);
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(altText);
        dest.writeString(galleryIndex);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
    // end Parcelable

    /**
     * @return The imageType
     */
    public String getImageType() {
        return imageType;
    }

    /**
     * @param imageType The imageType
     */
    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    /**
     * @return The format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format The format
     */
    public void setFormat(String format) {
        this.format = format;
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
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The altText
     */
    public String getAltText() {
        return altText;
    }

    /**
     * @param altText The altText
     */
    public void setAltText(String altText) {
        this.altText = altText;
    }

    /**
     * @return The galleryIndex
     */
    public String getGalleryIndex() {
        return galleryIndex;
    }

    /**
     * @param galleryIndex The galleryIndex
     */
    public void setGalleryIndex(String galleryIndex) {
        this.galleryIndex = galleryIndex;
    }

    /**
     * @return The width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width The width
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * @return The height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height The height
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    // end Parcelable
}