package com.example.onlinekonobar.Activity.User;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.onlinekonobar.R;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.android.Intents;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.InputStream;

public class ScanQR extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView qrScan;
    Button profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        qrScan=findViewById(R.id.qrScanBtn);
        profile=findViewById(R.id.scanQrProfileBtn);

        qrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Odaberite opciju")
                .setItems(new CharSequence[]{"Skeniraj QR kod", "Odaberi iz galerije"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                new IntentIntegrator(ScanQR.this).initiateScan();
                                break;
                            case 1:
                                openGallery();
                                break;
                        }
                    }
                });
        builder.create().show();
    }
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Odaberite QR kod sliku"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                String qrCodeValue = decodeQRCodeFromBitmap(bitmap);
                if (qrCodeValue != null) {
                    Log.d("SCANQR","Table id "+qrCodeValue);
                    saveToSharedPreferences(qrCodeValue);
                    Intent intent = new Intent(ScanQR.this, Articles.class);
//                    intent.putExtra("table", qrCodeValue);
                    ScanQR.this.startActivity(intent);
                } else {
                    Toast.makeText(this, "Nije moguće pročitati QR kod iz slike", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Greška prilikom učitavanja slike", Toast.LENGTH_SHORT).show();
            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    String qrValue = result.getContents();
                    saveToSharedPreferences(qrValue);
                    Toast.makeText(this, "QR Kod uspješno skeniran: " + qrValue, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Skeniranje otkazano", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private String decodeQRCodeFromBitmap(Bitmap bitmap) {
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            Result result = new MultiFormatReader().decode(binaryBitmap);
            return result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveToSharedPreferences(String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("QRPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("qrValue", value);
        editor.apply();
    }
}