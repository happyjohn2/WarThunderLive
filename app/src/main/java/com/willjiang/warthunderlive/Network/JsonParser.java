package com.willjiang.warthunderlive.Network;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.willjiang.warthunderlive.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {

    Context mContext;

    public JsonParser (Context context) {
        this.mContext = context;
    }

    public List readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readPostArray(reader);
        } finally {
            reader.close();
        }
    }

    public List readPostArray(JsonReader reader) throws IOException {
        List posts = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            posts.add(readPost(reader));
        }
        reader.endArray();
        return posts;
    }

    public HashMap readPost (JsonReader reader) throws  IOException {
        HashMap post = new HashMap();
        reader.beginObject();

        post.put(API.description, "");
        post.put(API.images, new ArrayList());

        while (reader.hasNext()) {
            String key = reader.nextName();
            if (key.equals(API.description)) {
                post.put(key, reader.nextString());
            } else if (key.equals(API.language)) {
                post.put(key, reader.nextString());
            } else if (key.equals(API.id)) {
                post.put(key, reader.nextString());
            } else if (key.equals(API.images)) {
                post.put(key, readImages(reader));
            } else if (key.equals(API.author)) {
                post.put(key, readAuthor(reader));
            } else if (key.equals(API.timestamp)) {
                post.put(key, reader.nextString());
            } else if (key.equals(API.video_info)) {
                post.put(key, readVideoImage(reader));
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return post;
    }

    public HashMap<String, String> readAuthor(JsonReader reader) throws IOException {
        HashMap author = new HashMap<String, String>();
        reader.beginObject();
        while (reader.hasNext()) {
            while (reader.hasNext()) {
                String key = reader.nextName();
                if (key.equals(API.author_avatar)) {
                    String imgURL = reader.nextString();
                    if (imgURL.length() > 80) {
                        imgURL = Utils.imageQuality(imgURL, 2);
                    }
                    author.put(key, imgURL);
                } else if (key.equals(API.author_nickname)){
                    author.put(key, reader.nextString());
                } else if (key.equals(API.author_id)) {
                    author.put(key, reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
        }
        reader.endObject();
        return author;
    }

    public List readImages(JsonReader reader) throws IOException {
        List images = new ArrayList();
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            while (reader.hasNext()) {
                String key = reader.nextName();
                if (key.equals(API.image_src)) {
                    if (reader.peek() != JsonToken.BOOLEAN) {
                        String imgURL = reader.nextString();
                        imgURL = Utils.imageQuality(imgURL, 1);
                        images.add(imgURL);
                    } else {
                        reader.skipValue();
                    }
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        reader.endArray();
        return images;
    }

    public String readVideoImage(JsonReader reader) throws IOException {
        String vid_image = null;
        reader.beginObject();
        while (reader.hasNext()) {
                String key = reader.nextName();
                if (key.equals(API.video_image_src)) {
                    vid_image = reader.nextString();
                } else if (key.equals(API.video_type)){
                    if (!reader.nextString().equals("youtube")) {
                        vid_image = null;
                    }
                } else {
                    reader.skipValue();
                }
            }
        reader.endObject();
        return vid_image;
    }


}
