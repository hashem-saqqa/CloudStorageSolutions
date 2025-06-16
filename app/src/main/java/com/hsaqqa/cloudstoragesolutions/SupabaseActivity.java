package com.hsaqqa.cloudstoragesolutions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hsaqqa.cloudstoragesolutions.databinding.ActivitySupabaseBinding;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseActivity extends AppCompatActivity {
    Retrofit retrofit;
    ApiRequests apiRequests;
    ActivitySupabaseBinding binding;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK && o.getData() != null) {
                Bitmap bitmap = (Bitmap) o.getData().getExtras().get("data");
                Log.d("pickImage", "onActivityResult: " + bitmap);
                binding.profileIv.setImageBitmap(bitmap);

                RequestBody requestFile = null;
                try {
                    requestFile = RequestBody.create(MediaType.parse("image/*"), bitmapToFile(bitmap, "hashemImage"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", "hashemImage", requestFile);


                Call<ResponseBody> call = apiRequests.uploadFile("images", "hashem.png", body);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d("UPLOAD", "Success!");
                        } else {
                            Log.e("UPLOAD", "Failed: " + response);
                            Log.e("UPLOAD", "Failed: " + response.code());
                            Log.e("UPLOAD", "Failed: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("UPLOAD", "Error: " + t.getMessage());
                    }
                });
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySupabaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.pickImage.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            launcher.launch(intent);
        });

        retrofit = new Retrofit.Builder().baseUrl("https://cpwfohyfdodhwmnsfmlk.supabase.co/").addConverterFactory(GsonConverterFactory.create()).build();

        apiRequests = retrofit.create(ApiRequests.class);

        Picasso.get().load("https://cpwfohyfdodhwmnsfmlk.supabase.co/storage/v1/object/images/profileImages").into(binding.profileIv);
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