package com.example.wisebody.editor.Util;

import android.content.Context;

/**
 * Created by wisebody on 2017. 2. 7..
 */

public class Util {


    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
