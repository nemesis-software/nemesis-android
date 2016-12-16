package io.nemesis.ninder.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ivanpetkov
 * @since 1/18/16
 */
public class ProductEntity implements Parcelable {
    private Product product;
    private List<Variation> variants = new ArrayList<>();

    // start Parcelable
    protected ProductEntity(Parcel in) {
        product = in.readParcelable(Product.class.getClassLoader());
        variants = in.createTypedArrayList(Variation.CREATOR);
    }

    public static final Creator<ProductEntity> CREATOR = new Creator<ProductEntity>() {
        @Override
        public ProductEntity createFromParcel(Parcel in) {
            return new ProductEntity(in);
        }

        @Override
        public ProductEntity[] newArray(int size) {
            return new ProductEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(product, flags);
        dest.writeTypedList(variants);
    }
    // end Parcelable


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Variation> getVariants() {
        return variants;
    }

    public void setVariants(List<Variation> variants) {
        this.variants = variants;
    }
}
