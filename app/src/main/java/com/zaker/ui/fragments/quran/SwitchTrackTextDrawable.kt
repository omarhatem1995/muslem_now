package com.zaker.ui.fragments.quran

import android.R
import android.content.Context
import android.graphics.*

import androidx.annotation.StringRes

import android.graphics.drawable.Drawable
import androidx.annotation.NonNull


class SwitchTrackTextDrawable(
    @NonNull context: Context,
    @StringRes leftTextId: Int,
    @StringRes rightTextId: Int
) : Drawable() {
    private val mContext: Context
    private val mLeftText: String
    private val mRightText: String
    private val mTextPaint: Paint
    private fun createTextPaint(): Paint {
        val textPaint = Paint()
        textPaint.setColor(mContext.getResources().getColor(R.color.white))
        textPaint.setAntiAlias(true)
        textPaint.setStyle(Paint.Style.FILL)
        textPaint.setTextAlign(Paint.Align.CENTER)
        // Set textSize, typeface, etc, as you wish
        return textPaint
    }

    override fun draw(canvas: Canvas) {
        val textBounds = Rect()
        mTextPaint.getTextBounds(mRightText, 0, mRightText.length, textBounds)

        // The baseline for the text: centered, including the height of the text itself
        val heightBaseline: Float = canvas.clipBounds.height() / 2 + textBounds.height() / 2 .toFloat()

        // This is one quarter of the full width, to measure the centers of the texts
        val widthQuarter: Float = canvas.clipBounds.width() / 4 .toFloat()
        canvas.drawText(
            mLeftText, 0, mLeftText.length,
            widthQuarter, heightBaseline,
            mTextPaint
        )
        canvas.drawText(
            mRightText, 0, mRightText.length,
            widthQuarter * 3, heightBaseline,
            mTextPaint
        )
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(cf: ColorFilter?) {}
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    init {
        mContext = context

        // Left text
        mLeftText = context.getString(leftTextId)
        mTextPaint = createTextPaint()

        // Right text
        mRightText = context.getString(rightTextId)
    }
}