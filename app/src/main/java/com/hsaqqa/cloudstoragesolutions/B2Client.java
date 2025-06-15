package com.hsaqqa.cloudstoragesolutions;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

public class B2Client {
    private static AmazonS3Client s3Client;

    public static AmazonS3Client getInstance(Context context) {
        if (s3Client == null) {
            SharedPreferences prefs = context.getSharedPreferences("B2Prefs", MODE_PRIVATE);
            BasicAWSCredentials credentials = new BasicAWSCredentials(
                    prefs.getString("keyID", ""),
                    prefs.getString("appKey", "")
            );

            s3Client = new AmazonS3Client(credentials,new ClientConfiguration());
            s3Client.setEndpoint(prefs.getString("endpoint", ""));
        }
        return s3Client;
    }
}
