package com.yieldlab.yieldprobe_sample_application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yieldlab.yieldprobe.data.Bid;
import com.yieldlab.yieldprobe.data.Configuration;
import com.yieldlab.yieldprobe.data.DeviceMetaData;
import com.yieldlab.yieldprobe.events.EventProbeFailure;
import com.yieldlab.yieldprobe.events.EventProbeLog;
import com.yieldlab.yieldprobe.events.EventProbeSuccess;
import com.yieldlab.yieldprobe.Yieldprobe;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Activity for doing SDK calls from Java.
 */
public class ActivitySDKJava extends AppCompatActivity {

    private TextView txtSDKVersion;
    private TextView txtSampleApplicationVersion;
    private TextView txtLog;
    private RadioButton chkKotlin;
    private RadioButton chkJava;
    private Button btnInitSDK;
    private Button btnIsInitialized;
    private Button btnSetConfiguration;
    private Button btnGetConfiguration;
    private Button btnIsGooglePlayServicesAvailable;
    private Button btnGetDeviceMetaData;
    private Button btnProbeEvents;
    private Button btnProbeEventsRequestParallel;
    private Button btnProbeFuture;
    private Button btnShowTargeting;
    private Button btnShowAd;
    private Button btnCheckAndRequestPermission;

    private CheckBox chkGeolocation;
    private CheckBox chkPersonalizedAds;

    private CheckBox chkAdslot1;
    private CheckBox chkAdslot2;
    private CheckBox chkAdslot3;
    private EditText edtAdslot;

