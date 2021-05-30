package io.nemesis.ninder.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ProductFacetSearchPageDto implements Parcelable {

    private List<SearchHit> content;

    protected ProductFacetSearchPageDto(Parcel in) {
        content = in.createTypedArrayList(SearchHit.CREATOR);
    }

    public static final Creator<ProductFacetSearchPageDto> CREATOR = new Creator<ProductFacetSearchPageDto>() {
        @Override
        public ProductFacetSearchPageDto createFromParcel(Parcel in) {
            return new ProductFacetSearchPageDto(in);
        }

        @Override
        public ProductFacetSearchPageDto[] newArray(int size) {
            return new ProductFacetSearchPageDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }

    public List<SearchHit> getContent() {
        return content;
    }

    public void setContent(List<SearchHit> content) {
        this.content = content;
    }
}
