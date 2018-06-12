package com.example.xuefengyan.myapplication;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment3 extends Fragment {
    public BlankFragment3() {
        // Required empty public constructor
    }
    private RequestQueue requestQueue;
    final String[] titleArray = new String[5];
    final String[] authorArray = new String[5];;
    final String[] dateArray = new String[5];;
    final String[] linkArray = new String[5];;
    public final String urlForTableContent = "http://stockmarket.us-east-2.elasticbeanstalk.com/news/";
    ListView listView;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_blank_fragment3, container, false);
            String tokens = getActivity().getIntent().getExtras().getString("inputString");
            String[] token = tokens.toString().split("-");
            final String companyName = token[0];
            String url = urlForTableContent + companyName;
            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            final ProgressBar progresbar = (ProgressBar) view.findViewById(R.id.newProgress);
            final TextView mymessage = (TextView) view.findViewById(R.id.lastPageError);
            progresbar.setVisibility(View.VISIBLE);
            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject posts = response.getJSONObject("rss");
                                JSONArray newObject = posts.getJSONArray("channel");
                                posts = newObject.getJSONObject(0);
                                newObject = posts.getJSONArray("item");
                                int j = 0;
                                for (int i = 0; i < newObject.length(); i++) {
                                    if (j == 5) {
                                        break;
                                    }
                                    JSONObject jresponse = newObject.getJSONObject(i);
                                    JSONArray links = jresponse.getJSONArray("link");
                                    String link = links.getString(0);
                                    if (link.contains("article")) {

                                        JSONArray titles = jresponse.getJSONArray("title");
                                        String title = titles.getString(0);
                                        JSONArray dates = jresponse.getJSONArray("pubDate");
                                        String date = dates.getString(0);
                                        date = date.substring(0, 26);
                                        date = date + "EST";
                                        JSONArray author = jresponse.getJSONArray("sa:author_name");
                                        String authors = author.getString(0);
                                        titleArray[j] = title;
                                        authorArray[j] = authors;
                                        dateArray[j] = date;
                                        linkArray[j] = link;
                                        j++;
                                    }
                                    CustomNewsListAdapter mycustomerlist = new CustomNewsListAdapter(getActivity(), titleArray, authorArray, dateArray);
                                    listView = (ListView) view.findViewById(R.id.newsListView);
                                    listView.setAdapter(mycustomerlist);
                                    listView.setOnItemClickListener(new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Uri uri = Uri.parse(linkArray[position]);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            getContext().startActivity(intent);
                                        }
                                    });
                                    //progresbar.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            progresbar.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progresbar.setVisibility(View.GONE);
                            mymessage.setVisibility(View.VISIBLE);
                        }
                    });
            requestQueue.add(jsonArrayRequest);
            return view;
        }else{
        return view;}
    }
}
