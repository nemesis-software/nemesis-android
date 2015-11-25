package io.nemesis.ninder.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.nemesis.ninder.R;

/**
 * @author ivanpetkov
 * @since 11/24/15
 */
public class GalleryPageFragment extends Fragment {

    public static final String IMAGE_URI = "image.uri";

    public static final String IMAGE_ERROR_RES_ID = "image.err.id";
    public static final String IMAGE_PLACEHOLDER_RES_ID = "image.placeholder.id";

    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery_page, container, false);

        imageView = (ImageView) root.findViewById(R.id.image_view);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle arguments = getArguments();
        String imageUri = arguments.getString(IMAGE_URI);
        int errorIcon = arguments.getInt(IMAGE_ERROR_RES_ID);
        int placeholderIcon = arguments.getInt(IMAGE_PLACEHOLDER_RES_ID);

        Picasso.with(getActivity())
                .load(imageUri)
                .error(errorIcon)
                .placeholder(placeholderIcon)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // do nothing
                    }

                    @Override
                    public void onError() {
                        // do something if needed
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
