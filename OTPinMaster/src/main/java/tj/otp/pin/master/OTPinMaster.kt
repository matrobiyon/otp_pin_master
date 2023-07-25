package tj.otp.pin.master

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.ActionMode
import androidx.appcompat.widget.AppCompatEditText
import kotlin.math.min


class OTPinMaster : AppCompatEditText {

    private var mSpace = 40f // space between the rectangles
    private var mNumMaxLength = 4 // maximum characters
    private var mTextBottomMargin =
        60f // height of the text from our lines. Text's margin from bottom

    private var backgroundResId : Int? = 0
    private var mRectangleWidth = 150
    private var mRectangleHeight = 200
    private var rectangleCornerRadius = 30f
    private var isCorrect = false
    private var density = 0

    private var rectanglePaint: Paint? = null
    private var newRectF: RectF? = null

    private val mPadding = 30

    private var textColor: Int = Color.BLACK
    private var activeRectangleColor: Int = Color.GREEN
    private var inactiveRectangleColor: Int = Color.GRAY
    private var errorRectangleColor: Int = Color.RED

    private var isPin: Boolean = false
    private var circleRadius: Float = 10f
    private var circleColor: Int = Color.BLACK

    private var animValue = 0f
    private var animator = ValueAnimator.ofFloat(0.0f, 1.0f)

    private var notCorrectAnimValue = 0f
    private var notCorrectAnimator = ValueAnimator.ofFloat(0.0f, 1f)
    private var shakePaddings = 50f

    private var inputDigitIndex = 0
    private var mClickListener: OnClickListener? = null
    private var onDoneListener: ((Int) -> Unit)? = null

    constructor(context: Context?) : super(context!!)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    override fun setBackground(background: Drawable?) {
    }

    private fun init(context: Context, attrs: AttributeSet?) {

        density = context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.OTPinMaster)

        minimumHeight = 220 / density
        minWidth = 600 / density
        setPadding(0,5,0,5)

        this.mRectangleWidth =
            typedArray.getDimensionPixelSize(
                R.styleable.OTPinMaster_rectangleWidth,
                mRectangleWidth
            )
        this.mRectangleHeight =
            typedArray.getDimensionPixelSize(
                R.styleable.OTPinMaster_rectangleHeight,
                mRectangleHeight
            )
        this.mTextBottomMargin =
            typedArray.getDimension(R.styleable.OTPinMaster_textBottomPadding, mTextBottomMargin)

        this.activeRectangleColor =
            typedArray.getColor(R.styleable.OTPinMaster_activeRectangleColor, activeRectangleColor)
        this.inactiveRectangleColor = typedArray.getColor(
            R.styleable.OTPinMaster_inactiveRectangleColor,
            inactiveRectangleColor
        )
        this.errorRectangleColor =
            typedArray.getColor(R.styleable.OTPinMaster_errorRectangleColor, errorRectangleColor)

        this.circleRadius =
            typedArray.getDimension(R.styleable.OTPinMaster_circleRadius, circleRadius)
        this.circleColor = typedArray.getColor(R.styleable.OTPinMaster_circleColor, circleColor)
        this.isPin = typedArray.getBoolean(R.styleable.OTPinMaster_isPin, isPin)

        this.mSpace = typedArray.getDimension(R.styleable.OTPinMaster_rectangleSpace, mSpace)
        val maxLength = typedArray.getInt(R.styleable.OTPinMaster_rectangleCount, mNumMaxLength)
        this.mNumMaxLength = if (maxLength > 7) mNumMaxLength else maxLength

        typedArray.recycle()

        mSpace /= density //convert to dp for our density
        mTextBottomMargin /= density
        mRectangleWidth /= density
        mRectangleHeight /= density
        shakePaddings /= density

        rectanglePaint = Paint()
        rectanglePaint?.isAntiAlias = true
        rectanglePaint?.color = activeRectangleColor
        rectanglePaint?.strokeWidth = 5.0f
        rectanglePaint?.style = Paint.Style.STROKE
        newRectF = RectF()

        setMaxLength()

        inputType = InputType.TYPE_CLASS_NUMBER
        val filters = arrayOfNulls<InputFilter>(1)
        filters[0] = LengthFilter(mNumMaxLength)
        setFilters(filters)
        textSize = 42f / density
        setBackgroundResource(androidx.appcompat.R.color.abc_decor_view_status_guard_light)

        //Animation characters while input
        animator.duration = 200 // Animation duration in milliseconds
        animator.repeatCount = 0
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener { animation: ValueAnimator ->
            animValue = animation.animatedValue as Float
            invalidate()
        }

        notCorrectAnimator.duration = 1000
        notCorrectAnimator.repeatCount = 0
        notCorrectAnimator.repeatMode = ValueAnimator.REVERSE

        notCorrectAnimator.addUpdateListener { animation: ValueAnimator ->
            notCorrectAnimValue = animation.animatedValue as Float
            if (animation.animatedValue as Float >= 0.99f) {
                isCorrect = true
                text = null
            }
            invalidate()
        }

