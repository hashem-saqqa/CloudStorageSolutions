package com.hsaqqa.cloudstoragesolutions;

import android.content.Intent;
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

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.hsaqqa.cloudstoragesolutions.databinding.ActivitySupabaseBinding;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class CloudinaryActivity extends AppCompatActivity {
    ActivitySupabaseBinding binding;
    String imageUrl;
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK && o.getData() != null) {
                Bitmap bitmap = (Bitmap) o.getData().getExtras().get("data");
                Log.d("pickImage", "onActivityResult: " + bitmap);
                binding.profileIv.setImageBitmap(bitmap);

                String requestId = MediaManager.get().upload(bitmapToByteArray(bitmap))
                        .option("folder", "uploads")
                        .option("public_id", "profile_" + System.currentTimeMillis())
                        .option("notification_url", "https://mysite.example.com/notify_endpoint")
                        .callback(new UploadCallback() {

                            @Override
                            public void onStart(String requestId) {

                            }

                            @Override
                            public void onProgress(String requestId, long bytes, long totalBytes) {

                            }

                            @Override
                            public void onSuccess(String requestId, Map resultData) {
                                imageUrl = (String) resultData.get("secure_url");

                                // Save this URL to your database
                                runOnUiThread(() -> {
                                    Toast.makeText(CloudinaryActivity.this, "Upload success!", Toast.LENGTH_SHORT).show();
                                    Picasso.get().load(imageUrl).into(binding.profileIv);
                                });
                            }

                            @Override
                            public void onError(String requestId, ErrorInfo error) {
                                Log.e("CloudinaryUpload", "onError: " + error.getCode() + " --> " + error.getDescription());
                            }

                            @Override
                            public void onReschedule(String requestId, ErrorInfo error) {

                            }
                        })
                        .dispatch();

            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySupabaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CloudinaryHelper.init(this);


        binding.pickImage.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            launcher.launch(intent);
        });



    }
    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream); // 80% quality
        return stream.toByteArray();
    }
}