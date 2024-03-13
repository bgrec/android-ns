package com.mastrosql.app.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)//need to add this every time is used this function
fun formatDate(date: String?): String {

    if (date.isNullOrEmpty()) {
        return "" // Return empty string if Date is null
    }

    return try {
        // Assuming the expirationDate format is yyyy-MM-dd
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        LocalDate.parse(date, DateTimeFormatter.ISO_DATE).format(formatter)
    } catch (ex: DateTimeParseException) {
        "" // Return empty string if expirationDate is not in the expected format
    }
}