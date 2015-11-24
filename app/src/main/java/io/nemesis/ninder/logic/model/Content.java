package io.nemesis.ninder.logic.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobi on 11/23/2015.
 */
public class Content {

    //    private Object testAttribute;
//    private Object potentialPromotions;
//    private DiscountedPrice discountedPrice;
//    private Object purchasable;
//    private Object variantOptions;
//    private List<Category> categories = new ArrayList<Category>();
//    private String description;
//    private Object volumePricesFlag;
//    private Object stock;
//    private Object variantType;
//    private String name;
//    private Object baseOptions;
//    private String code;
//    private Object productReferences;
//    private Discount discount;
//    private Object imageUrl;
//    private String url;
//    private Price price;
    private List<Image> images = new ArrayList<Image>();
//    private Object numberOfReviews;
//    private Object reviews;
//    private Integer averageRating;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }


//    /**
//     *
//     * @return
//     * The testAttribute
//     */
//    public Object getTestAttribute() {
//        return testAttribute;
//    }
//
//    /**
//     *
//     * @param testAttribute
//     * The testAttribute
//     */
//    public void setTestAttribute(Object testAttribute) {
//        this.testAttribute = testAttribute;
//    }
//
//    /**
//     *
//     * @return
//     * The potentialPromotions
//     */
//    public Object getPotentialPromotions() {
//        return potentialPromotions;
//    }
//
//    /**
//     *
//     * @param potentialPromotions
//     * The potentialPromotions
//     */
//    public void setPotentialPromotions(Object potentialPromotions) {
//        this.potentialPromotions = potentialPromotions;
//    }
//
//    /**
//     *
//     * @return
//     * The discountedPrice
//     */
//    public DiscountedPrice getDiscountedPrice() {
//        return discountedPrice;
//    }
//
//    /**
//     *
//     * @param discountedPrice
//     * The discountedPrice
//     */
//    public void setDiscountedPrice(DiscountedPrice discountedPrice) {
//        this.discountedPrice = discountedPrice;
//    }
//
//    /**
//     *
//     * @return
//     * The purchasable
//     */
//    public Object getPurchasable() {
//        return purchasable;
//    }
//
//    /**
//     *
//     * @param purchasable
//     * The purchasable
//     */
//    public void setPurchasable(Object purchasable) {
//        this.purchasable = purchasable;
//    }
//
//    /**
//     *
//     * @return
//     * The variantOptions
//     */
//    public Object getVariantOptions() {
//        return variantOptions;
//    }
//
//    /**
//     *
//     * @param variantOptions
//     * The variantOptions
//     */
//    public void setVariantOptions(Object variantOptions) {
//        this.variantOptions = variantOptions;
//    }
//
//    /**
//     *
//     * @return
//     * The categories
//     */
//    public List<Category> getCategories() {
//        return categories;
//    }
//
//    /**
//     *
//     * @param categories
//     * The categories
//     */
//    public void setCategories(List<Category> categories) {
//        this.categories = categories;
//    }
//
//    /**
//     *
//     * @return
//     * The description
//     */
//    public String getDescription() {
//        return description;
//    }
//
//    /**
//     *
//     * @param description
//     * The description
//     */
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    /**
//     *
//     * @return
//     * The volumePricesFlag
//     */
//    public Object getVolumePricesFlag() {
//        return volumePricesFlag;
//    }
//
//    /**
//     *
//     * @param volumePricesFlag
//     * The volumePricesFlag
//     */
//    public void setVolumePricesFlag(Object volumePricesFlag) {
//        this.volumePricesFlag = volumePricesFlag;
//    }
//
//    /**
//     *
//     * @return
//     * The stock
//     */
//    public Object getStock() {
//        return stock;
//    }
//
//    /**
//     *
//     * @param stock
//     * The stock
//     */
//    public void setStock(Object stock) {
//        this.stock = stock;
//    }
//
//    /**
//     *
//     * @return
//     * The variantType
//     */
//    public Object getVariantType() {
//        return variantType;
//    }
//
//    /**
//     *
//     * @param variantType
//     * The variantType
//     */
//    public void setVariantType(Object variantType) {
//        this.variantType = variantType;
//    }
//
//    /**
//     *
//     * @return
//     * The name
//     */
//    public String getName() {
//        return name;
//    }
//
//    /**
//     *
//     * @param name
//     * The name
//     */
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    /**
//     *
//     * @return
//     * The baseOptions
//     */
//    public Object getBaseOptions() {
//        return baseOptions;
//    }
//
//    /**
//     *
//     * @param baseOptions
//     * The baseOptions
//     */
//    public void setBaseOptions(Object baseOptions) {
//        this.baseOptions = baseOptions;
//    }
//
//    /**
//     *
//     * @return
//     * The code
//     */
//    public String getCode() {
//        return code;
//    }
//
//    /**
//     *
//     * @param code
//     * The code
//     */
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    /**
//     *
//     * @return
//     * The productReferences
//     */
//    public Object getProductReferences() {
//        return productReferences;
//    }
//
//    /**
//     *
//     * @param productReferences
//     * The productReferences
//     */
//    public void setProductReferences(Object productReferences) {
//        this.productReferences = productReferences;
//    }
//
//    /**
//     *
//     * @return
//     * The discount
//     */
//    public Discount getDiscount() {
//        return discount;
//    }
//
//    /**
//     *
//     * @param discount
//     * The discount
//     */
//    public void setDiscount(Discount discount) {
//        this.discount = discount;
//    }
//
//    /**
//     *
//     * @return
//     * The imageUrl
//     */
//    public Object getImageUrl() {
//        return imageUrl;
//    }
//
//    /**
//     *
//     * @param imageUrl
//     * The imageUrl
//     */
//    public void setImageUrl(Object imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//
//    /**
//     *
//     * @return
//     * The url
//     */
//    public String getUrl() {
//        return url;
//    }
//
//    /**
//     *
//     * @param url
//     * The url
//     */
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    /**
//     *
//     * @return
//     * The price
//     */
//    public Price getPrice() {
//        return price;
//    }
//
//    /**
//     *
//     * @param price
//     * The price
//     */
//    public void setPrice(Price price) {
//        this.price = price;
//    }
//
//    /**
//     *
//     * @return
//     * The images
//     */
//    public List<Image> getImages() {
//        return images;
//    }
//
//    /**
//     *
//     * @param images
//     * The images
//     */
//    public void setImages(List<Image> images) {
//        this.images = images;
//    }
//
//    /**
//     *
//     * @return
//     * The numberOfReviews
//     */
//    public Object getNumberOfReviews() {
//        return numberOfReviews;
//    }
//
//    /**
//     *
//     * @param numberOfReviews
//     * The numberOfReviews
//     */
//    public void setNumberOfReviews(Object numberOfReviews) {
//        this.numberOfReviews = numberOfReviews;
//    }
//
//    /**
//     *
//     * @return
//     * The reviews
//     */
//    public Object getReviews() {
//        return reviews;
//    }
//
//    /**
//     *
//     * @param reviews
//     * The reviews
//     */
//    public void setReviews(Object reviews) {
//        this.reviews = reviews;
//    }
//
//    /**
//     *
//     * @return
//     * The averageRating
//     */
//    public Integer getAverageRating() {
//        return averageRating;
//    }
//
//    /**
//     *
//     * @param averageRating
//     * The averageRating
//     */
//    public void setAverageRating(Integer averageRating) {
//        this.averageRating = averageRating;
//    }
}