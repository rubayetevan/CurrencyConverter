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
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread


class ConverterActivity : AppCompatActivity() {

    private val TAG = "ConverterActivity"
    lateinit var currencyDB: CurrencyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        currencyDB = CurrencyDB.getInstance(this@ConverterActivity)
        showCountryList()
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
                                showRateOption(rates)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showRateOption(rates: List<CurrencyVatModel>) {
        val radioGroup = RadioGroup(this@ConverterActivity)
        radioGroup.orientation = LinearLayout.VERTICAL
        radioGroup.removeAllViews()

        var standardRateRadioBtnID: Int? = null
        var parkingRateRadioBtnID: Int? = null
        var super_reducedRateRadioBtnID: Int? = null
        var reducedRateRadioBtnID: Int? = null
        var reduced1RateRadioBtnID: Int? = null
        var reduced2RateRadioBtnID: Int? = null

        if (rates?.get(0)?.standard!! > 0) {
            standardRateRadioBtnID = createRadioButton(radioGroup, "standard")
        }

        if (rates?.get(0)?.parking!! > 0) {
            parkingRateRadioBtnID=createRadioButton(radioGroup, "parking")
        }

        if (rates?.get(0)?.super_reduced!! > 0) {
            super_reducedRateRadioBtnID=createRadioButton(radioGroup, "super_reduced")
        }

        if (rates?.get(0)?.reduced!! > 0) {
            reducedRateRadioBtnID=createRadioButton(radioGroup, "reduced")
        }

        if (rates?.get(0)?.reduced1!! > 0) {
            reduced1RateRadioBtnID=createRadioButton(radioGroup, "reduced1")
        }

        if (rates?.get(0)?.reduced2!! > 0) {
            reduced2RateRadioBtnID=createRadioButton(radioGroup, "reduced2")
        }

        rateRadioGroup?.removeAllViews()
        rateRadioGroup?.addView(radioGroup)
        standardRateRadioBtnID?.let {
            radioGroup?.check(it)
            toast("standardRateRadioBtnID: ${rates?.get(0)?.standard}")
        }


        radioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId){
                standardRateRadioBtnID->{
                    toast("standardRateRadioBtnID: ${rates?.get(0)?.standard}")
                }
                parkingRateRadioBtnID->{
                    toast("parkingRateRadioBtnID: ${rates?.get(0)?.parking}")
                }
                super_reducedRateRadioBtnID->{
                    toast("super_reducedRateRadioBtnID: ${rates?.get(0)?.super_reduced}")
                }
                reducedRateRadioBtnID->{
                    toast("reducedRateRadioBtnID: ${rates?.get(0)?.reduced}")
                }
                reduced1RateRadioBtnID->{
                    toast("reduced1RateRadioBtnID: ${rates?.get(0)?.reduced1}")
                }
                reduced2RateRadioBtnID->{
                    toast("reduced2RateRadioBtnID :${rates?.get(0)?.reduced2}")
                }

            }

        }



    }

    private fun createRadioButton(radioGroup: RadioGroup, radioButtonText: String): Int {
        val rdbtn = RadioButton(this@ConverterActivity)
        rdbtn.text = radioButtonText
        rdbtn.id = View.generateViewId()
        radioGroup.addView(rdbtn)
        return rdbtn.id
    }


}
