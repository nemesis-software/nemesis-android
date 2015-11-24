package io.nemesis.ninder.logic.model;

/**
 * Created by bobi on 11/23/2015.
 */
public class Image {

    private String imageType;
    private String format;
    private Object name;
    private String url;
    private Object altText;
    private Object galleryIndex;
    private Integer width;
    private Integer height;

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
    public Object getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(Object name) {
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
    public Object getAltText() {
        return altText;
    }

    /**
     * @param altText The altText
     */
    public void setAltText(Object altText) {
        this.altText = altText;
    }

    /**
     * @return The galleryIndex
     */
    public Object getGalleryIndex() {
        return galleryIndex;
    }

    /**
     * @param galleryIndex The galleryIndex
     */
    public void setGalleryIndex(Object galleryIndex) {
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
}