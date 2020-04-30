package org.salient.artplayer.ui

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView
import android.view.View
import org.salient.artplayer.conduction.ScaleType

/**
 * 用于显示video的，做了横屏与竖屏的匹配，还有需要rotation需求的
 */
class ResizeTextureView : TextureView {
    private var mVideoWidth = 0
    private var mVideoHeight = 0
    private var screenType: ScaleType? = ScaleType.DEFAULT

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    fun setVideoSize(width: Int, height: Int) {
        mVideoWidth = width
        mVideoHeight = height
        requestLayout()
    }

    fun setScreenScale(type: ScaleType?) {
        screenType = type
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) { //        Log.i("@@@@", "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", "
//                + MeasureSpec.toString(heightMeasureSpec) + ")");
        var widthMeasureSpec = widthMeasureSpec
        var heightMeasureSpec = heightMeasureSpec
        if (rotation == 90f || rotation == 270f) { // 软解码时处理旋转信息，交换宽高
            widthMeasureSpec = widthMeasureSpec + heightMeasureSpec
            heightMeasureSpec = widthMeasureSpec - heightMeasureSpec
            widthMeasureSpec = widthMeasureSpec - heightMeasureSpec
        }
        var width = View.getDefaultSize(mVideoWidth, widthMeasureSpec)
        var height = View.getDefaultSize(mVideoHeight, heightMeasureSpec)
        when (screenType) {
            ScaleType.SCALE_ORIGINAL -> {
                width = mVideoWidth
                height = mVideoHeight
            }
            ScaleType.SCALE_16_9 -> if (height > width / 16 * 9) {
                height = width / 16 * 9
            } else {
                width = height / 9 * 16
            }
            ScaleType.SCALE_4_3 -> if (height > width / 4 * 3) {
                height = width / 4 * 3
            } else {
                width = height / 3 * 4
            }
            ScaleType.SCALE_MATCH_PARENT -> {
                width = widthMeasureSpec
                height = heightMeasureSpec
            }
            ScaleType.SCALE_CENTER_CROP -> if (mVideoWidth > 0 && mVideoHeight > 0) {
                if (mVideoWidth * height > width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight
                } else {
                    height = width * mVideoHeight / mVideoWidth
                }
            }
            ScaleType.DEFAULT -> if (mVideoWidth > 0 && mVideoHeight > 0) {
                val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
                val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
                val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
                val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
                if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) { // the size is fixed
                    width = widthSpecSize
                    height = heightSpecSize
                    // for compatibility, we adjust size based on aspect ratio
                    if (mVideoWidth * height < width * mVideoHeight) { //Log.i("@@@", "image too wide, correcting");
                        width = height * mVideoWidth / mVideoHeight
                    } else if (mVideoWidth * height > width * mVideoHeight) { //Log.i("@@@", "image too tall, correcting");
                        height = width * mVideoHeight / mVideoWidth
                    }
                } else if (widthSpecMode == MeasureSpec.EXACTLY) { // only the width is fixed, adjust the height to match aspect ratio if possible
                    width = widthSpecSize
                    height = width * mVideoHeight / mVideoWidth
                    if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) { // couldn't match aspect ratio within the constraints
                        height = heightSpecSize
                    }
                } else if (heightSpecMode == MeasureSpec.EXACTLY) { // only the height is fixed, adjust the width to match aspect ratio if possible
                    height = heightSpecSize
                    width = height * mVideoWidth / mVideoHeight
                    if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) { // couldn't match aspect ratio within the constraints
                        width = widthSpecSize
                    }
                } else { // neither the width nor the height are fixed, try to use actual video size
                    width = mVideoWidth
                    height = mVideoHeight
                    if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) { // too tall, decrease both width and height
                        height = heightSpecSize
                        width = height * mVideoWidth / mVideoHeight
                    }
                    if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) { // too wide, decrease both width and height
                        width = widthSpecSize
                        height = width * mVideoHeight / mVideoWidth
                    }
                }
            } else { // no size yet, just adopt the given spec sizes
            }
        }
        setMeasuredDimension(width, height)
    }
}