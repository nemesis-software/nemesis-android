package io.nemesis.ninder.model;

/**
 * Created by bobi on 11/23/2015.
 */

import java.util.ArrayList;
import java.util.List;

public class Supercategory {

    private Object picture;
    private String name;
    private String description;
    private String url;
    private List<Supercategory> supercategories = new ArrayList<Supercategory>();
    private Object subcategories;
    private Object thumbnail;
    private String uid;

    /**
     * @return The picture
     */
    public Object getPicture() {
        return picture;
    }

    /**
     * @param picture The picture
     */
    public void setPicture(Object picture) {
        this.picture = picture;
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
     * @return The supercategories
     */
    public List<Supercategory> getSupercategories() {
        return supercategories;
    }

    /**
     * @param supercategories The supercategories
     */
    public void setSupercategories(List<Supercategory> supercategories) {
        this.supercategories = supercategories;
    }

    /**
     * @return The subcategories
     */
    public Object getSubcategories() {
        return subcategories;
    }

    /**
     * @param subcategories The subcategories
     */
    public void setSubcategories(Object subcategories) {
        this.subcategories = subcategories;
    }

    /**
     * @return The thumbnail
     */
    public Object getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbnail The thumbnail
     */
    public void setThumbnail(Object thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * @return The uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid The uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }
}