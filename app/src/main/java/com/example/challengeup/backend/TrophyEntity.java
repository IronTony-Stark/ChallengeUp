package com.example.challengeup.backend;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TrophyEntity {
    private String id;
    private String name;
    private String description;
    private String photo;

    public TrophyEntity(String name, String description) {

        this.name = name;
        this.description = description;
        id = null;
        photo = null;
    }

    public static String addNewTrophy(TrophyEntity trophy) throws IllegalArgumentException {
        Validation.validateName(trophy.name);
        Validation.validateDescription(trophy.description);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("name", trophy.name)
                    .put("description", trophy.description);

            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/add_trophy")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String resStr = response.body().string();
            JSONObject object = new JSONObject(resStr);

            trophy.setId(object.getString("id"));
            return object.getString("id");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String addNewTrophy(String name, String description) throws IllegalArgumentException {
        Validation.validateName(name);
        Validation.validateDescription(description);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("name", name)
                    .put("description", description);

            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/add_trophy")
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

    public static ArrayList<TrophyEntity> getAllTrophies() {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/get_all_trophies")
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String resStr = response.body().string();

            JSONObject object = new JSONObject(resStr);
            object = new JSONObject(object.getString("trophies"));

            ArrayList<TrophyEntity> trophies = new ArrayList<>();
            for (Iterator<String> it = object.keys(); it.hasNext(); ) {
                String key = it.next();

                TrophyEntity trophy = new TrophyEntity(object.getJSONObject(key).getString("name"),
                        object.getJSONObject(key).getString("description"));
                trophy.setId(key);
                if (!object.getJSONObject(key).getString("photo_link").equals("")) {
                    trophy.setPhoto(object.getJSONObject(key).getString("photo_link"));
                }
                trophies.add(trophy);
            }
            return trophies;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TrophyEntity getTrophyById(String id) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/get_trophy_by_id?trophy_id=" + id)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String resStr = response.body().string();
            JSONObject object = new JSONObject(resStr);
            object = new JSONObject(object.getString("trophy"));

            TrophyEntity trophy = new TrophyEntity(object.getJSONObject(id).getString("name"),
                    object.getJSONObject(id).getString("description"));
            trophy.setId(id);
            if (!object.getJSONObject(id).getString("photo_link").equals("")) {
                trophy.setPhoto(object.getJSONObject(id).getString("photo_link"));
            }
            return trophy;
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    public void update() throws IllegalArgumentException {
        Validation.validateName(name);
        Validation.validateDescription(description);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("trophy_id", id)
                    .put("name", name)
                    .put("description", description);

            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/update_trophy")
                    .post(body)
                    .build();
            client.newCall(request).execute();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<UserEntity> getUsersWithThisTrophy() {
        ArrayList<UserEntity> users = UserEntity.getAllUsers();
        ArrayList<UserEntity> a = (ArrayList<UserEntity>) users.stream().filter(x -> x.getTrophies().contains(id)).collect(Collectors.toList());
        return a;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrophyEntity trophy = (TrophyEntity) o;
        return Objects.equals(id, trophy.id);
    }

    @Override
    public String toString() {
        return "TrophyEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
