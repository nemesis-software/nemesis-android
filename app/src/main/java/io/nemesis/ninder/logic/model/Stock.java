package io.nemesis.ninder.logic.model;

/**
 * Created by bobi on 11/23/2015.
 */
public class Stock {

    private String stockLevelStatus;
    private Integer stockLevel;

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
