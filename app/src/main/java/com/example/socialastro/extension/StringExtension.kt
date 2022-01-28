package com.example.socialastro.extension


fun String.formatDateName(): String {

    val day = this.substring(8, 10)
    val year = this.substring(0, 4)


    val monthName: String
    when (this.substring(5, 7)) {
        "01" -> {
            monthName = "jan."
        }
        "02" -> {
            monthName = "fev."
        }
        "03" -> {
            monthName = "mar."
        }
        "04" -> {
            monthName = "abr."
        }
        "05" -> {
            monthName = "maio"
        }
        "06" -> {
            monthName = "jun."
        }
        "07" -> {
            monthName = "jul."
        }
        "08" -> {
            monthName = "ago."
        }
        "09" -> {
            monthName = "set."
        }
        "10" -> {
            monthName = "out."
        }
        "11" -> {
            monthName = "nov."
        }
        else -> {
            monthName = "dez."
        }
    }


    return "$day de $monthName de $year"

}

fun String.formatNameSite(): String {
    return this.substring(this.indexOf("//") + 2, this.lastIndexOf(".") + 4)
}