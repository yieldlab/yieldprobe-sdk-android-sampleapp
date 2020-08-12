package com.yieldlab.yieldprobe_sample_application

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.yieldlab.yieldprobe.Yieldprobe
import com.yieldlab.yieldprobe.data.Bid
import com.yieldlab.yieldprobe.data.Configuration
import com.yieldlab.yieldprobe.data.DeviceMetaData
import com.yieldlab.yieldprobe.events.EventProbeFailure
import com.yieldlab.yieldprobe.events.EventProbeLog
import com.yieldlab.yieldprobe.events.EventProbeSuccess
import java.lang.Exception
import kotlinx.android.synthetic.main.activity_sdk_kotlin.*
import kotlinx.android.synthetic.main.content_shared.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Activity for doing SDK calls from Kotlin.
 */
class ActivitySDKKotlin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sdk_kotlin)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setIcon(R.mipmap.ic_launcher)

        setupUI()
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EventProbeLog) {
        addToLog(event.getMessage().toString())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EventProbeSuccess) {
        addToLog(event.getBids().toString())
        // store the response
        Common.mHashMapAdslotBids = event.getBids()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EventProbeFailure) {
        addToLog(event.getMessage().toString())
    }

    private fun addToLog(msg: String) {
        var sNewLog = msg + "\n" + txtLog.text
        txtLog.text = sNewLog
    }

    private fun setupUI() {

        // read versions
        txtSampleApplicationVersion.text = "Sample Application Version: " + BuildConfig.VERSION_NAME
        txtSDKVersion.text = "SDK Version: " + Yieldprobe.getVersionName()

        chkKotlin.isChecked = true
        chkKotlin.setOnClickListener {
            // show toast
            Toast.makeText(this, "Kotlin API calls already selected", Toast.LENGTH_SHORT).show()
        }

        chkJava.isChecked = false
        chkJava.setOnClickListener {
            // send intent
            val intent = Intent(this@ActivitySDKKotlin, ActivitySDKJava::class.java)
            startActivity(intent)
        }

        btnInitSDK.setOnClickListener {
            try {
                val configurationDefault: Configuration =
                    Configuration(
                        chkGeolocation.isChecked,
                        chkPersonalizedAds.isChecked)

                // configuration with all parameters set
                val hashMapParameterT: HashMap<String, String?> = HashMap()
                hashMapParameterT.put("1", "test")
                hashMapParameterT.put("2", "test")
                hashMapParameterT.put("3", "test")

                var configurationAllParamters: Configuration =
                    Configuration(
                        chkGeolocation.isChecked,
                        chkPersonalizedAds.isChecked,
                        10000,
                        "mAppName",
                        "mBundleName",
                        "mStoreURL",
                        hashMapParameterT
                    )
                Yieldprobe.initialize(applicationContext, this, configurationDefault)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        btnIsInitialized.setOnClickListener {
            try {
                val isInitialized: Boolean = Yieldprobe.isInitialized()
                Common.showAlertDialog(this@ActivitySDKKotlin, "Initialized", isInitialized.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                addToLog(e.toString())
            }
        }

        btnSetConfiguration.setOnClickListener {
            try {
                val configuration: Configuration =
                    Configuration(
                        chkGeolocation.isChecked,
                        chkPersonalizedAds.isChecked
                    )
                Yieldprobe.configure(applicationContext, this, configuration)
            } catch (e: Exception) {
                e.printStackTrace()
                addToLog(e.toString())
            }
        }

        btnGetConfiguration.setOnClickListener {
            try {
                val configuration: Configuration = Yieldprobe.getConfiguration()
                Common.showAlertDialog(this@ActivitySDKKotlin, "Configuration", configuration.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                addToLog(e.toString())
            }
        }

        btnIsGooglePlayServicesAvailable.setOnClickListener {
            try {
                val available = Yieldprobe.isGooglePlayServicesAvailable(this, false)
                Common.showAlterDialogGooglePlayServices(this@ActivitySDKKotlin, this, "Available", available.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                addToLog(e.toString())
            }
        }

        btnGetDeviceMetaData.setOnClickListener {
            try {
                val metaData: DeviceMetaData = Yieldprobe.getDeviceMetaData()
                Common.showAlertDialog(this@ActivitySDKKotlin, "Device Meta Data", metaData.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                addToLog(e.toString())
            }
        }

        btnProbeEvents.setOnClickListener {
            try {
                Common.mDoNotShowAdMenu = false
                val set: Set<Int> = Common.buildAdslotSet(chkAdslot1, chkAdslot2, chkAdslot3, edtAdslot)
                if (set.isEmpty()) {
                    // pass an empty set (!), will throw exception
                    Yieldprobe.probeWithEvents(set)
                } else if (set.size == 1) {
                    // call with a single integer
                    Yieldprobe.probeWithEvents(set.first())
                } else {
                    // do the call with set
                    Yieldprobe.probeWithEvents(set)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                addToLog(e.toString())
            }
        }

        btnProbeEventsRequestParallel.setOnClickListener {
            try {
                Common.mDoNotShowAdMenu = true
                for (i in 0..2) {
                    var set: Set<Int> = emptySet()
                    if (i == 0) {
                        set = setOf(Common.ADLSOT1)
                    } else if (i == 1) {
                        set = setOf(Common.ADLSOT1, Common.ADSLOT2)
                    } else {
                        set = setOf(Common.ADLSOT1, Common.ADSLOT2, Common.ADSLOT3)
                    }
                    Yieldprobe.probeWithEvents(set)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                addToLog(e.toString())
            }
        }

        btnProbeFuture.setOnClickListener {

            try {
                Common.mDoNotShowAdMenu = false
                val set: Set<Int> = Common.buildAdslotSet(chkAdslot1, chkAdslot2, chkAdslot3, edtAdslot)
                if (set.isEmpty()) {
                    // pass an empty set (!), will throw exception (but not from CompleteableFuture)
                    Yieldprobe.probe(set).thenApply {
                        this@ActivitySDKKotlin.runOnUiThread(java.lang.Runnable {
                            addToLog(it.toString())
                        })
                    }.exceptionally { e ->
                        e.printStackTrace()
                        this@ActivitySDKKotlin.runOnUiThread(java.lang.Runnable {
                            addToLog(e.toString())
                        })
                    }
                } else if (set.size == 1) {
                    // do the single int call
                    Yieldprobe.probe(set.first()).thenApply {
                        this@ActivitySDKKotlin.runOnUiThread(java.lang.Runnable {
                            addToLog(it.toString())
                        })
                        // convert single bid to a HashMap with one entry
                        var hashMap: HashMap<Int, Bid> = HashMap()
                        hashMap.put(Integer.parseInt(it.id.toString()), it)
                        Common.mHashMapAdslotBids = hashMap
                    }.exceptionally { e ->
                        e.printStackTrace()
                        this@ActivitySDKKotlin.runOnUiThread(java.lang.Runnable {
                            addToLog(e.toString())
                        })
                    }
                } else {
                    // do the call with set
                    Yieldprobe.probe(set).thenApply {
                        this@ActivitySDKKotlin.runOnUiThread(java.lang.Runnable {
                            addToLog(it.toString())
                        })
                        Common.mHashMapAdslotBids = it
                    }.exceptionally { e ->
                        e.printStackTrace()
                        this@ActivitySDKKotlin.runOnUiThread(java.lang.Runnable {
                            addToLog(e.toString())
                        })
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                addToLog(e.toString())
            }
        }

        btnShowTargeting.setOnClickListener {
            Common.showAlertDialog(this@ActivitySDKKotlin, "Targeting", Common.mHashMapAdslotBids.toString())
        }

        btnShowAd.setOnClickListener {
            Common.showAdslotChooseDialog(this@ActivitySDKKotlin)
        }

        btnCheckAndRequestPermission.setOnClickListener {
            checkPermissionsAndRequest()
        }
    }

    private fun checkPermissionsAndRequest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), Common.PERMISSION_REQUEST_CODE
            )
        } else {
            Toast.makeText(this, "Location permissions already granted.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (Common.PERMISSION_REQUEST_CODE) {
            0 -> {
                if (grantResults.size > 0) {

                    val ACCESS_FINE_LOCATION = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val ACCESS_COARSE_LOCATION = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (ACCESS_FINE_LOCATION && ACCESS_COARSE_LOCATION) {
                        Toast.makeText(this, "Location permission granted.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
