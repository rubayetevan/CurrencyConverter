package com.example.currencyconverter

import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.currencyconverter.API.ApiService
import com.example.currencyconverter.Constants.Companion.key_db_update_date
import com.example.currencyconverter.Constants.Companion.name_sharedPref
import com.example.currencyconverter.Database.CurrencyDB
import com.example.currencyconverter.Database.CurrencyVatModel
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*


class SplashActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {


    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null
    private var disposable: Disposable? = null
    private val TAG = "SplashActivity"
    lateinit var currencyDB: CurrencyDB
    private var pref: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        currencyDB = CurrencyDB.getInstance(this@SplashActivity)
        pref = getSharedPreferences(name_sharedPref, 0)
    }


    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        val dbUpdateDateString = pref?.getString(key_db_update_date, "12/13/1912")
        val dbUpdateDate = SimpleDateFormat("MM/dd/yyyy").parse(dbUpdateDateString)

        val date = Date()
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val today: String = formatter.format(date)
        val todayDate = SimpleDateFormat("MM/dd/yyyy").parse(today)

        if (todayDate > dbUpdateDate) {
            if (isConnected) {
                mSnackBar?.dismiss()
                insertDataToInternalDB()
                Log.d(TAG, "stage: insertDataToInternalDB")
            } else {
                takeDBUpdateDecision(isConnected)
                Log.d(TAG, "stage: takeDBUpdateDecision1")
            }
        } else {
            takeDBUpdateDecision(isConnected)
            Log.d(TAG, "stage: takeDBUpdateDecision2")
        }
    }

    private fun takeDBUpdateDecision(isConnected: Boolean) {
        doAsync {
            val currencyVatModelList = currencyDB.currencyVatDao().getAllCurrencyVatModel()
            uiThread {
                if (currencyVatModelList.isNullOrEmpty()) {
                    if (isConnected) {
                        mSnackBar?.dismiss()
                        insertDataToInternalDB()
                    } else {
                        showInternetConnectionAlert()
                    }
                } else {
                    goToNextPage()
                }
            }
        }
    }

    private fun showInternetConnectionAlert() {
        mSnackBar = Snackbar
            .make(splashActivityCL, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.turn_on_wifi)) {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
        mSnackBar?.show()
    }

    private fun goToNextPage() {
        val intent = Intent(this@SplashActivity, ConverterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finishAffinity()
    }

    private fun insertDataToInternalDB() {
        disposable = ApiService.create().getCurrencyList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->

                Log.d(TAG, "result size= ${result.rates?.size} \ngot result-> $result")

                doAsync {
                    currencyDB?.currencyVatDao()?.deleteAllCurrencyVatModel()
                    result?.rates?.forEach {
                        val currencyVatModel = CurrencyVatModel()

                        currencyVatModel.name = it?.name
                        currencyVatModel.code = it?.code
                        currencyVatModel.country_code = it?.countryCode

                        it?.periods?.forEach {
                            currencyVatModel.effective_from = it?.effectiveFrom
                            currencyVatModel.parking = it?.rates?.parking
                            currencyVatModel.standard = it?.rates?.standard
                            currencyVatModel.super_reduced = it?.rates?.super_reduced
                            currencyVatModel.reduced = it?.rates?.reduced
                            currencyVatModel.reduced1 = it?.rates?.reduced1
                            currencyVatModel.reduced2 = it?.rates?.reduced2
                        }
                        currencyDB?.currencyVatDao().insertCurrencyVatModel(currencyVatModel)
                    }

                    uiThread {

                        val date = Date()
                        val formatter = SimpleDateFormat("MM/dd/yyyy")
                        val dbUpdateDate: String = formatter.format(date)

                        pref?.edit {
                            putString(key_db_update_date, dbUpdateDate)
                        }

                        goToNextPage()
                    }
                }

            }, { error ->
                Log.d(TAG, "got error-> ${error.message}")
                val mSnackBar = Snackbar
                    .make(splashActivityCL, getString(R.string.went_wrong), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.retry)) {
                        insertDataToInternalDB()
                    }
                mSnackBar?.show()
            })
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetBroadCastReceiver)
    }
}
