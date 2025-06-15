package com.hsaqqa.cloudstoragesolutions;

import android.content.Context;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryHelper {
    public static void init(Context context) {
        Map config = new HashMap();
        config.put("cloud_name", "dqwnjh1j5");
        config.put("api_key", "464589737928676");
        config.put("api_secret", "nhGKBpnPcb4DqSMWNYl52vlJPwk"); // For secure uploads only
        MediaManager.init(context, config);
    }
}
