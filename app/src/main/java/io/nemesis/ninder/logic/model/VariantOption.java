package io.nemesis.ninder.logic.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobi on 11/23/2015.
 */
public class VariantOption {

    private Stock stock;
    private Object url;
    private Price price;
    private Object discount;
    private Object discountedPrice;
    private List<VariantOptionQualifier> variantOptionQualifiers = new ArrayList<VariantOptionQualifier>();
    private String code;
    private Picture picture;
    private List<Image> images = new ArrayList<Image>();

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
    public Object getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(Object url) {
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
    public void setDiscount(Object discount) {
        this.discount = discount;
    }

    /**
     * @return The discountedPrice
     */
    public Object getDiscountedPrice() {
        return discountedPrice;
    }

    /**
     * @param discountedPrice The discountedPrice
     */
    public void setDiscountedPrice(Object discountedPrice) {
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
    public Picture getPicture() {
        return picture;
    }

    /**
     * @param picture The picture
     */
    public void setPicture(Picture picture) {
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