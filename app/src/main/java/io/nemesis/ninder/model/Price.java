package io.nemesis.ninder.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bobi on 11/23/2015.
 */
public class Price implements Parcelable {

    private String currency;
    private double amount;
    private String formatted;

    protected Price(String currency, double amount, String formatted) {
        this.currency = currency;
        this.amount = amount;
        this.formatted = formatted;
    }

    // start Parcelable
    protected Price(Parcel in) {
        currency = in.readString();
        amount = in.readDouble();
        formatted = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currency);
        dest.writeDouble(amount);
        dest.writeString(formatted);
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
     * @return The currency
     */
    public String getCurrencyIso() {
        return currency;
    }

    /**
     * @param currency The currency
     */
    public void setCurrencyIso(String currency) {
        this.currency = currency;
    }

    /**
     * @return The amount
     */
    public double getValue() {
        return amount;
    }

    /**
     * @param amount The amount
     */
    public void setValue(double amount) {
        this.amount = amount;
    }

    /**
     * @return The formatted
     */
    public String getFormattedValue() {
        return formatted;
    }

    /**
     * @param formatted The formatted
     */
    public void setFormattedValue(String formatted) {
        this.formatted = formatted;
    }

}