package com.telenord.cobrosapp.util

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern


/**
 * Input filter that limits the number of decimal digits that are allowed to be
 * entered.
 */
class DecimalDigitsInputFilter(digitsBeforeZero: Int, digitsAfterZero: Int) : InputFilter {

    internal var mPattern: Pattern
    private var mDigitsBeforeZero: Int = 0
    private var mDigitsAfterZero: Int = 0

    private val DIGITS_BEFORE_ZERO_DEFAULT = 5
    private val DIGITS_AFTER_ZERO_DEFAULT = 2
    init {
        this.mDigitsBeforeZero = digitsBeforeZero ?: DIGITS_BEFORE_ZERO_DEFAULT
        this.mDigitsAfterZero = digitsAfterZero ?: DIGITS_AFTER_ZERO_DEFAULT
        mPattern = Pattern.compile("-?[0-9]{0," + mDigitsBeforeZero + "}+((\\.[0-9]{0," + mDigitsAfterZero
                + "})?)||(\\.)?")
    }

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        val replacement = source.subSequence(start, end).toString()
        val newVal = (dest.subSequence(0, dstart).toString() + replacement
                + dest.subSequence(dend, dest.length).toString())
        val matcher = mPattern.matcher(newVal)
        try {
            val input = (dest.toString() + source.toString()).toDouble()
            if (input >=1 && matcher.matches())
                return null
        } catch (nfe: NumberFormatException) {
        }

        return ""
    }
}