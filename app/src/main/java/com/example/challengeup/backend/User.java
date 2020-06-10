package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class User {
    private String id;
    private String tag;
    private String nick;
    private String email;


    private int rp;
    private int totalRp;

    private ArrayList<String> categories;
    private ArrayList<String> subscriptions;
    private ArrayList<String> undone;
    private ArrayList<String> done;
    private ArrayList<String> saved;
    private ArrayList<String> trophies;
    private ArrayList<String> liked;

    private HashMap<String, String> links;

    private String photo;



    public User(String tag, String nick, String email)  {
        this.tag = tag;
        this.nick = nick;
        this.email = email;


        undone = new ArrayList<>();
        done = new ArrayList<>();
        categories = new ArrayList<>();
        subscriptions = new ArrayList<>();
        saved = new ArrayList<>();
        trophies = new ArrayList<>();
        liked = new ArrayList<>();
        id = null;
        photo = null;
        rp = 0;
        totalRp = 0;

        links = new HashMap<>();
    }
    public User(String tag, String nick, String email, ArrayList<String> categories) {
        this(tag, nick, email);
        this.categories = categories;
    }

    public static String addNewUser(User user) throws IllegalArgumentException{
        Validation.validateUserTagToBeUnique(user.tag);
        Validation.validateNickTagPassword(user.nick);
        Validation.validateNickTagPassword(user.tag);

        Validation.validateEmail(user.email);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("tag", user.tag)
                    .put("email", user.email)
                    .put("nick", user.nick)
                    .put("categories", user.categories)
                    .put("subscriptions", user.subscriptions)
                    .put("undone", user.undone)
                    .put("done", user.done)
                    .put("links", user.links)
                    .put("saved", user.saved)
                    .put("trophies", user.trophies)
                    .put("rp", user.rp)
                    .put("totalRp", user.totalRp)
                    .put("liked", user.liked);



            RequestBody requestBody;
            if (user.photo!=null){

                URL url = new URL(user.photo);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                File file = File.createTempFile("photo", null);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("data", jsonObject.toString())
                        .addFormDataPart("file", "photo", RequestBody.create(MediaType.parse("image/png"), file))
                        .build();
            }   else {
                requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("data", jsonObject.toString())
                        .build();
            }

            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/add_user")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            String resStr = response.body().string();
            JSONObject object = new JSONObject(resStr);
            user.setId(object.getString("id"));
            return object.getString("id");
        } catch (JSONException  | IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String addNewUser(String tag, String nick, String email, ArrayList<String> categories) throws IllegalArgumentException{
        Validation.validateNickTagPassword(nick);
        Validation.validateNickTagPassword(tag);
        Validation.validateEmail(email);
        Validation.validateUserTagToBeUnique(tag);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("tag", tag)
                    .put("email", email)
                    .put("nick", nick)
                    .put("categories", categories)
                    .put("subscriptions", new ArrayList())
                    .put("undone", new ArrayList())
                    .put("done", new ArrayList())
                    .put("links", new HashMap())
                    .put("saved", new ArrayList())
                    .put("trophies", new ArrayList())
                    .put("rp", 0)
                    .put("totalRp", 0)
                    .put("liked", new ArrayList<>());

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("data", jsonObject.toString())
                        .build();



            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/add_user")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            String resStr = response.body().string();
            JSONObject object = new JSONObject(resStr);
            return object.getString("id");
        } catch (JSONException  | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void addChallengeToDone(Challenge challenge){
        done.add(challenge.getId());
    }
    public void removeChallengeFromDone(Challenge challenge){
        if (done.contains(challenge.getId())) done.remove(challenge.getId());
    }
    public void addChallengeToUndone(Challenge challenge){
        undone.add(challenge.getId());
    }
    public void removeChallengeFromUndone(Challenge challenge){
        if (undone.contains(challenge.getId())) undone.remove(challenge.getId());
    }
    public void addChallengeToSaved(Challenge challenge){
        saved.add(challenge.getId());
    }
    public void removeChallengeFromSaved(Challenge challenge){
        if (saved.contains(challenge.getId())) saved.remove(challenge.getId());
    }
    public void addAchievement(Trophy trophy){trophies.add(trophy.getId());};
    public void addChallengeToLiked(Challenge challenge){
        liked.add(challenge.getId());
    }
    public void removeChallengeFromLiked(Challenge challenge){
        if (liked.contains(challenge.getId())) liked.remove(challenge.getId());
    }


    public ArrayList<Challenge> getDoneChallenges(){
        ArrayList<Challenge> challenges = new ArrayList<>();
        for (String s:done) {
            challenges.add(Challenge.getChallengeById(s));
        }
        return challenges;
    }
    public ArrayList<Challenge> getLikedChallenges(){
        ArrayList<Challenge> challenges = new ArrayList<>();
        for (String s:liked) {
            challenges.add(Challenge.getChallengeById(s));
        }
        return challenges;
    }

    public ArrayList<Challenge> getUnDoneChallenges(){
        ArrayList<Challenge> challenges = new ArrayList<>();
        for (String s:undone) {
            challenges.add(Challenge.getChallengeById(s));
        }
        return challenges;
    }
    public ArrayList<Challenge> getSavedChallenges(){
        ArrayList<Challenge> challenges = new ArrayList<>();
        for (String s:saved) {
            challenges.add(Challenge.getChallengeById(s));
        }
        return challenges;
    }
    public ArrayList<Challenge> getAllCreatedChallenges(){
        ArrayList<Challenge> challenges = Challenge.getAllChallenges();
        ArrayList<Challenge> a = (ArrayList<Challenge>) challenges.stream().filter(x->x.getCreator_id().equals(id)).collect(Collectors.toList());
        return a;
    }
    public ArrayList<Trophy> getAchievementsAsTrophies(){
        ArrayList<Trophy> trophies = new ArrayList<>();
        for (String s: this.trophies) {
            trophies.add(Trophy.getTrophyById(s));
        }
        return trophies;
    }
    public ArrayList<Trophy> getUndoneAchievementsAsTrophies(){
        ArrayList<Trophy> trophies = Trophy.getAllTrophies();
        ArrayList<Trophy> curr = getAchievementsAsTrophies();
        trophies.removeAll(curr);
        return trophies;
    }

    public ArrayList<User> getSubscriptionsAsUsers(){
        ArrayList<User> users = new ArrayList<>();
        for (String s:subscriptions) {
            users.add(User.getUserById(s));
        }
        return users;
    }
    public ArrayList<User> getSubscribersAsUsers(){
       ArrayList<User> users = getAllUsers();
       ArrayList<User> a = (ArrayList<User>) users.stream().filter(x->x.getSubscriptions().contains(id)).collect(Collectors.toList());
       return a;
    }

    public static ArrayList<User> getAllUsers(){
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/get_all_users")
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String resStr = response.body().string();
            JSONObject object = new JSONObject(resStr);
            object = new JSONObject(object.getString("users"));

            ArrayList<User> users = new ArrayList<>();
            for (Iterator<String> it = object.keys(); it.hasNext(); ) {
                String key = it.next();

                ArrayList<String> undoneArray = new ArrayList<>();
                ArrayList<String> doneArray = new ArrayList<>();
                ArrayList<String> categoriesArray = new ArrayList<>();
                ArrayList<String> subscriptionsArray = new ArrayList<>();

                HashMap<String,String> links = new HashMap<>();

                ArrayList<String> saved = new ArrayList<>();
                ArrayList<String> achievements = new ArrayList<>();

                ArrayList<String> liked = new ArrayList<>();
                try{
                    JSONArray s = new JSONArray(object.getJSONObject(key).getString("liked"));
                    for (int i = 0; i< s.length(); ++i)liked.add((String) s.get(i));
                } catch (JSONException ignored){}

                try{
                    JSONArray s = new JSONArray(object.getJSONObject(key).getString("trophies"));
                    for (int i = 0; i< s.length(); ++i)achievements.add((String) s.get(i));
                } catch (JSONException ignored){}

                try{
                    JSONArray s = new JSONArray(object.getJSONObject(key).getString("saved"));
                    for (int i = 0; i< s.length(); ++i)saved.add((String) s.get(i));
                } catch (JSONException ignored){}

                try{
                    JSONObject l = new JSONObject(object.getJSONObject(key).getString("links"));
                    if (l.has("facebook"))
                        links.put("facebook", l.getString("facebook"));

                    if (l.has("instagram"))
                        links.put("instagram", l.getString("instagram"));

                    if (l.has("youtube"))
                        links.put("youtube", l.getString("youtube"));
                } catch (JSONException ignored){}

                try{
                    JSONArray subscriptions = new JSONArray(object.getJSONObject(key).getString("subscriptions"));
                    for (int i = 0; i< subscriptions.length(); ++i)subscriptionsArray.add((String) subscriptions.get(i));
                } catch (JSONException ignored){}


                try{
                    JSONArray categories = new JSONArray(object.getJSONObject(key).getJSONArray("categories"));
                    for (int i = 0; i< categories.length(); ++i)categoriesArray.add((String) categories.get(i));
                } catch (JSONException ignored){}

                try{
                    JSONArray undone = new JSONArray(object.getJSONObject(key).getString("undone"));
                    for (int i = 0; i< undone.length(); ++i)undoneArray.add((String) undone.get(i));
                } catch (JSONException ignored){}

                try {

                    JSONArray done = new JSONArray(object.getJSONObject(key).getString("done"));
                    for (int i = 0; i< done.length(); ++i)doneArray.add((String) done.get(i));
                }catch (JSONException ignored){}

                User user = new User(object.getJSONObject(key).getString("tag"),
                        object.getJSONObject(key).getString("nick"),
                        object.getJSONObject(key).getString("email"),
                        categoriesArray);
                user.setId(key);
                user.setDone(doneArray);
                user.setUndone(undoneArray);
                user.setSubscriptions(subscriptionsArray);
                user.setLinks(links);
                user.setSaved(saved);
                user.setTrophies(achievements);
                user.setRp(Integer.parseInt(object.getJSONObject(key).getString("rp")));
                user.setTotalRp(Integer.parseInt(object.getJSONObject(key).getString("totalRp")));
                user.setLiked(liked);
                if(!object.getJSONObject(key).getString("photo_link").equals("")){
                    user.setPhoto(object.getJSONObject(key).getString("photo_link"));
                }
                users.add(user);
            }
            return users;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User getUserByEmail(String email){
        ArrayList<User> users = getAllUsers();
        try{
            User a = users.stream().filter(x->x.getEmail().equals(email)).collect(Collectors.toList()).get(0);
            return a;
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    public static User getUserById(String id){
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/get_user_by_id?user_id="+id)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String resStr = response.body().string();

            JSONObject object = new JSONObject(resStr);


            object = new JSONObject(object.getString("user"));

            ArrayList<String> undoneArray = new ArrayList<>();
            ArrayList<String> doneArray = new ArrayList<>();
            ArrayList<String> categoriesArray = new ArrayList<>();
            ArrayList<String> subscriptionsArray = new ArrayList<>();

            HashMap<String,String> links = new HashMap<>();

            ArrayList<String> saved = new ArrayList<>();
            ArrayList<String> achievements = new ArrayList<>();

            ArrayList<String> liked = new ArrayList<>();
            try{
                JSONArray s = new JSONArray(object.getJSONObject(id).getString("liked"));
                for (int i = 0; i< s.length(); ++i)liked.add((String) s.get(i));
            } catch (JSONException ignored){}

            try{
                JSONArray s = new JSONArray(object.getJSONObject(id).getString("trophies"));
                for (int i = 0; i< s.length(); ++i)achievements.add((String) s.get(i));
            } catch (JSONException ignored){}

            try{
                JSONArray s = new JSONArray(object.getJSONObject(id).getString("saved"));
                for (int i = 0; i< s.length(); ++i)saved.add((String) s.get(i));
            } catch (JSONException ignored){}

            try{
                JSONObject l = new JSONObject(object.getJSONObject(id).getString("links"));
                if (l.has("facebook"))
                    links.put("facebook", l.getString("facebook"));

                if (l.has("instagram"))
                    links.put("instagram", l.getString("instagram"));

                if (l.has("youtube"))
                    links.put("youtube", l.getString("youtube"));
            } catch (JSONException ignored){}

            try{
                JSONArray subscriptions = new JSONArray(object.getJSONObject(id).getString("subscriptions"));
                for (int i = 0; i< subscriptions.length(); ++i)subscriptionsArray.add((String) subscriptions.get(i));
            } catch (JSONException ignored){}


            try{
                JSONArray categories = new JSONArray(object.getJSONObject(id).getJSONArray("categories"));
                for (int i = 0; i< categories.length(); ++i)categoriesArray.add((String) categories.get(i));
            } catch (JSONException ignored){}

            try{
                JSONArray undone = new JSONArray(object.getJSONObject(id).getString("undone"));
                for (int i = 0; i< undone.length(); ++i)undoneArray.add((String) undone.get(i));
            } catch (JSONException ignored){}

            try {

                JSONArray done = new JSONArray(object.getJSONObject(id).getString("done"));
                for (int i = 0; i< done.length(); ++i)doneArray.add((String) done.get(i));
            }catch (JSONException ignored){}

                User user = new User(object.getJSONObject(id).getString("tag"),
                        object.getJSONObject(id).getString("nick"),
                        object.getJSONObject(id).getString("email"),
                        categoriesArray);
                user.setId(id);
                user.setUndone(undoneArray);
                user.setDone(doneArray);
                user.setSubscriptions(subscriptionsArray);
                user.setLinks(links);
                user.setSaved(saved);
                user.setTrophies(achievements);
                user.setRp(Integer.parseInt(object.getJSONObject(id).getString("rp")));
                user.setTotalRp(Integer.parseInt(object.getJSONObject(id).getString("totalRp")));
                user.setLiked(liked);
                if(!object.getJSONObject(id).getString("photo_link").equals("")){
                    user.setPhoto(object.getJSONObject(id).getString("photo_link"));
                }
            return user;
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    public void update() throws IllegalArgumentException{
        Validation.validateNickTagPassword(nick);
        Validation.validateNickTagPassword(tag);
        Validation.validateEmail(email);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("user_id", id)
                    .put("tag", tag)
                    .put("email", email)
                    .put("nick", nick)
                    .put("done", done)
                    .put("undone", undone)
                    .put("categories", categories)
                    .put("subscriptions", subscriptions)
                    .put("links", links)
                    .put("saved", saved)
                    .put("trophies", trophies)
                    .put("rp", rp)
                    .put("totalRp", totalRp)
                    .put("liked", liked);

            //RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
            RequestBody requestBody;
            if (photo!=null){

                URL url = new URL(photo);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                File file = File.createTempFile("photo", null);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
                requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("data", jsonObject.toString())
                        .addFormDataPart("file", "photo", RequestBody.create(MediaType.parse("image/png"), file))
                        .build();
            }   else {
                requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("data", jsonObject.toString())
                        .build();
            }



            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/update_user")
                    .post(requestBody)
                    .build();

            client.newCall(request).execute();

        } catch (JSONException  | IOException e) {
            e.printStackTrace();
        }
    }


    public String getId() {
        return id;
    }
    public String getTag() {
        return tag;
    }
    public String getNick() {
        return nick;
    }
    public String getEmail() {
        return email;
    }
    public ArrayList<String> getUndone() {
        return undone;
    }
    public ArrayList<String> getDone() {
        return done;
    }
    public ArrayList<String> getCategories() {
        return categories;
    }
    public ArrayList<String> getSubscriptions() {
        return subscriptions;
    }
    public String getFacebookLink(){
        if (links.containsKey("facebook"))
        return links.get("facebook");
        return "";
    }
    public String getInstagramLink(){
        if (links.containsKey("instagram"))
        return links.get("instagram");
        return "";
    }
    public String getYoutubeLink(){
        if (links.containsKey("youtube"))
        return links.get("youtube");
        return "";
    }
    public String getPhoto() {
        return photo;
    }
    public ArrayList<String> getSaved() {
        return saved;
    }
    public ArrayList<String> getTrophies() {
        return trophies;
    }
    public int getRp() {
        return rp;
    }
    public int getTotalRp() {
        return totalRp;
    }

    public void setTrophies(ArrayList<String> trophies) {
        this.trophies = trophies;
    }
    public void setTag(String tag)  {

        this.tag = tag;
    }
    public void setNick(String nick) {

        this.nick = nick;
    }
    public void setEmail(String email) {

        this.email = email;
    }
    public void setUndone(ArrayList<String> undone) {
        this.undone = undone;
    }
    public void setDone(ArrayList<String> done) {
        this.done = done;
    }
    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
    public void setSubscriptions(ArrayList<String> subscriptions) {
        this.subscriptions = subscriptions;
    }
    public void setFacebookLink(String link){
        links.put("facebook", link);
    }
    public void setInstagramLink(String link){
        links.put("instagram", link);
    }
    public void setYuotubeLink(String link){
        links.put("youtube", link);
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public void setRp(int rp) {
        this.rp = rp;
    }
    public void setSaved(ArrayList<String> saved) {
        this.saved = saved;
    }
    public void setTotalRp(int totalRp) {
        this.totalRp = totalRp;
    }

    private void setLinks(HashMap<String, String> links) {
        this.links = links;
    }
    private void setId(String id){
        this.id = id;
    }

    public ArrayList<String> getLiked() {
        return liked;
    }

    public void setLiked(ArrayList<String> liked) {
        this.liked = liked;
    }
}
