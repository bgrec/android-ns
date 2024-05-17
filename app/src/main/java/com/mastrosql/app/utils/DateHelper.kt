package com.mastrosql.app.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class DateHelper {

    companion object {

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

        fun isDateBeforeToday(date: String?): Boolean {
            if (date.isNullOrEmpty()) {
                return false // Return false if Date is null or empty
            }

            return try {
                val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val parsedDate = LocalDate.parse(date, inputFormatter)
                val today = LocalDate.now()
                parsedDate.isBefore(today)
            } catch (ex: DateTimeParseException) {
                false // Return false if the date is not in the expected format
            }
        }
    }
}
