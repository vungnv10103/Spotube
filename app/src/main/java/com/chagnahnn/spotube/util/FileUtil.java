package com.chagnahnn.spotube.util;

import static com.chagnahnn.spotube.util.LogUtils.logE;

import android.content.Context;

import androidx.annotation.NonNull;

import com.chagnahnn.spotube.ui.model.MultiMedia;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileUtil {
    @NonNull
    public static List<MultiMedia> parseMultiMediaList(Context context, int resourceId) {
        List<MultiMedia> multimediaList = new ArrayList<>();
        try {
            String json = loadJSONFromAsset(context, resourceId);

            JSONObject jsonObject = new JSONObject(json).getJSONObject("multimedia");

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, MultiMedia>>() {
            }.getType();
            Map<String, MultiMedia> multimediaMap = gson.fromJson(jsonObject.toString(), type);

            for (Map.Entry<String, MultiMedia> entry : multimediaMap.entrySet()) {
                MultiMedia multiMedia = entry.getValue();
                multiMedia.setId(entry.getKey());
                multimediaList.add(multiMedia);
            }
        } catch (Exception e) {
            logE("", e);
        }
        return multimediaList;
    }

    @NonNull
    public static String loadJSONFromAsset(Context context, int resourceId) {
        StringBuilder json = new StringBuilder();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();
        } catch (Exception e) {
            logE("", e);
        }
        return json.toString();
    }

    @NonNull
    public static String loadDataFromRes(@NonNull Context context, int resId) {
        InputStream is = context.getResources().openRawResource(resId);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                logE("", e);
            }
        }
        return writer.toString();
    }
}
