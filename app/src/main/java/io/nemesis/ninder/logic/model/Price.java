package io.nemesis.ninder.logic.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

/**
 * Created by bobi on 11/23/2015.
 */
public class Price implements Parcelable {

    private String currencyIso;
    private double value;
    private String priceType;
    private String formattedValue;

    // start Parcelable
    protected Price(Parcel in) {
        currencyIso = in.readString();
        value = in.readDouble();
        priceType = in.readString();
        formattedValue = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currencyIso);
        dest.writeDouble(value);
        dest.writeString(priceType);
        dest.writeString(formattedValue);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Price> CREATOR = new Creator<Price>() {
        @Override
        public Price createFromParcel(Parcel in) {
            return new Price(in);
        }

        @Override
        public Price[] newArray(int size) {
            return new Price[size];
        }
    };

    // end Parcelable

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
    public double getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(double value) {
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