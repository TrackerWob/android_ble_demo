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

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap<String, String>();
    public static String HEART_RATE_MEASUREMENT       = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String ISSC_SERVICE_UUID            = "0000fff0-0000-1000-8000-00805f9b34fb";
    public static String ISSC_CHAR_RX_UUID            = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String ISSC_CHAR_TX_UUID            = "0000fff2-0000-1000-8000-00805f9b34fb";
    
    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "GATT Profile");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT,                 "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put(ISSC_SERVICE_UUID,                      "ISSC Service UUID");
        attributes.put(ISSC_CHAR_RX_UUID,                      "ISSC Char RX UUID");
        attributes.put(ISSC_CHAR_TX_UUID,                      "ISSC Char TX UUID");
        //
        attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
        attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance");
        attributes.put("00002a04-0000-1000-8000-00805f9b34fb", "Peripheral Preferred Connection Parameters");
        attributes.put("00002a24-0000-1000-8000-00805f9b34fb", "Model Number String");
        attributes.put("00002a25-0000-1000-8000-00805f9b34fb", "Serial Number String");
        attributes.put("00002a27-0000-1000-8000-00805f9b34fb", "Hardware Revision String");
        attributes.put("00002a26-0000-1000-8000-00805f9b34fb", "Firmware Revision String");
        attributes.put("00002a28-0000-1000-8000-00805f9b34fb", "Software Revision String");
        attributes.put("00002a23-0000-1000-8000-00805f9b34fb", "System ID");
        attributes.put("00002a2a-0000-1000-8000-00805f9b34fb", "IEEE11073-20601 Regulatory Certification Data List");
        attributes.put("49535343-fe7d-4ae5-8fa9-9fafd205e455", "ISSC Proprietary Service");
        attributes.put("49535343-6daa-4d02-abf6-19569aca69fe", "ISSC Connection Parameter");
        attributes.put("49535343-aca3-481c-91ec-d85e28a60318", "ISSC Air Patch");
    }
    
    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
