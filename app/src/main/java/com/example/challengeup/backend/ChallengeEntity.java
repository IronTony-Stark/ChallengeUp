package com.example.challengeup.backend;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChallengeEntity {
    private String id;
    private String name;
    private String task;
    private String creator_id;

    private int likes;
    private int timesViewed;
    private int rewardRp;

    private ArrayList<String> rewardTrophies;
    private ArrayList<String> tags;
    private ArrayList<String> categories;

    public ChallengeEntity(String name, String task, String creator_id, ArrayList<String> tags, ArrayList<String> categories) {
        this(name, task, creator_id);

        this.tags = tags;
        this.categories = categories;
    }

    public ChallengeEntity(String name, String task, String creator_id) {
        this.name = name;
        this.task = task;
        this.creator_id = creator_id;
        tags = new ArrayList<>();
        categories = new ArrayList<>();
        likes = 0;
        timesViewed = 0;
        rewardRp = 0;
        rewardTrophies = new ArrayList<>();
        id = null;
    }

    public static String addNewChallenge(ChallengeEntity challenge) throws IllegalArgumentException {
        Validation.validateTags(challenge.tags);
        Validation.validateTags(challenge.categories);
        Validation.validateName(challenge.name);
        Validation.validateTask(challenge.task);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("name", challenge.name)
                    .put("task", challenge.task)
                    .put("creator_id", challenge.creator_id)
                    .put("likes", challenge.likes)
                    .put("times_viewed", challenge.timesViewed)
                    .put("tags", challenge.tags)
                    .put("categories", challenge.categories)
                    .put("rewardRp", challenge.rewardRp)
                    .put("rewardTrophies", challenge.rewardTrophies);


            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/add_challenge")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String resStr = response.body().string();
            JSONObject object = new JSONObject(resStr);

            challenge.setId(object.getString("id"));
            return object.getString("id");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String addNewChallenge(String name, String task, String creator_id, ArrayList<String> tags, ArrayList<String> categories) throws IllegalArgumentException {
        Validation.validateTags(tags);
        Validation.validateTags(categories);
        Validation.validateName(name);
        Validation.validateTask(task);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("name", name)
                    .put("task", task)
                    .put("creator_id", creator_id)
                    .put("likes", 0)
                    .put("times_viewed", 0)
                    .put("tags", tags)
                    .put("categories", categories)
                    .put("rewardRp", 0)
                    .put("rewardTrophies", new ArrayList());


            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/add_challenge")
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

    public static ArrayList<ChallengeEntity> getAllChallenges() {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/get_all_challenges")
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String resStr = response.body().string();

            JSONObject object = new JSONObject(resStr);
            object = new JSONObject(object.getString("challenges"));

            ArrayList<ChallengeEntity> challenges = new ArrayList<>();
            for (Iterator<String> it = object.keys(); it.hasNext(); ) {
                String key = it.next();

                ArrayList<String> tagsArray = new ArrayList<>();
                ArrayList<String> categoriesArray = new ArrayList<>();

                ArrayList<String> trophiesArray = new ArrayList<>();

                try {
                    JSONArray rewardTrophies = new JSONArray(object.getJSONObject(key).getString("rewardTrophies"));
                    for (int i = 0; i < rewardTrophies.length(); ++i)
                        trophiesArray.add((String) rewardTrophies.get(i));
                } catch (JSONException ignored) {
                }

                try {
                    JSONArray tags = new JSONArray(object.getJSONObject(key).getString("tags"));
                    for (int i = 0; i < tags.length(); ++i) tagsArray.add((String) tags.get(i));
                } catch (JSONException ignored) {
                }


                try {
                    JSONArray categories = new JSONArray(object.getJSONObject(key).getString("categories"));
                    for (int i = 0; i < categories.length(); ++i)
                        categoriesArray.add((String) categories.get(i));
                } catch (JSONException ignored) {
                }


                ChallengeEntity challenge = new ChallengeEntity(object.getJSONObject(key).getString("name"),
                        object.getJSONObject(key).getString("task"),
                        object.getJSONObject(key).getString("creator_id"),
                        tagsArray,
                        categoriesArray);
                challenge.setId(key);
                challenge.setLikes(Integer.parseInt(object.getJSONObject(key).getString("likes")));
                challenge.setTimesViewed(Integer.parseInt(object.getJSONObject(key).getString("times_viewed")));
                challenge.setRewardRp(Integer.parseInt(object.getJSONObject(key).getString("rewardRp")));
                challenge.setRewardTrophies(trophiesArray);
                challenges.add(challenge);
            }
            return challenges;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ChallengeEntity getChallengeById(String id) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/get_challenge_by_id?challenge_id=" + id)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String resStr = response.body().string();
            JSONObject object = new JSONObject(resStr);
            object = new JSONObject(object.getString("challenge"));

            ArrayList<String> tagsArray = new ArrayList<>();
            ArrayList<String> categoriesArray = new ArrayList<>();

            ArrayList<String> trophiesArray = new ArrayList<>();

            try {
                JSONArray rewardTrophies = new JSONArray(object.getJSONObject(id).getString("rewardTrophies"));
                for (int i = 0; i < rewardTrophies.length(); ++i)
                    trophiesArray.add((String) rewardTrophies.get(i));
            } catch (JSONException ignored) {
            }

            try {
                JSONArray tags = new JSONArray(object.getJSONObject(id).getString("tags"));
                for (int i = 0; i < tags.length(); ++i) tagsArray.add((String) tags.get(i));
            } catch (JSONException ignored) {
            }


            try {
                JSONArray categories = new JSONArray(object.getJSONObject(id).getString("categories"));
                for (int i = 0; i < categories.length(); ++i)
                    categoriesArray.add((String) categories.get(i));
            } catch (JSONException ignored) {
            }

            ChallengeEntity challenge = new ChallengeEntity(object.getJSONObject(id).getString("name"),
                    object.getJSONObject(id).getString("task"),
                    object.getJSONObject(id).getString("creator_id"),
                    tagsArray,
                    categoriesArray);
            challenge.setId(id);
            challenge.setLikes(Integer.parseInt(object.getJSONObject(id).getString("likes")));
            challenge.setTimesViewed(Integer.parseInt(object.getJSONObject(id).getString("times_viewed")));
            challenge.setRewardRp(Integer.parseInt(object.getJSONObject(id).getString("rewardRp")));
            challenge.setRewardTrophies(trophiesArray);

            return challenge;
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    public static ArrayList<ChallengeEntity> getAllWithCategory(String category) {
        ArrayList<ChallengeEntity> challenges = ChallengeEntity.getAllChallenges();
        ArrayList<ChallengeEntity> a = (ArrayList<ChallengeEntity>) challenges.stream().filter(x -> x.getCategories().contains(category)).collect(Collectors.toList());
        return a;
    }

    public static ArrayList<ChallengeEntity> getAllWithTag(String tag) {
        ArrayList<ChallengeEntity> challenges = ChallengeEntity.getAllChallenges();
        ArrayList<ChallengeEntity> a = (ArrayList<ChallengeEntity>) challenges.stream().filter(x -> x.getTags().contains(tag)).collect(Collectors.toList());
        return a;
    }

    public static ArrayList<ChallengeEntity> getAllWithCategories(ArrayList<String> categories) {
        ArrayList<ChallengeEntity> challenges = ChallengeEntity.getAllChallenges();
        ArrayList<ChallengeEntity> a = (ArrayList<ChallengeEntity>) challenges.stream().filter(x -> x.getCategories().containsAll(categories)).collect(Collectors.toList());
        return a;
    }

    public static ArrayList<ChallengeEntity> getAllWithTags(ArrayList<String> tags) {
        ArrayList<ChallengeEntity> challenges = ChallengeEntity.getAllChallenges();
        ArrayList<ChallengeEntity> a = (ArrayList<ChallengeEntity>) challenges.stream().filter(x -> x.getTags().containsAll(tags)).collect(Collectors.toList());
        return a;
    }

    public long numberOfPeopleWhoAccepted() {
        ArrayList<UserEntity> users = UserEntity.getAllUsers();
        return users.stream().filter(x -> x.getUndone().contains(id)).count();
    }

    public long numberOfPeopleWhoComplete() {
        ArrayList<UserEntity> users = UserEntity.getAllUsers();
        return users.stream().filter(x -> x.getDone().contains(id)).count();
    }

    public long numberOfPeopleWhoCompleteAndAccepted() {
        ArrayList<UserEntity> users = UserEntity.getAllUsers();
        return users.stream().filter(x -> x.getDone().contains(id) || x.getUndone().contains(id)).count();
    }

    public ArrayList<CommentEntity> getAllComments() {
        ArrayList<CommentEntity> comments = CommentEntity.getAllComments();
        ArrayList<CommentEntity> a = (ArrayList<CommentEntity>) comments.stream().filter(x -> x.getChallenge_id().equals(id)).collect(Collectors.toList());
        return a;
    }

    public void update() throws IllegalArgumentException {
        Validation.validateTags(tags);
        Validation.validateTags(categories);
        Validation.validateName(name);
        Validation.validateTask(task);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("challenge_id", id)
                    .put("name", name)
                    .put("task", task)
                    .put("creator_id", creator_id)
                    .put("likes", likes)
                    .put("categories", categories)
                    .put("tags", tags)
                    .put("times_viewed", timesViewed)
                    .put("rewardRp", rewardRp)
                    .put("rewardTrophies", rewardTrophies);

            // RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("data", jsonObject.toString())
                    .build();

            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/update_challenge")
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

    public String getName() {
        return name;
    }

    public String getTask() {
        return task;
    }

    public int getLikes() {
        return likes;
    }

    public int getTimesViewed() {
        return timesViewed;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public int getRewardRp() {
        return rewardRp;
    }

    public ArrayList<String> getRewardTrophies() {
        return rewardTrophies;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTask(String task) {

        this.task = task;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setTimesViewed(int timesViewed) {
        this.timesViewed = timesViewed;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public void setTags(ArrayList<String> tags) {

        this.tags = tags;
    }

    public void setCategories(ArrayList<String> categories) {

        this.categories = categories;
    }

    public void setRewardRp(int rewardRp) {
        this.rewardRp = rewardRp;
    }

    public void setRewardTrophies(ArrayList<String> rewardTrophies) {
        this.rewardTrophies = rewardTrophies;
    }

    private void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", task='" + task + '\'' +
                ", creator_id='" + creator_id + '\'' +
                ", likes=" + likes +
                ", timesViewed=" + timesViewed +
                ", rewardRp=" + rewardRp +
                ", rewardTrophies=" + rewardTrophies +
                ", tags=" + tags +
                ", categories=" + categories +
                '}';
    }
}