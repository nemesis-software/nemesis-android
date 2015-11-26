package io.nemesis.ninder.logic.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bobi on 11/23/2015.
 */
public class Stock implements Parcelable {

    private String stockLevelStatus;
    private int stockLevel;

    // start Parcelable
    protected Stock(Parcel in) {
        stockLevelStatus = in.readString();
        stockLevel = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stockLevelStatus);
        dest.writeInt(stockLevel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Stock> CREATOR = new Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel in) {
            return new Stock(in);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };
    // end Parcelable

    /**
     * @return The stockLevelStatus
     */
    public String getStockLevelStatus() {
        return stockLevelStatus;
    }

    /**
     * @param stockLevelStatus The stockLevelStatus
     */
    public void setStockLevelStatus(String stockLevelStatus) {
        this.stockLevelStatus = stockLevelStatus;
    }

    /**
     * @return The stockLevel
     */
    public Integer getStockLevel() {
        return stockLevel;
    }

    /**
     * @param stockLevel The stockLevel
     */
    public void setStockLevel(Integer stockLevel) {
        this.stockLevel = stockLevel;
    }

}
