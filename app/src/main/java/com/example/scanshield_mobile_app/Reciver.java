package com.example.scanshield_mobile_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class Reciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        if (intent.getAction().equalsIgnoreCase("com.example.Broacast")){

            String msg = bundle.getString("msg");
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
        }

        if (intent.getAction().equalsIgnoreCase("android.provider.Telephony.SMS_RECEIVED")){

            if (bundle != null){
                final Object[] pdusObj = (Object[])bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdusObj.length];
                for (int i = 0; i < messages.length; i++){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        String format = bundle.getString("format");
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
                    }
                    else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    }

//                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String senderNum = messages[i].getOriginatingAddress();
                    String message = messages[i].getMessageBody();
                    //Sms Recived
                    Toast.makeText(context,senderNum +":"+message ,Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
