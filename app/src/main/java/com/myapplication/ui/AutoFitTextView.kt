package com.myapplication.ui

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.text.Layout.Alignment
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView


class AutoFitTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyle) {
    private val _availableSpaceRect = RectF()
    private val _sizeTester: SizeTester
    private var _maxTextSize: Float = 0.toFloat()
    private var _spacingMult = 1.0f
    private var _spacingAdd = 0.0f
    private var _minTextSize: Float = 0.toFloat()
    private var _widthLimit: Int = 0
    private var _maxLines: Int = 0
    private var _initialized = false
    private var _paint: TextPaint? = null

    init {
        _minTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1f, resources.displayMetrics)
        _maxTextSize = textSize
        _paint = TextPaint(paint)
        if (_maxLines == 0)
            _maxLines = NO_LINE_LIMIT
        _sizeTester = object : SizeTester {
            val textRect = RectF()

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onTestSize(suggestedSize: Int, availableSpace: RectF): Int {
                _paint!!.textSize = suggestedSize.toFloat()
                val transformationMethod = transformationMethod
                val text: String
                if (transformationMethod != null)
                    text = transformationMethod.getTransformation(getText(), this@AutoFitTextView).toString()
                else
                    text = getText().toString()
                val singleLine = maxLines == 1
                if (singleLine) {
                    textRect.bottom = _paint!!.fontSpacing
                    textRect.right = _paint!!.measureText(text)
                } else {
                    val layout =
                        StaticLayout(text, _paint, _widthLimit, Alignment.ALIGN_NORMAL, _spacingMult, _spacingAdd, true)
                    if (maxLines != NO_LINE_LIMIT && layout.lineCount > maxLines)
                        return 1
                    textRect.bottom = layout.height.toFloat()
                    var maxWidth = -1
                    val lineCount = layout.lineCount
                    for (i in 0 until lineCount) {
                        val end = layout.getLineEnd(i)
                        if (i < lineCount - 1 && end > 0 && !isValidWordWrap(text[end - 1]))
                            return 1
                        if (maxWidth < layout.getLineRight(i) - layout.getLineLeft(i))
                            maxWidth = layout.getLineRight(i).toInt() - layout.getLineLeft(i).toInt()
                    }
                    textRect.right = maxWidth.toFloat()
                }
                textRect.offsetTo(0f, 0f)
                return if (availableSpace.contains(textRect)) -1 else 1
            }
        }
        _initialized = true
    }

    fun isValidWordWrap(before: Char): Boolean {
        return before == ' ' || before == '-'
    }

    override fun setAllCaps(allCaps: Boolean) {
        super.setAllCaps(allCaps)
        adjustTextSize()
    }

    override fun setTypeface(tf: Typeface?) {
        super.setTypeface(tf)
        adjustTextSize()
    }

    override fun setTextSize(size: Float) {
        _maxTextSize = size
        adjustTextSize()
    }

    override fun getMaxLines(): Int {
        return _maxLines
    }

    override fun setMaxLines(maxLines: Int) {
        super.setMaxLines(maxLines)
        _maxLines = maxLines
        adjustTextSize()
    }

    override fun setSingleLine() {
        super.setSingleLine()
        _maxLines = 1
        adjustTextSize()
    }

    override fun setSingleLine(singleLine: Boolean) {
        super.setSingleLine(singleLine)
        _maxLines = if (singleLine)
            1
        else
            NO_LINE_LIMIT
        adjustTextSize()
    }

    override fun setLines(lines: Int) {
        super.setLines(lines)
        _maxLines = lines
        adjustTextSize()
    }

    override fun setTextSize(unit: Int, size: Float) {
        val c = context
        val r: Resources
        r = if (c == null)
            Resources.getSystem()
        else
            c.resources
        _maxTextSize = TypedValue.applyDimension(unit, size, r.displayMetrics)
        adjustTextSize()
    }

    override fun setLineSpacing(add: Float, mult: Float) {
        super.setLineSpacing(add, mult)
        _spacingMult = mult
        _spacingAdd = add
    }

    private fun adjustTextSize() {
        if (!_initialized)
            return
        val startSize = _minTextSize.toInt()
        val heightLimit = measuredHeight - compoundPaddingBottom - compoundPaddingTop
        _widthLimit = measuredWidth - compoundPaddingLeft - compoundPaddingRight
        if (_widthLimit <= 0)
            return
        _paint = TextPaint(paint)
        _availableSpaceRect.right = _widthLimit.toFloat()
        _availableSpaceRect.bottom = heightLimit.toFloat()
        superSetTextSize(startSize)
    }

    private fun superSetTextSize(startSize: Int) {
        val textSize = binarySearch(startSize, _maxTextSize.toInt(), _sizeTester, _availableSpaceRect)
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
    }

    private fun binarySearch(start: Int, end: Int, sizeTester: SizeTester, availableSpace: RectF): Int {
        var lastBest = start
        var lo = start
        var hi = end - 1
        var mid: Int
        while (lo <= hi) {
            mid = (lo + hi).ushr(1)
            val midValCmp = sizeTester.onTestSize(mid, availableSpace)
            when {
                midValCmp < 0 -> {
                    lastBest = lo
                    lo = mid + 1
                }
                midValCmp > 0 -> {
                    hi = mid - 1
                    lastBest = hi
                }
                else -> return mid
            }
        }
        // make sure to return last best
        // this is what should always be returned
        return lastBest
    }

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, after: Int) {
        super.onTextChanged(text, start, before, after)
        adjustTextSize()
    }

    override fun onSizeChanged(width: Int, height: Int, oldwidth: Int, oldheight: Int) {
        super.onSizeChanged(width, height, oldwidth, oldheight)
        if (width != oldwidth || height != oldheight)
            adjustTextSize()
    }

    private interface SizeTester {
        /**
         * @param suggestedSize  Size of text to be tested
         * @param availableSpace available space in which text must fit
         * @return an integer < 0 if after applying `suggestedSize` to
         * text, it takes less space than `availableSpace`, > 0
         * otherwise
         */
        fun onTestSize(suggestedSize: Int, availableSpace: RectF): Int
    }

    companion object {
        private const val NO_LINE_LIMIT = -1
    }
}
