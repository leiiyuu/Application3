package com.example.application3.utils

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    private val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    fun format(timestamp: Long): String = format.format(Date(timestamp))
}