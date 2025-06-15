package com.hsaqqa.cloudstoragesolutions;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiRequests {

    @Multipart
    @POST("storage/v1/object/{bucket}/{path}")
    @Headers({"Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNwd2ZvaHlmZG9kaHdtbnNmbWxrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDk4MDE4NjMsImV4cCI6MjA2NTM3Nzg2M30.EE9qs0FmEVCwQNQklRSGaoa9m3VU6vsxDAPfI4j8pRk"})
    Call<ResponseBody> uploadFile(
            @Path("bucket") String bucket,
            @Path("path") String path,
            @Part MultipartBody.Part file
//            ,@Header("Authorization") String auth
    );

    @GET("storage/v1/object/{bucket}/{path}")
    @Headers({"Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNwd2ZvaHlmZG9kaHdtbnNmbWxrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDk4MDE4NjMsImV4cCI6MjA2NTM3Nzg2M30.EE9qs0FmEVCwQNQklRSGaoa9m3VU6vsxDAPfI4j8pRk"})
    Call<ResponseBody> downloadFile(
            @Path("bucket") String bucket,
            @Path("path") String filePath
    );
}
