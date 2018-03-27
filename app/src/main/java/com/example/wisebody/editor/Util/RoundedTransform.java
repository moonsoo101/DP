package com.example.wisebody.editor.Util;

/**
 * Created by wisebody on 2017. 2. 9..
 */

import android.graphics.Bitmap;

import android.graphics.Bitmap.Config;

import android.graphics.BitmapShader;

import android.graphics.Canvas;

import android.graphics.Paint;

import android.graphics.RectF;

import android.graphics.Shader;

import com.squareup.picasso.Transformation;


// enables hardware accelerated rounded corners

// original idea here : http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/

public class RoundedTransform implements Transformation
{
        private final int radius;

        public RoundedTransform(int radius) {
            this.radius = radius;
        }

        @Override public Bitmap transform(Bitmap source) {

            Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(result);
            Paint shaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            shaderPaint.setShader(shader);

            canvas.drawRoundRect(new RectF(0, 0, source.getWidth(), source.getHeight()), radius, radius, shaderPaint);

            if (source != result) {
                source.recycle();
            }
            return result;
        }

        @Override public String key() {
            return RoundedTransform.class.getCanonicalName();
        }
}