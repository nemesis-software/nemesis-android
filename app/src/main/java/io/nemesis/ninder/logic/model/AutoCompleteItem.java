package io.nemesis.ninder.logic.model;

/**
 * Created by hristo.stoyanov on 08-Dec-16.
 */

public class AutoCompleteItem {
    private String code;
    private Object description;
    private Object discount;
    private Price discountedPrice;
    private String imageUrl;
    private String name;
    private Price price;
    private Boolean purchasable;
    private String url;
    private Object variantType;

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
     * @return The description
     */
    public Object getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(Object description) {
        this.description = description;
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
    public void setDiscount(Object discount) {
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
     * @return The purchasable
     */
    public Boolean getPurchasable() {
        return purchasable;
    }

    /**
     * @param purchasable The purchasable
     */
    public void setPurchasable(Boolean purchasable) {
        this.purchasable = purchasable;
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
     * @return The variantType
     */
    public Object getVariantType() {
        return variantType;
    }

    /**
     * @param variantType The variantType
     */
    public void setVariantType(Object variantType) {
        this.variantType = variantType;
    }
}