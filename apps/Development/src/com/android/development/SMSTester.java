/*
 * Copyright (C) 2012 The CyanogenMod Project
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

package com.android.development;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Telephony.Sms.Intents;
import android.telephony.MSimSmsManager;
import android.telephony.MSimTelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.internal.util.HexDump;

import static com.android.internal.telephony.MSimConstants.SUBSCRIPTION_KEY;

public class SMSTester extends Activity {

    private final String MOCK_PDU =
            "07914151551512f2040B916105551511f100006060605130308A04D4F29C0E";

    private EditText mSc;
    private EditText mSender;
    private EditText mSubscription;
    private LinearLayout mSubscriptionLayout;
    private EditText mMsg;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.sms_tester);

        mSc = (EditText)findViewById(R.id.sms_tester_mock_sms_sc);
        mSender = (EditText)findViewById(R.id.sms_tester_mock_sms_sender);
        mSubscription = (EditText)findViewById(R.id.sms_tester_mock_sms_subscription);
        mSubscriptionLayout = (LinearLayout)findViewById(R.id.sms_tester_mock_sms_subscription_layout);
        mMsg = (EditText)findViewById(R.id.sms_tester_mock_sms_msg);

        if (MSimTelephonyManager.getDefault().isMultiSimEnabled()) {
            mSubscription.setText(String.valueOf(MSimSmsManager.getDefault().getPreferredSmsSubscription()));
            mSubscriptionLayout.setVisibility(View.VISIBLE);
        }

        Button btnMockSms = (Button)findViewById(R.id.sms_tester_mock_sms_send_msg);
        btnMockSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sc = mSc.getText().toString();
                String sender = mSender.getText().toString();
                int subscription;
                try {
                    subscription = Integer.parseInt(mSubscription.getText().toString());
                } catch (NumberFormatException nfe) {
                    subscription = -1;
                }
                String msg = mMsg.getText().toString();

                Intent in = new Intent(Intents.MOCK_SMS_RECEIVED_ACTION);
                if (!TextUtils.isEmpty(sc)) {
                    in.putExtra("scAddr", sc);
                }
                if (!TextUtils.isEmpty(sender)) {
                    in.putExtra("senderAddr", sender);
                }
                if (!TextUtils.isEmpty(msg)) {
                    in.putExtra("msg", msg);
                }
                if (subscription >= 0) {
                    in.putExtra(SUBSCRIPTION_KEY, subscription);
                }
                sendOrderedBroadcast(in, null);
            }
        });
        Button btnMockSmsPdu = (Button)findViewById(R.id.sms_tester_mock_sms_send_pdu);
        btnMockSmsPdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[][] pdus = new byte[1][];
                pdus[0] = HexDump.hexStringToByteArray(MOCK_PDU);
                int subscription;
                try {
                    subscription = Integer.parseInt(mSubscription.getText().toString());
                } catch (NumberFormatException nfe) {
                    subscription = -1;
                }
                Intent in = new Intent(Intents.MOCK_SMS_RECEIVED_ACTION);
                in.putExtra("pdus", pdus);
                if (subscription >= 0) {
                    in.putExtra(SUBSCRIPTION_KEY, subscription);
                }
                sendOrderedBroadcast(in, null);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

