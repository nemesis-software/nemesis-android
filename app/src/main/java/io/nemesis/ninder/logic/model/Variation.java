package io.nemesis.ninder.logic.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ivanpetkov
 * @since 1/18/16
 */
public class Variation implements Parcelable {
    private Stock stock;
    private String url;
    private Price price;
    private Object discount;
    private Price discountedPrice;
    private List<VariantOptionQualifier> variantOptionQualifiers = new ArrayList<VariantOptionQualifier>();
    private String uid;
    private Image pictures;
    private List<Image> images = new ArrayList<>();


    // start Parcelable
    protected Variation(Parcel in) {
        stock = in.readParcelable(Stock.class.getClassLoader());
        url = in.readString();
        price = in.readParcelable(Price.class.getClassLoader());
        discountedPrice = in.readParcelable(Price.class.getClassLoader());
        variantOptionQualifiers = in.createTypedArrayList(VariantOptionQualifier.CREATOR);
        uid = in.readString();
        pictures = in.readParcelable(Image.class.getClassLoader());
        images = in.createTypedArrayList(Image.CREATOR);
    }

    public static final Creator<Variation> CREATOR = new Creator<Variation>() {
        @Override
        public Variation createFromParcel(Parcel in) {
            return new Variation(in);
        }

        @Override
        public Variation[] newArray(int size) {
            return new Variation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(stock, flags);
        dest.writeString(url);
        dest.writeParcelable(price, flags);
        dest.writeParcelable(discountedPrice, flags);
        dest.writeTypedList(variantOptionQualifiers);
        dest.writeString(uid);
        dest.writeParcelable(pictures, flags);
        dest.writeTypedList(images);
    }
    // end Parcelable


    public Stock getStock() {
        return stock;
    }

    public String getUrl() {
        return url;
    }

    public Price getPrice() {
        return price;
    }

    public Object getDiscount() {
        return discount;
    }

    public Price getDiscountedPrice() {
        return discountedPrice;
    }

    public List<VariantOptionQualifier> getVariantOptionQualifiers() {
        return variantOptionQualifiers;
    }

    public String getUid() {
        return uid;
    }

    public Image getPictures() {
        return pictures;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public void setDiscount(Object discount) {
        this.discount = discount;
    }

    public void setDiscountedPrice(Price discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public void setVariantOptionQualifiers(List<VariantOptionQualifier> variantOptionQualifiers) {
        this.variantOptionQualifiers = variantOptionQualifiers;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPictures(Image pictures) {
        this.pictures = pictures;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
