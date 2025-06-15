package com.hsaqqa.cloudstoragesolutions;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hsaqqa.cloudstoragesolutions.databinding.ActivitySupabaseBinding;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class BackblazeActivity extends AppCompatActivity {

    ActivitySupabaseBinding binding;
    String keyID = "00517525ed7b0fe0000000001";
    String keyName = "storageAppKey";
    String applicationKey = "K005AlYEzxLhzYeoOQSSgJn5A025qDQ";
    String endpoint = "https://s3.us-east-005.backblazeb2.com";


    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK && o.getData() != null) {
                Bitmap bitmap = (Bitmap) o.getData().getExtras().get("data");
                Log.d("pickImage", "onActivityResult: " + bitmap);
                binding.profileIv.setImageBitmap(bitmap);

                AmazonS3Client s3Client = B2Client.getInstance(getApplicationContext());
                String bucketName = "hashemSaqqaImages";
                String s3Key = "uploads/profileImage"; // Unique path

                new Thread(() -> {
                    try {
                        s3Client.putObject(new PutObjectRequest(bucketName, s3Key, bitmapToFile(bitmap, "hashemImage")));
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Upload successful!", Toast.LENGTH_SHORT).show());
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        Log.e("uploadError", "onActivityResult: "+e.getMessage() );
                    }
                }).start();

            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySupabaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences prefs = getSharedPreferences("B2Prefs", MODE_PRIVATE);
        prefs.edit()
                .putString("keyID", keyID)
                .putString("appKey", applicationKey)
                .putString("endpoint", endpoint)
                .apply();


        binding.pickImage.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            launcher.launch(intent);
        });


        String signedUrl = B2Client.getInstance(getApplicationContext()).generatePresignedUrl(
                "hashemSaqqaImages",
                "uploads/profileImage",
                new Date(System.currentTimeMillis() + 3600000) // 1-hour expiry
        ).toString();

        Picasso.get().load(signedUrl).into(binding.profileIv);


    }

    private File bitmapToFile(Bitmap bitmap, String name) throws IOException {
        File f = new File(getApplicationContext().getCacheDir(), name);
        f.createNewFile();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return f;
    }
}