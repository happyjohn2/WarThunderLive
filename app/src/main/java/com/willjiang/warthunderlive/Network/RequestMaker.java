package com.willjiang.warthunderlive.Network;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.etsy.android.grid.StaggeredGridView;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.willjiang.warthunderlive.PostsAdapter;
import com.willjiang.warthunderlive.PostsPagerAdapter;
import com.willjiang.warthunderlive.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.transform.Result;

/**
 * Created by Will on 9/1/15.
 */
public class RequestMaker extends AsyncTask<Bundle , Void, String> {
    private Context mContext;
    private View rootView;
    private List response;
    private Bundle args;


    private OkHttpClient CLIENT = new OkHttpClient();

    public RequestMaker () {
        super();
    }

    public RequestMaker(Context context, View rootView, Bundle args) {
        super();
        this.rootView = rootView;
        this.args = args;
        this.mContext = context;
    }

    public InputStream makeRequest() throws IOException {

        String type = args.getString("type");
        Request request;
        Response response;

        if (type.equals("feed")) {

            String content = String.valueOf(args.getInt("index"));
            String page = String.valueOf(args.getInt("page"));
            String period = String.valueOf(args.getInt("period"));

            RequestBody postBody = new FormEncodingBuilder()
                    .add("page", page)
                    .add("content", content)
                    .add("sort", "1")
                    .add("user", "0")
                    .add("period", period)
                    .build();

            request = new Request.Builder()
                    .url(API.unLogged)
                    .post(postBody)
                    .build();

//        } else if (type.equals("post")) {

        } else {

            String id = String.valueOf(args.getInt("id"));

            RequestBody postBody = new FormEncodingBuilder()
            .add("lang_group", id)
            .add("language", "en")
            .build();

            request = new Request.Builder()
                    .url(API.postURL)
                    .post(postBody)
                    .build();
        }

        response = CLIENT.newCall(request).execute();
        return response.body().byteStream();
    }

    @Override
    protected String doInBackground(Bundle... params) {
        try {
            JsonParser parser = new JsonParser(this.mContext);
            response = parser.readJsonStream(makeRequest());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        String type = args.getString("type");
        if (type.equals("feed")) {
            RecyclerView posts_list = (RecyclerView) rootView.findViewById(R.id.posts_list);
            ((PostsAdapter) posts_list.getAdapter()).addAll(response);
        } else {
        }
    }

}



