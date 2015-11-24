package io.nemesis.ninder.logic.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobi on 11/23/2015.
 */
public class Example {

    private List<Content> content = new ArrayList<Content>();
    //    private CurrentQuery currentQuery;
    private List<Object> breadcrumbs = new ArrayList<Object>();
    //    private List<Facet> facets = new ArrayList<Facet>();
//    private List<SortOption> sortOptions = new ArrayList<SortOption>();
    private Object keywordRedirectUrl;
    private Object subCategories;
    private Boolean last;
    private Integer totalPages;
    private Integer totalElements;
    private Boolean first;
    private Object sort;
    private Integer numberOfElements;
    private Integer size;
    private Integer number;

    /**
     * @return The content
     */
    public List<Content> getContent() {
        return content;
    }

    /**
     * @param content The content
     */
    public void setContent(List<Content> content) {
        this.content = content;
    }

//    /**
//     *
//     * @return
//     * The currentQuery
//     */
//    public CurrentQuery getCurrentQuery() {
//        return currentQuery;
//    }
//
//    /**
//     *
//     * @param currentQuery
//     * The currentQuery
//     */
//    public void setCurrentQuery(CurrentQuery currentQuery) {
//        this.currentQuery = currentQuery;
//    }

    /**
     * @return The breadcrumbs
     */
    public List<Object> getBreadcrumbs() {
        return breadcrumbs;
    }

    /**
     * @param breadcrumbs The breadcrumbs
     */
    public void setBreadcrumbs(List<Object> breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

//    /**
//     *
//     * @return
//     * The facets
//     */
//    public List<Facet> getFacets() {
//        return facets;
//    }
//
//    /**
//     *
//     * @param facets
//     * The facets
//     */
//    public void setFacets(List<Facet> facets) {
//        this.facets = facets;
//    }
//
//    /**
//     *
//     * @return
//     * The sortOptions
//     */
//    public List<SortOption> getSortOptions() {
//        return sortOptions;
//    }
//
//    /**
//     *
//     * @param sortOptions
//     * The sortOptions
//     */
//    public void setSortOptions(List<SortOption> sortOptions) {
//        this.sortOptions = sortOptions;
//    }

    /**
     * @return The keywordRedirectUrl
     */
    public Object getKeywordRedirectUrl() {
        return keywordRedirectUrl;
    }

    /**
     * @param keywordRedirectUrl The keywordRedirectUrl
     */
    public void setKeywordRedirectUrl(Object keywordRedirectUrl) {
        this.keywordRedirectUrl = keywordRedirectUrl;
    }

    /**
     * @return The subCategories
     */
    public Object getSubCategories() {
        return subCategories;
    }

    /**
     * @param subCategories The subCategories
     */
    public void setSubCategories(Object subCategories) {
        this.subCategories = subCategories;
    }

    /**
     * @return The last
     */
    public Boolean getLast() {
        return last;
    }

    /**
     * @param last The last
     */
    public void setLast(Boolean last) {
        this.last = last;
    }

    /**
     * @return The totalPages
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     * @param totalPages The totalPages
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * @return The totalElements
     */
    public Integer getTotalElements() {
        return totalElements;
    }

    /**
     * @param totalElements The totalElements
     */
    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    /**
     * @return The first
     */
    public Boolean getFirst() {
        return first;
    }

    /**
     * @param first The first
     */
    public void setFirst(Boolean first) {
        this.first = first;
    }

    /**
     * @return The sort
     */
    public Object getSort() {
        return sort;
    }

    /**
     * @param sort The sort
     */
    public void setSort(Object sort) {
        this.sort = sort;
    }

    /**
     * @return The numberOfElements
     */
    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    /**
     * @param numberOfElements The numberOfElements
     */
    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    /**
     * @return The size
     */
    public Integer getSize() {
        return size;
    }

    /**
     * @param size The size
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @return The number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * @param number The number
     */
    public void setNumber(Integer number) {
        this.number = number;
    }
}