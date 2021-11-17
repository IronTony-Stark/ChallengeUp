package com.example.challengeup.backend;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChallengeEntity {
    private final LocalDateTime dateTime;
    private String id;
    private String name;
    private String task;
    private String creator_id;
    private int likes;
    private int timesViewed;
    private int rewardRp;
    private int reportsCount;
    private boolean isBlocked;
    private ArrayList<String> rewardTrophies;
    private ArrayList<String> tags;
    private ArrayList<String> categories;
    private String photo;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ChallengeEntity(String name, String task, String creator_id, ArrayList<String> tags, ArrayList<String> categories) {
        this(name, task, creator_id);

        this.tags = tags;
        this.categories = categories;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        photo = null;

        dateTime = LocalDateTime.now();
    }

    private ChallengeEntity(String name, String task, String creator_id, ArrayList<String> tags, ArrayList<String> categories, LocalDateTime localDateTime) {
        this.name = name;
        this.task = task;
        this.creator_id = creator_id;
        likes = 0;
        timesViewed = 0;
        rewardRp = 0;
        rewardTrophies = new ArrayList<>();
        id = null;
        photo = null;
        this.tags = tags;
        this.categories = categories;

        dateTime = localDateTime;

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
                    .put("rewardTrophies", challenge.rewardTrophies)
                    .put("dateTime", challenge.dateTime)
                    .put("reportsCount", 0)
                    .put("isBlocked", false);


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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                    .put("rewardTrophies", new ArrayList())
                    .put("dateTime", LocalDateTime.now())
                    .put("reportsCount", 0)
                    .put("isBlocked", false);

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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                        categoriesArray,
                        LocalDateTime.parse(object.getJSONObject(key).getString("dateTime")));
                challenge.setId(key);
                challenge.setLikes(Integer.parseInt(object.getJSONObject(key).getString("likes")));
                challenge.setTimesViewed(Integer.parseInt(object.getJSONObject(key).getString("times_viewed")));
                challenge.setRewardRp(Integer.parseInt(object.getJSONObject(key).getString("rewardRp")));
                challenge.setRewardTrophies(trophiesArray);
                challenge.setBlocked(object.getJSONObject(key).getString("isBlocked")));
                challenge.setReportsCount(object.getJSONObject(key).getString("reportsCount"));
                if (!object.getJSONObject(key).getString("photo_link").equals("")) {
                    challenge.setPhoto(object.getJSONObject(key).getString("photo_link"));
                }
                challenges.add(challenge);
            }
            return challenges;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<ChallengeEntity> search(String query, Integer liked, Integer accepted, Integer completed, Integer rp, List<String> categories, OrderBy orderBy, OrderDirection orderDirection) {


        Predicate<ChallengeEntity> predicate = challengeEntity -> true;

        if (!Objects.isNull(liked))
            predicate = predicate.and(challengeEntity -> challengeEntity.likes >= liked);
        if (!Objects.isNull(rp))
            predicate = predicate.and(challengeEntity -> challengeEntity.getRewardRp() >= rp);
        if (!Objects.isNull(accepted))
            predicate = predicate.and(challengeEntity -> challengeEntity.numberOfPeopleWhoAccepted() >= accepted);
        if (!Objects.isNull(completed))
            predicate = predicate.and(challengeEntity -> challengeEntity.numberOfPeopleWhoComplete() > completed);

        if (!Objects.isNull(query)) {
            Predicate<ChallengeEntity> predicate1 = challengeEntity -> challengeEntity.getName().toLowerCase().contains(query.toLowerCase());
            predicate1 = predicate1.or(challengeEntity -> challengeEntity.getTags().parallelStream().anyMatch(x -> x.toLowerCase().contains(query.toLowerCase())));
            predicate1 = predicate1.or(challengeEntity -> challengeEntity.getTask().toLowerCase().contains(query.toLowerCase()));
            predicate = predicate.and(predicate1);
        }

        if (!categories.isEmpty()) {
            predicate = predicate.and(challengeEntity -> challengeEntity.getCategories().stream().anyMatch(categories::contains));
        }

        Comparator<ChallengeEntity> challengeEntityComparator;
        switch (orderBy) {
            case Liked: {
                challengeEntityComparator = Comparator.comparing(ChallengeEntity::getLikes);
                break;
            }
            case RP: {
                challengeEntityComparator = Comparator.comparing(ChallengeEntity::getRewardRp);
                break;
            }
            case Accepted: {
                challengeEntityComparator = Comparator.comparing(ChallengeEntity::numberOfPeopleWhoAccepted);
                break;
            }
            case Completed: {
                challengeEntityComparator = Comparator.comparing(ChallengeEntity::numberOfPeopleWhoComplete);
                break;
            }
            default:
                challengeEntityComparator = Comparator.comparing(ChallengeEntity::getName);
        }

        switch (orderDirection) {
            case Descending:
                challengeEntityComparator = challengeEntityComparator.reversed();
        }

        Stream<ChallengeEntity> stream = ChallengeEntity.getAllChallenges().parallelStream();

        return (ArrayList<ChallengeEntity>) stream.filter(predicate).sorted(challengeEntityComparator).collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                    categoriesArray,
                    LocalDateTime.parse(object.getJSONObject(id).getString("dateTime")));
            challenge.setId(id);
            challenge.setLikes(Integer.parseInt(object.getJSONObject(id).getString("likes")));
            challenge.setTimesViewed(Integer.parseInt(object.getJSONObject(id).getString("times_viewed")));
            challenge.setRewardRp(Integer.parseInt(object.getJSONObject(id).getString("rewardRp")));
            challenge.setRewardTrophies(trophiesArray);
            challenge.setBlocked(object.getJSONObject(key).getString("isBlocked")));
            challenge.setReportsCount(object.getJSONObject(key).getString("reportsCount"));
            if (!object.getJSONObject(id).getString("photo_link").equals("")) {
                challenge.setPhoto(object.getJSONObject(id).getString("photo_link"));
            }

            return challenge;
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<ChallengeEntity> getAllWithCategory(String category) {
        ArrayList<ChallengeEntity> challenges = ChallengeEntity.getAllChallenges();
        ArrayList<ChallengeEntity> a = (ArrayList<ChallengeEntity>) challenges.stream().filter(x -> x.getCategories().contains(category)).collect(Collectors.toList());
        return a;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<ChallengeEntity> getAllWithTag(String tag) {
        ArrayList<ChallengeEntity> challenges = ChallengeEntity.getAllChallenges();
        ArrayList<ChallengeEntity> a = (ArrayList<ChallengeEntity>) challenges.stream().filter(x -> x.getTags().contains(tag)).collect(Collectors.toList());
        return a;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<ChallengeEntity> getAllWithCategories(ArrayList<String> categories) {
        ArrayList<ChallengeEntity> challenges = ChallengeEntity.getAllChallenges();
        ArrayList<ChallengeEntity> a = (ArrayList<ChallengeEntity>) challenges.stream().filter(x -> x.getCategories().containsAll(categories)).collect(Collectors.toList());
        return a;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<ChallengeEntity> getAllWithTags(ArrayList<String> tags) {
        ArrayList<ChallengeEntity> challenges = ChallengeEntity.getAllChallenges();
        ArrayList<ChallengeEntity> a = (ArrayList<ChallengeEntity>) challenges.stream().filter(x -> x.getTags().containsAll(tags)).collect(Collectors.toList());
        return a;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ArrayList<VideoConfirmationEntity> getAllConfirmedVideos() {
        return (ArrayList<VideoConfirmationEntity>) VideoConfirmationEntity.getAllVideos().stream().filter(x -> x.getChallenge_id().equals(id) && x.isConfirmed()).collect(Collectors.toList());
    }

    public ArrayList<VideoConfirmationEntity> getAllUnconfirmedVideos() {
        return (ArrayList<VideoConfirmationEntity>) VideoConfirmationEntity.getAllVideos().stream().filter(x -> x.getChallenge_id().equals(id) && !x.isConfirmed()).collect(Collectors.toList());
    }

    public ArrayList<VideoConfirmationEntity> getAllVideos() {
        return (ArrayList<VideoConfirmationEntity>) VideoConfirmationEntity.getAllVideos().stream().filter(x -> x.getChallenge_id().equals(id)).collect(Collectors.toList());
    }

    public long numberOfPeopleWhoAccepted() {
        ArrayList<UserEntity> users = UserEntity.getAllUsers();
        return users.stream().filter(x -> x.getUndone().contains(id)).count();
    }

    public ArrayList<UserEntity> peopleWhoAccepted() {
        ArrayList<UserEntity> users = UserEntity.getAllUsers();
        return (ArrayList<UserEntity>) users.stream().filter(x -> x.getUndone().contains(id)).collect(Collectors.toList());
    }

    public long numberOfPeopleWhoComplete() {
        ArrayList<UserEntity> users = UserEntity.getAllUsers();
        return users.stream().filter(x -> x.getDone().contains(id)).count();
    }

    public ArrayList<UserEntity> peopleWhoComplete() {
        ArrayList<UserEntity> users = UserEntity.getAllUsers();
        return (ArrayList<UserEntity>) users.stream().filter(x -> x.getDone().contains(id)).collect(Collectors.toList());
    }

    public long numberOfPeopleWhoCompleteAndAccepted() {
        ArrayList<UserEntity> users = UserEntity.getAllUsers();
        return users.stream().filter(x -> x.getDone().contains(id) || x.getUndone().contains(id)).count();
    }

    public ArrayList<UserEntity> peopleWhoCompleteAndAccepted() {
        ArrayList<UserEntity> users = UserEntity.getAllUsers();
        return (ArrayList<UserEntity>) users.stream().filter(x -> x.getDone().contains(id) || x.getUndone().contains(id)).collect(Collectors.toList());
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
                    .put("rewardTrophies", rewardTrophies)
                    .put("dateTime", dateTime)
                    .put("reportsCount", reportsCount)
                    .put("isBlocked", isBlocked);

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

    private void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {

        this.task = task;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getTimesViewed() {
        return timesViewed;
    }

    public void setTimesViewed(int timesViewed) {
        this.timesViewed = timesViewed;
    }

    public int getReportsCount() {
        return reportsCount;
    }

    public void setReportsCount(int reportsCount) {
        this.reportsCount = reportsCount;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {

        this.tags = tags;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {

        this.categories = categories;
    }

    public int getRewardRp() {
        return rewardRp;
    }

    public void setRewardRp(int rewardRp) {
        this.rewardRp = rewardRp;
    }

    public ArrayList<String> getRewardTrophies() {
        return rewardTrophies;
    }

    public void setRewardTrophies(ArrayList<String> rewardTrophies) {
        this.rewardTrophies = rewardTrophies;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "ChallengeEntity{" +
                "dateTime=" + dateTime +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", task='" + task + '\'' +
                ", creator_id='" + creator_id + '\'' +
                ", likes=" + likes +
                ", timesViewed=" + timesViewed +
                ", rewardRp=" + rewardRp +
                ", reportsCount=" + reportsCount +
                ", isBlocked=" + isBlocked +
                ", rewardTrophies=" + rewardTrophies +
                ", tags=" + tags +
                ", categories=" + categories +
                ", photo='" + photo + '\'' +
                '}';
    }
}