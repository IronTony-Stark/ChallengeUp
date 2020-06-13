package com.example.challengeup.backend;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VideoConfirmationEntity {
    private String id;
    private String url;
    private String user_id;
    private String challenge_id;
    private int numberOfConfirmation;
    private boolean confirmed;

    public VideoConfirmationEntity(String user_id, String challenge_id) {
        this.user_id = user_id;
        this.challenge_id = challenge_id;
        numberOfConfirmation = 0;
        confirmed = false;
        id = null;
    }

    public static String addNewVideo(VideoConfirmationEntity videoConfirmationEntity) throws IllegalArgumentException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("user_id", videoConfirmationEntity.user_id)
                    .put("challenge_id", videoConfirmationEntity.challenge_id)
                    .put("numberOfConfirmation", videoConfirmationEntity.numberOfConfirmation)
                    .put("confirmed", videoConfirmationEntity.confirmed);


            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/add_videoConfirmation")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String resStr = response.body().string();
            JSONObject object = new JSONObject(resStr);

            videoConfirmationEntity.setId(object.getString("id"));
            return object.getString("id");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String addNewVideo(String user_id, String challenge_id) throws IllegalArgumentException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("user_id", user_id)
                    .put("challenge_id", challenge_id)
                    .put("numberOfConfirmation", 0)
                    .put("confirmed", false);


            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/add_videoConfirmation")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String resStr = response.body().string();
            JSONObject object = new JSONObject(resStr);

            return object.getString("id");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static ArrayList<VideoConfirmationEntity> getAllVideos() {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/get_all_videoConfirmations")
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String resStr = response.body().string();

            JSONObject object = new JSONObject(resStr);
            object = new JSONObject(object.getString("videoConfirmations"));

            ArrayList<VideoConfirmationEntity> videoConfirmationEntities = new ArrayList<>();
            for (Iterator<String> it = object.keys(); it.hasNext(); ) {
                String key = it.next();


                VideoConfirmationEntity videoConfirmationEntity = new VideoConfirmationEntity(
                        object.getJSONObject(key).getString("user_id"),
                        object.getJSONObject(key).getString("challenge_id"));
                videoConfirmationEntity.setConfirmed(object.getJSONObject(key).getBoolean("confirmed"));
                videoConfirmationEntity.setNumberOfConfirmation(object.getJSONObject(key).getInt("numberOfConfirmation"));
                videoConfirmationEntity.setId(key);
                if (!object.getJSONObject(key).getString("video_link").equals("")) {
                    videoConfirmationEntity.setUrl(object.getJSONObject(key).getString("video_link"));
                }
                videoConfirmationEntities.add(videoConfirmationEntity);
            }
            return videoConfirmationEntities;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static VideoConfirmationEntity getVideoById(String id) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/get_videoConfirmation_by_id?videoConfirmation_id=" + id)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String resStr = response.body().string();
            JSONObject object = new JSONObject(resStr);
            object = new JSONObject(object.getString("videoConfirmation"));


                VideoConfirmationEntity videoConfirmationEntity = new VideoConfirmationEntity(
                        object.getJSONObject(id).getString("user_id"),
                        object.getJSONObject(id).getString("challenge_id"));
                videoConfirmationEntity.setConfirmed(object.getJSONObject(id).getBoolean("confirmed"));
                videoConfirmationEntity.setNumberOfConfirmation(object.getJSONObject(id).getInt("numberOfConfirmation"));
                videoConfirmationEntity.setId(id);
                if (!object.getJSONObject(id).getString("video_link").equals("")) {
                    videoConfirmationEntity.setUrl(object.getJSONObject(id).getString("video_link"));
                }
            return videoConfirmationEntity;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void update() throws IllegalArgumentException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("videoConfirmation_id", id)
                    .put("user_id", user_id)
                    .put("challenge_id", challenge_id)
                    .put("confirmed", confirmed)
                    .put("numberOfConfirmation", numberOfConfirmation);

            // RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("data", jsonObject.toString())
                    .build();

            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/update_videoConfirmation")
                    .post(requestBody)
                    .build();
            Response r = client.newCall(request).execute();
        } catch (JSONException | IOException e) {
            Log.d("TITLE", e.getMessage());
            e.printStackTrace();
        }
    }


    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getChallenge_id() {
        return challenge_id;
    }

    public void setChallenge_id(String challenge_id) {
        this.challenge_id = challenge_id;
    }

    public int getNumberOfConfirmation() {
        return numberOfConfirmation;
    }

    public void setNumberOfConfirmation(int numberOfConfirmation) {
        this.numberOfConfirmation = numberOfConfirmation;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public String toString() {
        return "VideoConfirmationEntity{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", user_id='" + user_id + '\'' +
                ", challenge_id='" + challenge_id + '\'' +
                ", numberOfConfirmation=" + numberOfConfirmation +
                ", confirmed=" + confirmed +
                '}';
    }
}
