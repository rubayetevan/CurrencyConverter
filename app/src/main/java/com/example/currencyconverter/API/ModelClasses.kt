package com.example.currencyconverter.API
import com.google.gson.annotations.SerializedName


data class CurrencyVatModel(
    @SerializedName("details")
    var details: String? = "",
    @SerializedName("rates")
    var rates: List<Rate?>? = listOf(),
    @SerializedName("version")
    var version: Any? = Any()
)

data class Rate(
    @SerializedName("code")
    var code: String? = "",
    @SerializedName("country_code")
    var countryCode: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("periods")
    var periods: List<Period?>? = listOf()
)

data class Period(
    @SerializedName("effective_from")
    var effectiveFrom: String? = "",
    @SerializedName("rates")
    var rates: Rates? = Rates()
)

data class Rates(
    @SerializedName("parking")
    var parking: Double? = 0.0,

    @SerializedName("super_reduced")
    var super_reduced: Double? = 0.0,

    @SerializedName("reduced")
    var reduced: Double? = 0.0,

    @SerializedName("reduced1")
    var reduced1: Double? = 0.0,

    @SerializedName("reduced2")
    var reduced2: Double? = 0.0,

    @SerializedName("standard")
    var standard: Double? = 0.0
)