package com.tweener.floatingactiongroup.util;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vivien Mahe
 */
public final class Typefaces {

    private static final Map<String, Typeface> TYPEFACES = new HashMap<>();

    private Typefaces() {
    }

    public static Typeface createFromAsset(final AssetManager assets, final String fontFileName) {
        final Typeface typeface;
        if (TYPEFACES.containsKey(fontFileName)) {
            typeface = TYPEFACES.get(fontFileName);
        } else {
            typeface = Typeface.createFromAsset(assets, fontFileName);
            TYPEFACES.put(fontFileName, typeface);
        }

        return typeface;
    }
}
