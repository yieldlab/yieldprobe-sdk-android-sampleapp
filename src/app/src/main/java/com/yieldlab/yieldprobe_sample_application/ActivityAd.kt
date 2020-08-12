package com.yieldlab.yieldprobe_sample_application

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.adition.android.sdk.AdViewListener
import com.adition.android.sdk.AditionErrorCode
import com.adition.android.sdk.AditionView
import com.adition.android.sdk.util.Log
import com.yieldlab.yieldprobe.data.Bid
import kotlinx.android.synthetic.main.activity_ad.*
import kotlinx.android.synthetic.main.content_activity_ad.*

/**
 * Activity to load an Adition AdView with targeting information.
 */
class ActivityAd : AppCompatActivity(), AdViewListener {

    val CONTENT_UNIT_ID = "4493233"
    val NETWORK_ID = "99"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setIcon(R.mipmap.ic_launcher)

        // setting debug log level
        Log.setLogLevel(Log.LEVEL_DEBUG)

        // setup UI
        setupUI()
        setupAditionAdView()
    }

    private fun setupUI() {

        // setup back button
        btnBack.setOnClickListener() {
            // just close ad activity
            finish()
        }
    }

    /**
     * Sets up the Adition AdView per code.
     */
    private fun setupAditionAdView() {

        // create AdView
        var aditionView = AditionView(this, CONTENT_UNIT_ID, NETWORK_ID, 100, 100, true)
        aditionView.setBackgroundColor(Color.WHITE)

        // add a listener
        aditionView.adViewListener = this

        // get the targeting bid
        var bid: Bid? = Common.mHashMapAdslotBids?.get(Common.mSelectedAdslotId)
        var adviewSetData = "addProfileTargetingKey() with:\n"

        // iterate over targeting information and add all keys
        for ((k, v) in bid!!.customTargeting) {
            aditionView.addProfileTargetingKey(k, "$v")
            adviewSetData += "$k $v\n"
        }

        // set further information
        txtAdViewSetData.text = adviewSetData
        txtTargeting.text = bid.toString()

        // insert in layout
        var params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        aditionView.setPadding(20, 20, 20, 20)
        aditionView.setBackgroundColor(Color.GRAY)
        // aditionView.layoutParams = params
        linearLayoutAd.addView(aditionView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // call execute to load content
        aditionView.execute()
    }

    override fun onAdClicked() {
        //
    }

    override fun onAdLoaded() {
        //
    }

    override fun beforeAdSDKEvent(var1: String) {
        Log.d("YLQA", "beforeAdSDKEvent(): $var1")
    }

    override fun onAdSDKEvent(var1: String, var2: String) {
        Log.d("YLQA", "onAdSDKEvent(): $var1 $var2")
    }

    override fun onAdSDKError(var1: AditionErrorCode, var2: String) {
        Log.d("YLQA", "onAdSDKError(): ${var1.getName()} $var2")
    }

    override fun onAdFiredEvent(var1: String, var2: String) {
        Log.d("YLQA", "onAdFiredEvent(): $var1 $var2")
    }
}