        super.setOnClickListener { v -> // When tapped, move cursor to end of text.
            setSelection(text!!.length)
            if (mClickListener != null) {
                mClickListener!!.onClick(v)
            }
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    override fun setBackgroundResource(resId: Int) {
        backgroundResId = resId
        invalidate()
    }

    override fun setCustomSelectionActionModeCallback(actionModeCallback: ActionMode.Callback?) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
    }

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (getText()?.length == mNumMaxLength) {
            onDoneListener?.invoke(getText().toString().toInt())
            setIsCorrect(false)
        }
        if (lengthAfter > lengthBefore) {
            inputDigitIndex = text.length - 1
            animator.start()
            invalidate()
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        calculateRectangleHeight(heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun calculateRectangleHeight(heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode == MeasureSpec.EXACTLY) {
            mRectangleHeight = heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            mRectangleHeight = min(mRectangleHeight, heightSize)
        }
    }

    override fun onDraw(canvas: Canvas) {

        val totalPaddings = paddingLeft + (2 * mPadding) + paddingRight
        val mPositionedRectanglesWidth =
            (mNumMaxLength * mRectangleWidth) + (mSpace * (mNumMaxLength - 1))
        val leftFreeSpaces = width - totalPaddings - mPositionedRectanglesWidth

        var startX = paddingLeft + mPadding + (leftFreeSpaces / 2)
        val bottom = mRectangleHeight - paddingBottom

        if (!isCorrect) {
            startX += if (notCorrectAnimValue <= 0.2f) {
                shakeAnimation(shakePaddings, 0.1f)
            } else if (notCorrectAnimValue <= 0.4f) {
                shakeAnimation(shakePaddings, 0.3f)
            } else if (notCorrectAnimValue <= 0.6f) {
                shakeAnimation(shakePaddings, 0.5f)
            } else if (notCorrectAnimValue <= 0.8f) {
                shakeAnimation(shakePaddings, 0.7f)
            } else {
                shakeAnimation(shakePaddings, 0.9f)
            }
        }

        //Text Width
        val text = text
        val textLength = text!!.length
        val textWidths = FloatArray(textLength)
        paint.getTextWidths(getText(), 0, textLength, textWidths)
        paint.color = textColor

        var i = 0
        while (i < mNumMaxLength) {

            newRectF!![startX, paddingTop.toFloat(), startX + mRectangleWidth] =
                bottom.toFloat()

            if (getText()!!.length > i) {

                isCorrect()

                val middle = startX + mRectangleWidth / 2
                val bottomBaseline = bottom - mTextBottomMargin
                val animationState = (1 - animValue + 1) * bottomBaseline
                val finalBottomBaseline: Float = if (i == inputDigitIndex) {
                    animationState
                } else bottomBaseline

                if (isPin) {
                    drawCircle(startX, canvas, circleRadius!!, circleColor!!)
                } else {
                    canvas.drawText(
                        text, i, i + 1, middle - textWidths[0] / 2, finalBottomBaseline,
                        paint
                    )
                }
            } else {
                rectanglePaint!!.color = inactiveRectangleColor
            }
            canvas.drawRoundRect(
                newRectF!!,
                rectangleCornerRadius,
                rectangleCornerRadius,
                rectanglePaint!!
            )

            //Changing rectangle start position for next character
            startX += if (mSpace < 0) {
                (mRectangleWidth * 2).toInt()
            } else {
                (mRectangleWidth + mSpace).toInt()
            }
            i++
        }
    }

    private fun drawCircle(startX: Float, canvas: Canvas, circleRadius: Float, circleColor: Int) {
        val centerX = startX + mRectangleWidth / 2f
        val centerY = mRectangleHeight / 2f
        canvas.drawCircle(centerX, centerY, circleRadius, Paint().also {
            it.color = circleColor
            it.style = Paint.Style.FILL
        })
    }

    private fun isCorrect() {
        if (text!!.length >= mNumMaxLength && !isCorrect) {
            rectanglePaint!!.color = errorRectangleColor
        } else {
            rectanglePaint!!.color = activeRectangleColor
        }
    }


    // -----------------Private Functions -------------- //

    private fun setMaxLength() {
        val filters = arrayOfNulls<InputFilter>(1)
        filters[0] = LengthFilter(mNumMaxLength)
        setFilters(filters)
    }

    private fun shakeAnimation(mShakePadding: Float, halfPercent: Float): Float {
        return if (notCorrectAnimValue <= halfPercent) {
            (mShakePadding * (halfPercent - notCorrectAnimValue)) / 0.1f
        } else {
            mShakePadding * (((notCorrectAnimValue - halfPercent)) * 10)
        }
    }

    // ---------------------- Functions -------------- //

    override fun setTextColor(color: Int) {
        this.textColor = color
        super.setTextColor(color)
    }

    fun setRectangleWidth(px: Int) {
        this.mRectangleWidth = px / density
    }

    fun setRectangleHeight(px: Int) {
        this.mRectangleHeight = px * density
    }

    fun setIsCorrect(isCorrect: Boolean) {
        this.isCorrect = isCorrect
        if (!isCorrect && notCorrectAnimator != null)
            notCorrectAnimator.start()
    }

    fun setActiveRectangleColor(color: Int) {
        this.activeRectangleColor = color
    }

    fun setInActiveRectangleColor(color: Int) {
        this.inactiveRectangleColor = color
    }

    fun setErrorRectangleColor(color: Int) {
        this.errorRectangleColor = color
    }

    fun setRectangleSpace(dp: Float) {
        this.mSpace = dp / density
    }

    fun setRectangleCount(count: Int) {
        this.mNumMaxLength = if (count <= 7) count else mNumMaxLength
    }

    fun setCircleColor(color: Int) {
        this.circleColor = color
    }

    fun setCircleRadius(dp: Float) {
        this.circleRadius = dp / density
    }

    fun setIsPin(isPin: Boolean) {
        this.isPin = isPin
    }

    fun setTextBottomPadding(px: Float) {
        this.mTextBottomMargin = px / density
    }

    fun setOnDoneListener(onDoneListener: (Int) -> Unit) {
        this.onDoneListener = onDoneListener
    }
}