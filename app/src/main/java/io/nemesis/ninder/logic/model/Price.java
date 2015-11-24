package io.nemesis.ninder.logic.model;

import java.math.BigDecimal;

/**
 * Created by bobi on 11/23/2015.
 */
public class Price {

    private String currencyIso;
    private BigDecimal value;
    private String priceType;
    private String formattedValue;

    /**
     * @return The currencyIso
     */
    public String getCurrencyIso() {
        return currencyIso;
    }

    /**
     * @param currencyIso The currencyIso
     */
    public void setCurrencyIso(String currencyIso) {
        this.currencyIso = currencyIso;
    }

    /**
     * @return The value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * @return The priceType
     */
    public String getPriceType() {
        return priceType;
    }

    /**
     * @param priceType The priceType
     */
    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    /**
     * @return The formattedValue
     */
    public String getFormattedValue() {
        return formattedValue;
    }

    /**
     * @param formattedValue The formattedValue
     */
    public void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
    }

}