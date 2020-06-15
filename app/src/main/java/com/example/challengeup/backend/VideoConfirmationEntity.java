package com.example.challengeup.backend;

import android.util.Log;

import org.json.JSONArray;
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

    private int numberOfConfirmationLeft;
    private int numberOfRejectionLeft;

    private ArrayList<String> usersWhoConfirmedOrDenied;


    public VideoConfirmationEntity(String user_id, String challenge_id, int numberOfConfirmationLeft, int numberOfRejectionLeft) {
        this.user_id = user_id;
        this.challenge_id = challenge_id;
        this.numberOfConfirmationLeft = numberOfConfirmationLeft;
        this.numberOfRejectionLeft = numberOfRejectionLeft;

        usersWhoConfirmedOrDenied = new ArrayList<>();
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
                    .put("numberOfConfirmationLeft", videoConfirmationEntity.numberOfConfirmationLeft)
                    .put("numberOfRejectionLeft", videoConfirmationEntity.numberOfRejectionLeft)
                    .put("usersWhoConfirmedOrDenied", videoConfirmationEntity.usersWhoConfirmedOrDenied);


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

    public static String addNewVideo(String user_id, String challenge_id, int numberOfConfirmationLeft, int numberOfRejectionLeft) throws IllegalArgumentException {
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
                    .put("numberOfConfirmationLeft", numberOfConfirmationLeft)
                    .put("numberOfRejectionLeft", numberOfRejectionLeft)
                    .put("usersWhoConfirmedOrDenied", new ArrayList<>());



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

                ArrayList<String> usersArray  = new ArrayList<>();

                try {
                    JSONArray users = new JSONArray(object.getJSONObject(key).getString("usersWhoConfirmedOrDenied"));
                    for (int i = 0; i < users.length(); ++i)
                        usersArray.add((String) users.get(i));
                } catch (JSONException ignored) {
                }

                VideoConfirmationEntity videoConfirmationEntity = new VideoConfirmationEntity(
                        object.getJSONObject(key).getString("user_id"),
                        object.getJSONObject(key).getString("challenge_id"),
                        object.getJSONObject(key).getInt("numberOfConfirmationLeft"),
                        object.getJSONObject(key).getInt("numberOfRejectionLeft"));
                videoConfirmationEntity.setId(key);
                videoConfirmationEntity.setUsersWhoConfirmedOrDenied(usersArray);
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

    public boolean deleteVideo(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("https://us-central1-challengeup-49057.cloudfunctions.net/delete_video_by_id?videoConfirmation_id=" + id)
                .get()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            id = null;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean deleteVideo(String id){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("https://us-central1-challengeup-49057.cloudfunctions.net/delete_video_by_id?videoConfirmation_id=" + id)
                .get()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static VideoConfirmationEntity getVideoById(String id, String challenge_id) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/get_videoConfirmation_by_id?videoConfirmation_id=" + id+"&videoConfirmation_challenge_id="+challenge_id)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String resStr = response.body().string();
            JSONObject object = new JSONObject(resStr);
            object = new JSONObject(object.getString("videoConfirmation"));

            ArrayList<String> usersArray  = new ArrayList<>();
            try {
                JSONArray users = new JSONArray(object.getJSONObject(id).getString("usersWhoConfirmedOrDenied"));
                for (int i = 0; i < users.length(); ++i)
                    usersArray.add((String) users.get(i));
            } catch (JSONException ignored) {
            }

                VideoConfirmationEntity videoConfirmationEntity = new VideoConfirmationEntity(
                        object.getJSONObject(id).getString("user_id"),
                        object.getJSONObject(id).getString("challenge_id"),
                        object.getJSONObject(id).getInt("numberOfConfirmationLeft"),
                        object.getJSONObject(id).getInt("numberOfRejectionLeft"));
                videoConfirmationEntity.setId(id);
                videoConfirmationEntity.setUsersWhoConfirmedOrDenied(usersArray);
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
                    .put("numberOfConfirmationLeft", numberOfConfirmationLeft)
                    .put("numberOfRejectionLeft", numberOfRejectionLeft)
                    .put("usersWhoConfirmedOrDenied", usersWhoConfirmedOrDenied);

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

    public int getNumberOfConfirmationLeft() {
        return numberOfConfirmationLeft;
    }

    public void setNumberOfConfirmationLeft(int numberOfConfirmationLeft) {
        this.numberOfConfirmationLeft = numberOfConfirmationLeft;
    }

    public int getNumberOfRejectionLeft() {
        return numberOfRejectionLeft;
    }

    public ArrayList<String> getUsersWhoConfirmedOrDenied() {
        return usersWhoConfirmedOrDenied;
    }

    public void setUsersWhoConfirmedOrDenied(ArrayList<String> usersWhoConfirmedOrDenied) {
        this.usersWhoConfirmedOrDenied = usersWhoConfirmedOrDenied;
    }

    public void setNumberOfRejectionLeft(int numberOfRejectionLeft) {
        this.numberOfRejectionLeft = numberOfRejectionLeft;
    }

    public boolean isConfirmed() {
        return numberOfConfirmationLeft<=0;
    }
    public boolean isRejected() {
        return numberOfRejectionLeft<=0;
    }

    @Override
    public String toString() {
        return "VideoConfirmationEntity{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", user_id='" + user_id + '\'' +
                ", challenge_id='" + challenge_id + '\'' +
                ", numberOfConfirmationLeft=" + numberOfConfirmationLeft +
                ", numberOfRejectionLeft=" + numberOfRejectionLeft +
                ", usersWhoConfirmedOrDenied=" + usersWhoConfirmedOrDenied +
                '}';
    }
}
