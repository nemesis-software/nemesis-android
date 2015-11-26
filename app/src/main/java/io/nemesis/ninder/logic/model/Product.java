package io.nemesis.ninder.logic.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobi on 11/23/2015.
 */
public class Product implements Parcelable {

//    private Object testAttribute;
    private List<Image> images = new ArrayList<Image>();
    private int averageRating;
    private int numberOfReviews;
//    private List<Object> reviews = new ArrayList<Object>();
    private String name;
    private Price price;
    private String imageUrl;
    private String variantType;
    private String description;
    private String code;
    private Price discountedPrice;
//    private List<Object> baseOptions = new ArrayList<Object>();
//    private Object volumePricesFlag;
//    private List<Object> productReferences = new ArrayList<Object>();
//    private Discount discount;
    private List<VariantOption> variantOptions = new ArrayList<VariantOption>();
    private String url;
    private String uid;
//    private List<Category> categories = new ArrayList<Category>();
//    private Boolean purchasable;
//    private Stock stock;
//    private Object potentialPromotions;

//    /**
//     * @return The testAttribute
//     */
//    public Object getTestAttribute() {
//        return testAttribute;
//    }
//
//    /**
//     * @param testAttribute The testAttribute
//     */
//    public void setTestAttribute(Object testAttribute) {
//        this.testAttribute = testAttribute;
//    }
//
//    /**
//     * @return The potentialPromotions
//     */
//    public Object getPotentialPromotions() {
//        return potentialPromotions;
//    }
//
//    /**
//     * @param potentialPromotions The potentialPromotions
//     */
//    public void setPotentialPromotions(Object potentialPromotions) {
//        this.potentialPromotions = potentialPromotions;
//    }

    // start Parcelable
    protected Product(Parcel in) {
        images = in.createTypedArrayList(Image.CREATOR);
        averageRating = in.readInt();
        numberOfReviews = in.readInt();
        name = in.readString();
        price = in.readParcelable(Price.class.getClassLoader());
        imageUrl = in.readString();
        variantType = in.readString();
        description = in.readString();
        code = in.readString();
        discountedPrice = in.readParcelable(Price.class.getClassLoader());
        variantOptions = in.createTypedArrayList(VariantOption.CREATOR);
        url = in.readString();
        uid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(images);
        dest.writeInt(averageRating);
        dest.writeInt(numberOfReviews);
        dest.writeString(name);
        dest.writeParcelable(price, flags);
        dest.writeString(imageUrl);
        dest.writeString(variantType);
        dest.writeString(description);
        dest.writeString(code);
        dest.writeParcelable(discountedPrice, flags);
        dest.writeTypedList(variantOptions);
        dest.writeString(url);
        dest.writeString(uid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
    // end Parcelable

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

//    /**
//     * @return The purchasable
//     */
//    public Boolean getPurchasable() {
//        return purchasable;
//    }
//
//    /**
//     * @param purchasable The purchasable
//     */
//    public void setPurchasable(Boolean purchasable) {
//        this.purchasable = purchasable;
//    }

    /**
     * @return The variantOptions
     */
    public List<VariantOption> getVariantOptions() {
        return variantOptions;
    }

    /**
     * @param variantOptions The variantOptions
     */
    public void setVariantOptions(List<VariantOption> variantOptions) {
        this.variantOptions = variantOptions;
    }

//    /**
//     * @return The categories
//     */
//    public List<Category> getCategories() {
//        return categories;
//    }
//
//    /**
//     * @param categories The categories
//     */
//    public void setCategories(List<Category> categories) {
//        this.categories = categories;
//    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

//    /**
//     * @return The volumePricesFlag
//     */
//    public Object getVolumePricesFlag() {
//        return volumePricesFlag;
//    }
//
//    /**
//     * @param volumePricesFlag The volumePricesFlag
//     */
//    public void setVolumePricesFlag(Object volumePricesFlag) {
//        this.volumePricesFlag = volumePricesFlag;
//    }

//    /**
//     * @return The stock
//     */
//    public Stock getStock() {
//        return stock;
//    }
//
//    /**
//     * @param stock The stock
//     */
//    public void setStock(Stock stock) {
//        this.stock = stock;
//    }

    /**
     * @return The variantType
     */
    public String getVariantType() {
        return variantType;
    }

    /**
     * @param variantType The variantType
     */
    public void setVariantType(String variantType) {
        this.variantType = variantType;
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

//    /**
//     * @return The baseOptions
//     */
//    public List<Object> getBaseOptions() {
//        return baseOptions;
//    }
//
//    /**
//     * @param baseOptions The baseOptions
//     */
//    public void setBaseOptions(List<Object> baseOptions) {
//        this.baseOptions = baseOptions;
//    }

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

//    /**
//     * @return The productReferences
//     */
//    public List<Object> getProductReferences() {
//        return productReferences;
//    }
//
//    /**
//     * @param productReferences The productReferences
//     */
//    public void setProductReferences(List<Object> productReferences) {
//        this.productReferences = productReferences;
//    }
//
//    /**
//     * @return The discount
//     */
//    public Discount getDiscount() {
//        return discount;
//    }
//
//    /**
//     * @param discount The discount
//     */
//    public void setDiscount(Discount discount) {
//        this.discount = discount;
//    }

    /**
     * @return The imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @param imageUrl The imageUrl
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    /**
     * @return The numberOfReviews
     */
    public Integer getNumberOfReviews() {
        return numberOfReviews;
    }

    /**
     * @param numberOfReviews The numberOfReviews
     */
    public void setNumberOfReviews(Integer numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

//    /**
//     * @return The reviews
//     */
//    public List<Object> getReviews() {
//        return reviews;
//    }
//
//    /**
//     * @param reviews The reviews
//     */
//    public void setReviews(List<Object> reviews) {
//        this.reviews = reviews;
//    }

    /**
     * @return The averageRating
     */
    public Integer getAverageRating() {
        return averageRating;
    }

    /**
     * @param averageRating The averageRating
     */
    public void setAverageRating(Integer averageRating) {
        this.averageRating = averageRating;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}