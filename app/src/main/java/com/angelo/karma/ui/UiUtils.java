package com.angelo.karma.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

public class UiUtils {

    public void UiUtils(){

    }

    public static int themeAttributeToColor(int themeAttributeId,
                                            Context context,
                                            int fallbackColorId) {
        TypedValue outValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        boolean wasResolved =
                theme.resolveAttribute(
                        themeAttributeId, outValue, true);
        if (wasResolved) {
            @ColorInt int color = outValue.data;
            return color;
        } else {
            // fallback colour handling
            return fallbackColorId;
        }
    }
}
