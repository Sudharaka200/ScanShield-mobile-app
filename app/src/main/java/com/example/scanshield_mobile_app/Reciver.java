package com.example.scanshield_mobile_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Reciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");

            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu, bundle.getString("format"));
                    String sender = smsMessage.getDisplayOriginatingAddress();
                    String messageBody = smsMessage.getMessageBody();

                    // Get current user
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String email = (currentUser != null) ? currentUser.getEmail() : "Unknown User";

                    // 🔍 Preprocess message into float[] input
                    float[] inputVector = preprocessMessage(messageBody); // Dummy implementation

                    String status = "Unknown";
                    try {
                        Interpreter interpreter = new Interpreter(loadModelFile(context, "spam_model.tflite"));

                        // Allocate input and output buffers
                        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * 7490).order(ByteOrder.nativeOrder());
                        for (float val : inputVector) {
                            inputBuffer.putFloat(val);
                        }

                        float[][] output = new float[1][1]; // Assuming binary classification

                        // Run inference
                        interpreter.run(inputBuffer, output);

                        float prediction = output[0][0];
                        status = (prediction > 0.5f) ? "Spam" : "Not Spam";

                        interpreter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        status = "Error";
                    }

                    // Save to Firebase
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("messageData");

                    message_F message_f = new message_F();
                    message_f.setEmail(email);
                    message_f.setPhoneNumber(sender);
                    message_f.setMessage(messageBody);
                    message_f.setDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                    message_f.setStatus(status);

                    ref.push().setValue(message_f);
                }
            }
        }
    }

    private MappedByteBuffer loadModelFile(Context context, String modelFileName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(context.getAssets().openFd(modelFileName).getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = context.getAssets().openFd(modelFileName).getStartOffset();
        long declaredLength = context.getAssets().openFd(modelFileName).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Dummy method — replace this with real vectorization logic from your model training
    private float[] preprocessMessage(String message) {
        float[] vector = new float[7490];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = 0.0f;
        }
        return vector;
    }
}
