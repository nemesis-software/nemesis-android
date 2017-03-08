package io.nemesis.ninder.util;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;

import io.nemesis.ninder.R;

/**
 * Created by hristo.stoyanov on 12-Dec-16.
 */

public class Util {
    private static final int PASSWORD_MIN_LENGTH = 6;
    private static final int FIELD_MIN_LENGTH = 3;

    public static boolean isPasswordValid(Context context, TextInputEditText field){
        String field_text = field.getText().toString();
        if (TextUtils.isEmpty(field_text) || field_text.length()<PASSWORD_MIN_LENGTH) {
            field.setError(context.getString(R.string.error_invalid_password));
            field.requestFocus();
            return false;
        }
        else return true;
    }

    public static boolean isTextValid(Context context, TextInputEditText field){
        String field_text = field.getText().toString();
        if (TextUtils.isEmpty(field_text) || field_text.length()<FIELD_MIN_LENGTH) {
            field.setError(context.getString(R.string.error_invalid_field));
            field.requestFocus();
            return false;
        }
        else return true;
    }
}
