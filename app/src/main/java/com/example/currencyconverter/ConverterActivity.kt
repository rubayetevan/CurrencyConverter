package com.example.currencyconverter

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.currencyconverter.Database.CurrencyDB
import com.example.currencyconverter.Database.CurrencyVatModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat


class ConverterActivity : AppCompatActivity() {

    private val TAG = "ConverterActivity"
    private lateinit var currencyDB: CurrencyDB

    private var standardRateRadioBtnID: Int? = null
    private var parkingRateRadioBtnID: Int? = null
    private var super_reducedRateRadioBtnID: Int? = null
    private var reducedRateRadioBtnID: Int? = null
    private var reduced1RateRadioBtnID: Int? = null
    private var reduced2RateRadioBtnID: Int? = null
    private var rateSelectedID: Int? = null
    private var currentVateRate: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        currencyDB = CurrencyDB.getInstance(this@ConverterActivity)
        showCountryList()
        currencyAmountET?.easyOnTextChangedListener { it ->
            showAndCalculateCurrency(it?.toString())
        }

        showAcknowledgement()


    }

    private fun showAcknowledgement() {
        try {
            val pref = getSharedPreferences(Constants.name_sharedPref, 0)
            val dbUpdateDateString = pref?.getString(Constants.key_db_update_date, "12/13/1912")
            val dbUpdateDate = SimpleDateFormat("MM/dd/yyyy").parse(dbUpdateDateString)

            val formatter = SimpleDateFormat("dd-MMMM-yyyy")
            val dt: String = formatter.format(dbUpdateDate)

            nbTV?.text ="${getString(R.string.acknowledgement)} $dt"

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun showCountryList() {
        doAsync {
            val countryList = currencyDB.currencyVatDao().getAllCountryList()
            uiThread {
                val countryAdapter =
                    ArrayAdapter(this@ConverterActivity, android.R.layout.simple_spinner_item, countryList)
                countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                countrySpinner.adapter = countryAdapter

                countrySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedCountryName = countryList[position]

                        Log.d(TAG, "selectedCountryName= $selectedCountryName")

                        doAsync {
                            val rates = currencyDB.currencyVatDao().getRatesByCountryName(selectedCountryName)
                            uiThread {
                                Log.d(TAG, "rates= $rates")
                                if (!rates.isNullOrEmpty()) {
                                    showRateOption(rates[0])
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showRateOption(rate: CurrencyVatModel) {
        val radioGroup = RadioGroup(this@ConverterActivity)
        radioGroup.orientation = LinearLayout.VERTICAL
        radioGroup.removeAllViews()


        if (rate?.standard!! > 0) {
            standardRateRadioBtnID = createRadioButton(radioGroup, "standard", rate?.standard!!)
        }

        if (rate?.parking!! > 0) {
            parkingRateRadioBtnID = createRadioButton(radioGroup, "parking", rate?.parking!!)
        }

        if (rate?.super_reduced!! > 0) {
            super_reducedRateRadioBtnID = createRadioButton(radioGroup, "super_reduced", rate?.super_reduced!!)
        }

        if (rate?.reduced!! > 0) {
            reducedRateRadioBtnID = createRadioButton(radioGroup, "reduced", rate?.reduced!!)
        }

        if (rate?.reduced1!! > 0) {
            reduced1RateRadioBtnID = createRadioButton(radioGroup, "reduced1", rate?.reduced1!!)
        }

        if (rate?.reduced2!! > 0) {
            reduced2RateRadioBtnID = createRadioButton(radioGroup, "reduced2", rate?.reduced2!!)
        }

        rateRadioGroup?.removeAllViews()
        rateRadioGroup?.addView(radioGroup)

        standardRateRadioBtnID?.let {
            radioGroup?.check(it)
            rateSelectedID = it
            convertCurrency(it, rate)
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            rateSelectedID = checkedId
            convertCurrency(checkedId, rate)
        }

    }

    private fun convertCurrency(checkedId: Int, rate: CurrencyVatModel) {
        when (checkedId) {
            standardRateRadioBtnID -> {
                calculateCurrencyBasedOnUserInput(rate.standard)
            }
            parkingRateRadioBtnID -> {
                calculateCurrencyBasedOnUserInput(rate.parking)
            }
            super_reducedRateRadioBtnID -> {
                calculateCurrencyBasedOnUserInput(rate.super_reduced)
            }
            reducedRateRadioBtnID -> {
                calculateCurrencyBasedOnUserInput(rate.reduced)
            }
            reduced1RateRadioBtnID -> {
                calculateCurrencyBasedOnUserInput(rate.reduced1)
            }
            reduced2RateRadioBtnID -> {
                calculateCurrencyBasedOnUserInput(rate.reduced2)
            }
        }
    }

    private fun calculateCurrencyBasedOnUserInput(vatRate: Double?) {
        currentVateRate = vatRate
        showAndCalculateCurrency(currencyAmountET?.text?.toString())
    }

    private fun showAndCalculateCurrency(amount: String?) {
        var convertedCurrency = 0.0
        var tax = 0.0
        var amountD = 0.0

        if (amount?.isNotBlank()!!) {
            amountD = amount?.toDouble()
            tax = amountD * (currentVateRate!! / 100.00)
            convertedCurrency = amountD + tax
        } else {
            tax = 0.0
            convertedCurrency = 0.0
            amountD=0.0
        }

        originalAmountTV.text = "$amountD"
        taxTV.text = "$tax"
        convertedCurrecyTV?.text = "$convertedCurrency"

        Log.d(TAG, "originalAmount: $amount \ntax: $tax \nconvertedCurrency: $convertedCurrency")

    }


    private fun createRadioButton(radioGroup: RadioGroup, radioButtonText: String, rate: Double): Int {
        val radioButton = RadioButton(this@ConverterActivity)
        radioButton.text = "$radioButtonText ($rate%)"
        radioButton.id = View.generateViewId()
        radioGroup.addView(radioButton)
        return radioButton.id
    }


}
