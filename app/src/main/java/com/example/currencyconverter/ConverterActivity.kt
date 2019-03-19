package com.example.currencyconverter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.currencyconverter.API.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ConverterActivity : AppCompatActivity() {

    private var disposable: Disposable? = null
    private val TAG ="ConverterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCurrencyList()
    }

    private fun getCurrencyList() {
        disposable = ApiService.create().getCurrencyList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->

                Log.d(TAG,"got result-> $result")

            },{error->
                Log.d(TAG,"got error-> ${error.message}")
            })
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

}
