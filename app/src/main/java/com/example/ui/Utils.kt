package com.example.ui

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale
import java.util.Locale

fun String.toPersianDigits(): String {
    val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val builder = StringBuilder()
    for (char in this) {
        if (char in '0'..'9') {
            builder.append(persianDigits[char - '0'])
        } else {
            builder.append(char)
        }
    }
    return builder.toString()
}

fun formatCurrencyFa(amount: Double): String {
    if (amount == 0.0) return "۰ تومان"
    val formatted = String.format(Locale.US, "%,.0f", amount)
    return formatted.toPersianDigits() + " تومان"
}

fun formatNumberFa(number: Int): String {
    if (number == 0) return "۰"
    val formatted = String.format(Locale.US, "%,d", number)
    return formatted.toPersianDigits()
}

fun getTodayFaDate(): String {
    val locale = ULocale("fa_IR@calendar=persian")
    val calendar = Calendar.getInstance(locale)
    val format = SimpleDateFormat("EEEE، d MMMM yyyy", locale)
    return format.format(calendar.time)
}

fun getTodayYMD(): String {
    val cal = java.util.Calendar.getInstance()
    val y = cal.get(java.util.Calendar.YEAR)
    val m = String.format(Locale.US, "%02d", cal.get(java.util.Calendar.MONTH) + 1)
    val d = String.format(Locale.US, "%02d", cal.get(java.util.Calendar.DAY_OF_MONTH))
    return "$y-$m-$d"
}

fun getYesterdayYMD(): String {
    val cal = java.util.Calendar.getInstance()
    cal.add(java.util.Calendar.DAY_OF_MONTH, -1)
    val y = cal.get(java.util.Calendar.YEAR)
    val m = String.format(Locale.US, "%02d", cal.get(java.util.Calendar.MONTH) + 1)
    val d = String.format(Locale.US, "%02d", cal.get(java.util.Calendar.DAY_OF_MONTH))
    return "$y-$m-$d"
}
