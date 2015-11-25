package io.nemesis.ninder.util;

/**
 * @author ivanpetkov
 * @since 11/25/15
 */
public class StringUtils {

    public static boolean isEmpty(String str) {
        return null == str || str.trim().length() == 0;
    }
}