    private Activity mActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_java);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        setupUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventProbeLog event) {
        addToLog(event.getMessage().toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventProbeSuccess event) {
        addToLog(  "Number of Bids returned: " + event.getBids().size());

        // store the response
        Common.INSTANCE.setMHashMapAdslotBids(event.getBids());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventProbeFailure event) {
        addToLog(event.getMessage().toString());
    }

    private void setupUI() {

        txtLog = findViewById(R.id.txtLog);
        chkAdslot1 = findViewById(R.id.chkAdslot1);
        chkAdslot2 = findViewById(R.id.chkAdslot2);
        chkAdslot3 = findViewById(R.id.chkAdslot3);
        edtAdslot = findViewById(R.id.edtAdslot);

        // read versions
        txtSampleApplicationVersion = findViewById(R.id.txtSampleApplicationVersion);
        txtSampleApplicationVersion.setText(String.format("Sample Application Version: %s", BuildConfig.VERSION_NAME));
        txtSDKVersion = findViewById(R.id.txtSDKVersion);
        txtSDKVersion.setText("SDK Version: " + Yieldprobe.getVersionName());

        // setup checkboxes
        chkGeolocation = findViewById(R.id.chkGeolocation);
        chkPersonalizedAds = findViewById(R.id.chkPersonalizedAds);

        // radio buttons
        chkKotlin = findViewById(R.id.chkKotlin);
        chkKotlin.setChecked(false);
        chkKotlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivitySDKJava.this, ActivitySDKKotlin.class);
                startActivity(intent);
            }
        });

        chkJava = findViewById(R.id.chkJava);
        chkJava.setChecked(true);
        chkJava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivitySDKJava.this, "Java API calls already selected", Toast.LENGTH_SHORT).show();
            }
        });

        btnInitSDK = findViewById(R.id.btnInitSDK);
        btnInitSDK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Configuration configuration =
                            new Configuration(
                                    chkGeolocation.isChecked(),
                                    chkPersonalizedAds.isChecked(),
                                    10000, null, null, null,
                                    null);
                    Yieldprobe.initialize(getApplicationContext(), mActivity, configuration);
                } catch (Exception e) {
                    e.printStackTrace();
                    addToLog(e.getMessage());
                }
            }
        });

        btnIsInitialized = findViewById(R.id.btnIsInitialized);
        btnIsInitialized.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Boolean isInitialized = Yieldprobe.isInitialized();
                    Common.showAlertDialog(ActivitySDKJava.this, "Initialized", isInitialized.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    addToLog(e.toString());
                }
            }
        });

        btnSetConfiguration = findViewById(R.id.btnSetConfiguration);
        btnSetConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Configuration configuration =
                            new Configuration(
                                    chkGeolocation.isChecked(),
                                    chkPersonalizedAds.isChecked(),
                                    10000, null, null,
                                    null, null);
                    Yieldprobe.configure(getApplicationContext(), mActivity, configuration);
                } catch (Exception e) {
                    e.printStackTrace();
                    addToLog(e.getMessage());
                }
            }
        });

        btnGetConfiguration = findViewById(R.id.btnGetConfiguration);
        btnGetConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Configuration configuration = Yieldprobe.getConfiguration();
                    Common.showAlertDialog(ActivitySDKJava.this, "Configuration", configuration.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    addToLog(e.toString());
                }
            }
        });

        btnIsGooglePlayServicesAvailable = findViewById(R.id.btnIsGooglePlayServicesAvailable);
        btnIsGooglePlayServicesAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Boolean available = Yieldprobe.isGooglePlayServicesAvailable(ActivitySDKJava.this, false);
                    Common.showAlterDialogGooglePlayServices(ActivitySDKJava.this, ActivitySDKJava.this,"Available", available.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    addToLog(e.toString());
                }
            }
        });

        btnGetDeviceMetaData = findViewById(R.id.btnGetDeviceMetaData);
        btnGetDeviceMetaData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DeviceMetaData metaData = Yieldprobe.getDeviceMetaData();
                    Common.showAlertDialog(ActivitySDKJava.this, "Device Meta Data", metaData.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    addToLog(e.toString());
                }
            }
        });

        btnProbeEvents = findViewById(R.id.btnProbeEvents);
        btnProbeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Common.setMDoNotShowAdMenu(false);
                    Set<Integer> set = Common.buildAdslotSet(chkAdslot1, chkAdslot2, chkAdslot3, edtAdslot);
                    if (set.isEmpty()) {
                        // pass an empty set (!), will throw exception
                        Yieldprobe.probeWithEvents(set);
                    } else if (set.size() == 1) {
                        // call with a single integer
                        Yieldprobe.probeWithEvents(set.iterator().next());
                    } else {
                        // do the call with set
                        Yieldprobe.probeWithEvents(set);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    addToLog(e.toString());
                }
            }
        });

        btnProbeEventsRequestParallel = findViewById(R.id.btnProbeEventsRequestParallel);
        btnProbeEventsRequestParallel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Common.setMDoNotShowAdMenu(true);
                    for (int i = 0; i <= 2; i++) {
                        Set<Integer> set = new HashSet<Integer>();
                        if (i == 0) {
                            set.add(Common.ADLSOT1);
                        } else if (i == 1) {
                            set.add(Common.ADLSOT1);
                            set.add(Common.ADSLOT2);
                        } else {
                            set.add(Common.ADLSOT1);
                            set.add(Common.ADSLOT2);
                            set.add(Common.ADSLOT3);
                        }
                        Yieldprobe.probeWithEvents(set);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    addToLog(e.toString());
                }
            }
        });

        btnProbeFuture = findViewById(R.id.btnProbeFuture);
        btnProbeFuture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Common.setMDoNotShowAdMenu(false);
                    Set<Integer> set = Common.buildAdslotSet(chkAdslot1, chkAdslot2, chkAdslot3, edtAdslot);
                    if (set.isEmpty()) {
                        // pass an empty set (!), will throw exception
                        Yieldprobe.probe(set).thenApply(it -> {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addToLog(it.toString());
                                }
                            });
                            return null;
                        }).exceptionally(e -> {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addToLog(e.toString());
                                }
                            });
                            return null;
                        });
                    } else if (set.size() == 1) {
                        // do the single int call
                        Yieldprobe.probe(set.iterator().next()).thenApply(it -> {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addToLog(it.toString());
                                }
                            });
                            // convert single bid to a HashMap with one entry
                            HashMap<Integer, Bid> hashMap = new HashMap<>();
                            hashMap.put(Integer.parseInt(it.getId()), it);
                            Common.setMHashMapAdslotBids(hashMap);
                            return null;
                        }).exceptionally(e -> {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addToLog(e.toString());
                                }
                            });
                            return null;
                        });
                    } else {
                        // do the call with set
                        Yieldprobe.probe(set).thenApply(it -> {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addToLog(it.toString());
                                }
                            });
                            Common.setMHashMapAdslotBids(it);
                            return null;
                        }).exceptionally(e -> {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addToLog(e.toString());
                                }
                            });
                            return null;
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    addToLog(e.toString());
                }
            }
        });

        btnShowTargeting = findViewById(R.id.btnShowTargeting);
        btnShowTargeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<Integer, Bid> map = Common.getMHashMapAdslotBids();
                if(map != null) {
                    Common.showAlertDialog(ActivitySDKJava.this, "Targeting", map.toString());
                }
            }
        });

        btnShowAd = findViewById(R.id.btnShowAd);
        btnShowAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.showAdslotChooseDialog(ActivitySDKJava.this);
            }
        });

        btnCheckAndRequestPermission = findViewById(R.id.btnCheckAndRequestPermission);
        btnCheckAndRequestPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivitySDKJava.this, "Please use the button in the Kotlin activity.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addToLog(String msg) {
        String s = msg + "\n" + txtLog.getText().toString();
        txtLog.setText(s);
    }

}
