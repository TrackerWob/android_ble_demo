/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jesstech.bluetooth.bledemo;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final boolean D = false;
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private String mDeviceRSSI;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;
    public Handler mHandler;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    public TextView lbl_device_name;
    public CheckBox chk_ascii_mode;
    public CheckBox chk_show_service_list;
    public TextView lbl_device_rssi;
    public Button btn_send;
    public EditText edt_send;
    public Button btn_clear;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            if (mGattCharacteristics != null) {
/*                final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
                final int charaProp = characteristic.getProperties();
                
                Log.e("", charaProp + "");
                
                //if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                if (charaProp == 2) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
                    if (mNotifyCharacteristic != null) {
                        mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                        mNotifyCharacteristic = null;
                    }
                    mBluetoothLeService.readCharacteristic(characteristic);
                	Log.e("", "got READ characteristic ---- ");
                }
                
                //if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                if (charaProp == 16) {
                    mNotifyCharacteristic = characteristic;
                    mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);
                	Log.e("", "got NOTIFY characteristic ---- ");
                	
                	mBluetoothLeService.readCharacteristic(characteristic);
                }
                
                //if ((charaProp | BluetoothGattCharacteristic.PERMISSION_WRITE) > 0) {
                if (charaProp == 8) {
                	mWriteCharacteristic = characteristic;
                	Log.e("", "got WRITE characteristic ---- ");
                }
                */
                return true;
            }
            return false;
        }
    };

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        //mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);
        mDataField.setMovementMethod(ScrollingMovementMethod.getInstance());

        getActionBar().setTitle(getString(R.string.title));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        lbl_device_name = (TextView) findViewById(R.id.device_name);
        lbl_device_name.setText(mDeviceName);

        edt_send = (EditText) findViewById(R.id.edt_send);

        lbl_device_rssi = (TextView) findViewById(R.id.device_rssi);

        Public.b_ascii_mode = false;
        chk_ascii_mode = (CheckBox) findViewById(R.id.chk_ascii_mode);
        chk_ascii_mode.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Public.b_ascii_mode = isChecked;
            }
        });

        chk_show_service_list = (CheckBox) findViewById(R.id.chk_show_service_list);
        chk_show_service_list.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mGattServicesList.setVisibility(View.VISIBLE);
                } else {
                    mGattServicesList.setVisibility(View.INVISIBLE);
                }
            }
        });

        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConnected) {
                    if (mWriteCharacteristic != null) {
                        String str = edt_send.getText().toString();
                        if (str.length() == 0) {
                            Public.ShowAlert("Warning", "Please enter data!", DeviceControlActivity.this);
                            return;
                        }

                        if (Public.b_ascii_mode) {
                            byte[] buf = new byte[str.length()];
                            buf = str.getBytes();
                            mBluetoothLeService.writeCharacteristic(mWriteCharacteristic, buf);
                        } else {
                            int idx = 0;
                            int count = str.length() / 3;
                            if (str.length() % 3 != 0) {
                                count++;
                            }
                            byte[] buf = new byte[count];

                            for (int i = 0; i < str.length(); i += 3) {
                                int end = i + 2;
                                if (end > str.length()) {
                                    end = str.length();
                                }
                                String s = str.substring(i, end);
                                if (!Public.is_hex_char(s)) {
                                    Public.ShowAlert("Error", "Wrong data format!\n\nCorrect format:\n30 39 9D AA FF\n30,39,9D,AA,FF", DeviceControlActivity.this);
                                    return;
                                }
                                if (idx >= count) {
                                    break;
                                }
                                buf[idx++] = (byte) Integer.parseInt(s, 16);
                            }
                            mBluetoothLeService.writeCharacteristic(mWriteCharacteristic, buf);
                        }
                    }
                } else {
                    //Log.e("", "Not Connected!");
                    Public.ShowAlert("Warning", "Not connected!", DeviceControlActivity.this);
                }
            }
        });

        btn_clear = (Button) findViewById(R.id.btn_clear_received);
        btn_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataField.setText("");
                mDataField.post(new Runnable() {
                    @Override
                    public void run() {
                        mDataField.scrollTo(0, 0);
                    }
                });
            }
        });

        mHandler = new Handler();
        mHandler.postDelayed(runnable, 200);
    }

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //读取RSSI
            if (mBluetoothLeService != null) {
                mBluetoothLeService.update_rssi();
                lbl_device_rssi.setText(mBluetoothLeService.get_rssi() + "");
            }
            mHandler.postDelayed(this, 200);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_about:
                String str = String.format("Version: %s\nJess Technology Co., Ltd.\nWebsite: www.jesstech.com\n", Public.getAppVersionName(this));
                Public.ShowInfo("About JessTech BLE Tool", str, DeviceControlActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 更新连接状态
     *
     * @param resourceId
     */
    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.append(data);

            //心跳的就加个空格，普通的加一个也无所谓
            mDataField.append(" ");

            if (mDataField.getLineCount() > 7) {
                mDataField.scrollBy(0, mDataField.getLineHeight());
            }
        }
        Log.d("-------", data);
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);

                //++++++++++++++++++
                final int charaProp = gattCharacteristic.getProperties();

                Log.e("", charaProp + "===UUID:" + gattCharacteristic.getUuid().toString());
                
/*                
                //if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                if (charaProp == 2) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
                    if (mNotifyCharacteristic != null) {
                        mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                        mNotifyCharacteristic = null;
                    }
                    mBluetoothLeService.readCharacteristic(gattCharacteristic);
                	Log.e("", "got READ characteristic ---- ");
                }
*/
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {

                    //判断一下UUID
                    if (gattCharacteristic.getUuid().toString().compareToIgnoreCase(SampleGattAttributes.ISSC_CHAR_RX_UUID) == 0) {
                        if (D) Log.e("", "got NOTIFY characteristic ---- ");
                        mNotifyCharacteristic = gattCharacteristic;
                        mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);

                        //延时500ms，再发送一次，防止AMIC收不到
//                		try {
//							Thread.sleep(500);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//                		mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);
                    }
                }

                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0 ||
                        (charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                    //判断一下UUID
                    if (gattCharacteristic.getUuid().toString().compareToIgnoreCase(SampleGattAttributes.ISSC_CHAR_TX_UUID) == 0) {
                        mWriteCharacteristic = gattCharacteristic;
                        if (D) Log.e("", "got WRITE characteristic ---- ");
                    }
                }

                //+++++++++++++++++
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{LIST_NAME, LIST_UUID},
                new int[]{android.R.id.text1, android.R.id.text2},
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{LIST_NAME, LIST_UUID},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
        Toast.makeText(mBluetoothLeService, "...", Toast.LENGTH_SHORT).show();
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}
