package com.example.challengeup.backend;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Utilities {
    public static ArrayList<String> getCategories() {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/get_categories")
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String resStr = response.body().string();

            JSONObject object = new JSONObject(resStr);

            JSONArray array = (JSONArray) object.get("categories");
            ArrayList<String> arrayList = new ArrayList<>();

            for (int i = 0; i < array.length(); ++i) arrayList.add((String) array.get(i));
            return arrayList;
        } catch (JSONException | IOException ignored) {
        }
        return null;
    }

    public static boolean addNewCategory(String category) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("category", category);

            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/add_category")
                    .post(body)
                    .build();

            client.newCall(request).execute();
            return true;
        } catch (JSONException | IOException e) {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  static  void downloadFile(String download, String fileName) throws IOException {
        InputStream inputStream = new URL(download).openStream();
        Files.copy(inputStream, Paths.get(fileName));
    }
}

