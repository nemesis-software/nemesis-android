package io.nemesis.ninder.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentManager;

import java.util.List;

import io.nemesis.ninder.R;
import io.nemesis.ninder.fragment.GalleryPageFragment;
import io.nemesis.ninder.logic.model.Image;

/**
 * @author ivanpetkov
 * @since 11/24/15
 */
public class GalleryPageAdapter extends FragmentStatePagerAdapter {

    private final List<Image> data;
    private final int imageErrorPlaceholder;
    private final int imagePlaceholder;

    public GalleryPageAdapter(FragmentManager fm, List<Image> data) {
        this(fm, data, R.drawable.image_err_placeholder, R.drawable.placeholder);
    }

    public GalleryPageAdapter(FragmentManager fm, List<Image> data, int imageErrorPlaceholder, int imagePlaceholder) {
        super(fm);

        this.data = data;
        this.imageErrorPlaceholder = imageErrorPlaceholder;
        this.imagePlaceholder = imagePlaceholder;
    }

    @Override
    public Fragment getItem(int position) {
        Image image = data.get(position);
        GalleryPageFragment galleryPageFragment = new GalleryPageFragment();

        Bundle args = new Bundle();
        args.putString(GalleryPageFragment.IMAGE_URI, image.getUrl());
        args.putInt(GalleryPageFragment.IMAGE_ERROR_RES_ID, imageErrorPlaceholder);
        args.putInt(GalleryPageFragment.IMAGE_PLACEHOLDER_RES_ID, imagePlaceholder);
        galleryPageFragment.setArguments(args);

        return galleryPageFragment;
    }

    @Override
    public int getCount() {
        return data.size();
    }
}
