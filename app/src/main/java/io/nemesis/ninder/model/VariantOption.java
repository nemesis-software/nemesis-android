package io.nemesis.ninder.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobi on 11/23/2015.
 */
public class VariantOption implements Parcelable {

    private Stock stock;
    private String url;
    private Price price;
    private Discount discount;
    private Price discountedPrice;
    private List<VariantOptionQualifier> variantOptionQualifiers = new ArrayList<VariantOptionQualifier>();
    private String code;
    private Image picture;
    private List<Image> images = new ArrayList<Image>();

    // start Parcelable
    protected VariantOption(Parcel in) {
        stock = in.readParcelable(Stock.class.getClassLoader());
        url = in.readString();
        price = in.readParcelable(Price.class.getClassLoader());
        discount = in.readParcelable(Discount.class.getClassLoader());
        discountedPrice = in.readParcelable(Price.class.getClassLoader());
        variantOptionQualifiers = in.createTypedArrayList(VariantOptionQualifier.CREATOR);
        code = in.readString();
        picture = in.readParcelable(Image.class.getClassLoader());
        images = in.createTypedArrayList(Image.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(stock, flags);
        dest.writeString(url);
        dest.writeParcelable(price, flags);
        dest.writeParcelable(discount, flags);
        dest.writeParcelable(discountedPrice, flags);
        dest.writeTypedList(variantOptionQualifiers);
        dest.writeString(code);
        dest.writeParcelable(picture, flags);
        dest.writeTypedList(images);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VariantOption> CREATOR = new Creator<VariantOption>() {
        @Override
        public VariantOption createFromParcel(Parcel in) {
            return new VariantOption(in);
        }

        @Override
        public VariantOption[] newArray(int size) {
            return new VariantOption[size];
        }
    };
    // end Parcelable

    /**
     * @return The stock
     */
    public Stock getStock() {
        return stock;
    }

    /**
     * @param stock The stock
     */
    public void setStock(Stock stock) {
        this.stock = stock;
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
     * @return The price
     */
    public Price getPrice() {
        return price;
    }

    /**
     * @param price The price
     */
    public void setPrice(Price price) {
        this.price = price;
    }

    /**
     * @return The discount
     */
    public Object getDiscount() {
        return discount;
    }

    /**
     * @param discount The discount
     */
    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    /**
     * @return The discountedPrice
     */
    public Price getDiscountedPrice() {
        return discountedPrice;
    }

    /**
     * @param discountedPrice The discountedPrice
     */
    public void setDiscountedPrice(Price discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    /**
     * @return The variantOptionQualifiers
     */
    public List<VariantOptionQualifier> getVariantOptionQualifiers() {
        return variantOptionQualifiers;
    }

    /**
     * @param variantOptionQualifiers The variantOptionQualifiers
     */
    public void setVariantOptionQualifiers(List<VariantOptionQualifier> variantOptionQualifiers) {
        this.variantOptionQualifiers = variantOptionQualifiers;
    }

    /**
     * @return The code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code The code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return The picture
     */
    public Image getPicture() {
        return picture;
    }

    /**
     * @param picture The picture
     */
    public void setPicture(Image picture) {
        this.picture = picture;
    }

    /**
     * @return The images
     */
    public List<Image> getImages() {
        return images;
    }

    /**
     * @param images The images
     */
    public void setImages(List<Image> images) {
        this.images = images;
    }

}