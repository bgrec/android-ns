package com.mastrosql.app.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class DateHelper {

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun formatDateToDisplay(date: String?): String {
            if (date.isNullOrEmpty()) {
                return "" // Return empty string if Date is null
            }

            return try {
                // Assuming the expirationDate format is yyyy-MM-dd
                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val parsedDate = LocalDate.parse(date, inputFormatter)
                parsedDate.format(outputFormatter)
            } catch (ex: DateTimeParseException) {
                "" // Return empty string if expirationDate is not in the expected format
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun formatDateToInput(date: String?): String {
            if (date.isNullOrEmpty()) {
                return "" // Return empty string if Date is null
            }

            return try {
                // Assuming the expirationDate format is dd/MM/yyyy
                val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val parsedDate = LocalDate.parse(date, inputFormatter)
                parsedDate.format(outputFormatter)
            } catch (ex: DateTimeParseException) {
                "" // Return empty string if expirationDate is not in the expected format
            }
        }
    }
}

/*@RequiresApi(Build.VERSION_CODES.O)//need to add this every time is used this function
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
}*/