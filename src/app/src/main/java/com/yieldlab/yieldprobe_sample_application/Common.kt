package com.yieldlab.yieldprobe_sample_application

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.yieldlab.yieldprobe.Yieldprobe
import com.yieldlab.yieldprobe.data.Bid

/**
 * Class to hold Common data.
 */
object Common {

    // adslots const
    const val ADLSOT1 = 5220339
    const val ADSLOT2 = 5220336
    const val ADSLOT3 = 6846238

    // flag set for multiple request tested
    @JvmStatic
    var mDoNotShowAdMenu = false

    // stored request adslot ids and response
    @JvmStatic
    var mAdslots: Set<Int>? = null
    @JvmStatic
    var mHashMapAdslotBids: HashMap<Int, Bid>? = null

    // the selected adslot id for the adition activity
    @JvmStatic
    var mSelectedAdslotId: Int? = null

    @JvmStatic
    val PERMISSION_REQUEST_CODE = 0

    @JvmStatic
    fun buildAdslotSet(
        chkAdslot1: CheckBox,
        chkAdslot2: CheckBox,
        chkAdslot3: CheckBox,
        edtAdslot: EditText
    ): Set<Int> {
        var adslotSet: MutableSet<Int> = mutableSetOf()

        /*
        Test adslots:
        5220339 (video)
        5220336 (728x90 banner)
        6846238 (300x250 banner)
        */

        // read check boxes
        if (chkAdslot1.isChecked) {
            adslotSet.add(ADLSOT1)
        }
        if (chkAdslot2.isChecked) {
            adslotSet.add(ADSLOT2)
        }
        if (chkAdslot3.isChecked) {
            adslotSet.add(ADSLOT3)
        }

        // read edit text field and add adslots
        var sAdslotEdt = edtAdslot.text.split(",")
        for (element in sAdslotEdt) {
            if (element != "") {
                adslotSet.add(Integer.parseInt(element))
            }
        }

        // store the set
        Common.mAdslots = adslotSet

        return adslotSet
    }

    @JvmStatic
    fun showAlertDialog(context: Context, title: String, msg: String) {

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setIcon(android.R.drawable.ic_menu_info_details)

        builder.setPositiveButton("Okay") { dialogInterface, which ->
            // do nothing
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    @JvmStatic
    fun showAlterDialogGooglePlayServices(context: Context, activity: Activity, title: String, msg: String) {

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setIcon(android.R.drawable.ic_menu_info_details)

        builder.setPositiveButton("Okay") { dialogInterface, which ->
            // do nothing
        }

        builder.setNegativeButton("Enable") { dialogInterface, which ->
            // show popup to enable
            Yieldprobe.isGooglePlayServicesAvailable(activity, true)
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    @JvmStatic
    fun showAdslotChooseDialog(context: Context) {

        if (mDoNotShowAdMenu) {
            // will only happen when testing multiple adslots in parallel button
            showAlertDialog(context, "Error", "Can not display anything. Will only happen when testing multiple adslots in parallel button.")
        } else {
            if (mHashMapAdslotBids != null) {
                // convert adslots into string array
                var listOfAdslotsInResponse = Array<String>(mHashMapAdslotBids!!.size) { "" }
                for (x in 0 until mHashMapAdslotBids!!.size) {
                    listOfAdslotsInResponse.set(
                        x,
                        mHashMapAdslotBids!!.entries.elementAt(x).key.toString()
                    )
                }

                // create a new builder
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Select Adslotid")
                builder.setItems(
                    listOfAdslotsInResponse
                ) { dialog, which ->

                    mSelectedAdslotId = mAdslots!!.elementAt(which)

                    val intent = Intent(context, ActivityAd::class.java)
                    context.startActivity(intent)
                }
                builder.show()
            } else {
                Toast.makeText(context, "No Adslots available.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
