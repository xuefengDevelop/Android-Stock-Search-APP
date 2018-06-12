package com.example.xuefengyan.myapplication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.widget.AdapterView.OnItemClickListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TableRow.LayoutParams;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Iterator;
import java.text.DecimalFormat;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {
    public final String urlForTableContent = "http://stockmarket.us-east-2.elasticbeanstalk.com/pricetable/";
    private String currentPrice, change, open, close, DayRange , volume;
    private double changepercent;
    private RequestQueue requestQueue;
    private int lastPostionOfItem;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private ArrayAdapter<String> listAdapter ;
    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss' EST'");
    private String Facebooklink;
    private String testNUlls;
    ProgressBar secondBar;
    TextView errorTextView;
    View view;
    public static final String PREFS_NAME = "my_prefrence_list";
    int []myint = {0};
    public BlankFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view == null){
        String tokens = getActivity().getIntent().getExtras().getString("inputString");
        String[] token = tokens.toString().split("-");
        final String companyName = token[0];
        isoFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        final boolean judgeOpen = (hour > 9 || (hour == 9 && minute >= 30)) && (hour < 16);
        Date date = new Date();
        final String mytime = isoFormat.format(date);
        final String url = urlForTableContent + companyName;
        view = inflater.inflate(R.layout.fragment_blank, container, false);
        final String[] nameArray = {"Stock Symbol","Last Price","Change","Timestamp","Open","CLose","Day's Range","Volume"};
        final Integer[] imageArray = {0,0,R.drawable.up,0,0,0,0,0};
        final String[] infoArray = new String[8];
        final ProgressBar firstbar = (ProgressBar)view.findViewById(R.id.firstBar) ;
        final TextView ErrorMessage = (TextView)view.findViewById(R.id.listViewErrorMessage) ;
        firstbar.setVisibility(View.VISIBLE);
        errorTextView = (TextView)view.findViewById(R.id.webViewErrorMessage) ;
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url,null ,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject posts = response.getJSONObject("Time Series (Daily)");
                            Iterator<String> keys = posts.keys();
                            String str_Name=keys.next();
                            String passTime = str_Name + " 16:00:00 EST";
                            JSONObject thisObject = posts.getJSONObject(str_Name);
                            double price = thisObject.getDouble("4. close");
                            double openPrice = thisObject.getDouble("1. open");
                            double highPrice = thisObject.getDouble("2. high");
                            double lowPrice = thisObject.getDouble("3. low");
                            int volumeNumber = thisObject.getInt("5. volume");
                            currentPrice = decimalFormat.format(price);
                            String lastDay = keys.next();
                            JSONObject lastDayObject = posts.getJSONObject(lastDay);
                            double lastDayClosePrice = lastDayObject.getDouble("4. close");
                            double changeNumber = price - lastDayClosePrice;
                            changepercent = changeNumber;
                            double changePercent = changeNumber/lastDayClosePrice * 100;
                            change = decimalFormat.format(changeNumber) + '('+ decimalFormat.format(changePercent) +"%)";
                            open = decimalFormat.format(openPrice);
                            close = decimalFormat.format(lastDayClosePrice);
                            DayRange = decimalFormat.format(lowPrice) + '-' +decimalFormat.format(highPrice);
                            volume = volumeNumber+"";
                            if(changeNumber<0){
                                imageArray[2] = R.drawable.arrowdownbutton;
                            }
                            infoArray[0] = companyName;
                            infoArray[1] = currentPrice;
                            infoArray[2] = change;
                            if(!judgeOpen){
                                infoArray[3] = passTime;
                            } else {
                            infoArray[3] = mytime;}
                            infoArray[4] = open;
                            infoArray[5] = close;
                            infoArray[6] = DayRange;
                            infoArray[7] = volume;
                            CustomListAdapter mainDetailview = new CustomListAdapter(getActivity(), nameArray, infoArray, imageArray);
                            ListView listView = (ListView) view.findViewById(R.id.mainListView);
                            listView.setAdapter(mainDetailview);
                            firstbar.setVisibility(View.GONE);
                            testNUlls = "mytest";
                        } catch (JSONException e) {
                            e.printStackTrace();
                            firstbar.setVisibility(View.GONE);
                            ErrorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        firstbar.setVisibility(View.GONE);
                        ErrorMessage.setVisibility(View.VISIBLE);
                    }
                });
        requestQueue.add(jsonArrayRequest);

        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner2);
        String[] plants = new String[]{
                "Price",
                "SMA",
                "EMA",
                "STOCH",
                "RSI",
                "ADX",
                "CCI",
                "BBANDS",
                "MACD"
        };

        final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));
        final Button button = (Button) view.findViewById(R.id.changebutton);
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,plantsList);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
        lastPostionOfItem = 0;
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if(position == lastPostionOfItem){
                        button.setEnabled(false);
                    }else{
                        button.setEnabled(true);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    button.setEnabled(false);
                }

            });

            // webview section
        final WebView webView = (WebView)view.findViewById(R.id.webViewWithIndicators);
            secondBar = (ProgressBar) view.findViewById(R.id.secondProgressBar);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setPadding(0, 0, 0, 0);
        secondBar.setVisibility(View.VISIBLE);
        webView.loadUrl("file:///android_asset/price.html");
        webView.addJavascriptInterface(new infoExchange(getContext()), "mytext");
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                webView.loadUrl("javascript:init('" + companyName + "')");
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                secondBar.setVisibility(View.VISIBLE);
                button.setEnabled(false);
               if(spinner.getSelectedItemPosition() == 0){
                   lastPostionOfItem = 0;
                   webView.loadUrl("file:///android_asset/price.html");
                   webView.addJavascriptInterface(new infoExchange(getContext()), "mytext");
                   webView.setWebViewClient(new WebViewClient(){
                       public void onPageFinished(WebView view, String url){
                           webView.loadUrl("javascript:init('" + companyName + "')");
                       }
                   });
               }
                if(spinner.getSelectedItemPosition() == 1){
                    lastPostionOfItem = 1;
                    final WebView webView = (WebView)view.findViewById(R.id.webViewWithIndicators);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl("file:///android_asset/sma.html");
                    webView.addJavascriptInterface(new infoExchange(getContext()), "mytext");
                    webView.setWebViewClient(new WebViewClient(){
                        public void onPageFinished(WebView view, String url){
                            webView.loadUrl("javascript:init('" + companyName + "')");
                        }
                    });
                }
                if(spinner.getSelectedItemPosition() == 2){
                    lastPostionOfItem = 2;
                    final WebView webView = (WebView)view.findViewById(R.id.webViewWithIndicators);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl("file:///android_asset/ema.html");
                    webView.addJavascriptInterface(new infoExchange(getContext()), "mytext");
                    webView.setWebViewClient(new WebViewClient(){
                        public void onPageFinished(WebView view, String url){
                            webView.loadUrl("javascript:init('" + companyName + "')");
                        }
                    });
                }
                if(spinner.getSelectedItemPosition() == 3){
                    lastPostionOfItem = 3;
                    final WebView webView = (WebView)view.findViewById(R.id.webViewWithIndicators);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl("file:///android_asset/stoch.html");
                    webView.addJavascriptInterface(new infoExchange(getContext()), "mytext");
                    webView.setWebViewClient(new WebViewClient(){
                        public void onPageFinished(WebView view, String url){
                            webView.loadUrl("javascript:init('" + companyName + "')");
                        }
                    });
                }
                if(spinner.getSelectedItemPosition() == 4){
                    lastPostionOfItem = 4;
                    final WebView webView = (WebView)view.findViewById(R.id.webViewWithIndicators);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl("file:///android_asset/rsi.html");
                    webView.addJavascriptInterface(new infoExchange(getContext()), "mytext");
                    webView.setWebViewClient(new WebViewClient(){
                        public void onPageFinished(WebView view, String url){
                            webView.loadUrl("javascript:init('" + companyName + "')");
                        }
                    });
                }
                if(spinner.getSelectedItemPosition() == 5){
                    lastPostionOfItem = 5;
                    final WebView webView = (WebView)view.findViewById(R.id.webViewWithIndicators);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl("file:///android_asset/adx.html");
                    webView.setWebViewClient(new WebViewClient(){
                        public void onPageFinished(WebView view, String url){
                            webView.loadUrl("javascript:init('" + companyName + "')");
                        }
                    });
                }
                if(spinner.getSelectedItemPosition() == 6){
                    lastPostionOfItem = 6;
                    final WebView webView = (WebView)view.findViewById(R.id.webViewWithIndicators);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl("file:///android_asset/cci.html");
                    webView.addJavascriptInterface(new infoExchange(getContext()), "mytext");
                    webView.setWebViewClient(new WebViewClient(){
                        public void onPageFinished(WebView view, String url){
                            webView.loadUrl("javascript:init('" + companyName + "')");
                        }
                    });
                }
                if(spinner.getSelectedItemPosition() == 7){
                    lastPostionOfItem = 7;
                    final WebView webView = (WebView)view.findViewById(R.id.webViewWithIndicators);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl("file:///android_asset/bbands.html");
                    webView.addJavascriptInterface(new infoExchange(getContext()), "mytext");
                    webView.setWebViewClient(new WebViewClient(){
                        public void onPageFinished(WebView view, String url){
                            webView.loadUrl("javascript:init('" + companyName + "')");
                        }
                    });
                }
                if(spinner.getSelectedItemPosition() == 8){
                    lastPostionOfItem = 8;
                    final WebView webView = (WebView)view.findViewById(R.id.webViewWithIndicators);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl("file:///android_asset/macd.html");
                    webView.addJavascriptInterface(new infoExchange(getContext()), "mytext");
                    webView.setWebViewClient(new WebViewClient(){
                        public void onPageFinished(WebView view, String url){
                            webView.loadUrl("javascript:init('" + companyName + "')");
                        }
                    });
                }
            }
        });
        final SharedPreferences.Editor editor = getContext().getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE).edit();
        final SharedPreferences SP = getContext().getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        final ImageButton btnstar =(ImageButton) view.findViewById(R.id.starsymbol);
        if (SP.contains(companyName)) {
            btnstar.setBackgroundResource(R.drawable.filled);
        }
        btnstar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(testNUlls!= null){
                    if(SP.contains(companyName)){
                        btnstar.setBackgroundResource(R.drawable.empty);
                        editor.remove(companyName).commit();
                    } else {
                        btnstar.setBackgroundResource(R.drawable.filled);
                        String mynewString = companyName+"$"+currentPrice+"$"+change+"$"+changepercent;
                        editor.putString(companyName,mynewString);
                        editor.apply();
                }}
            }
        });
        final ImageButton FBbutton  =(ImageButton) view.findViewById(R.id.facebookButton);
            FBbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(Facebooklink!= null){
                    ShareLinkContent fbsharecontent = new ShareLinkContent.Builder().setContentUrl(Uri.parse(Facebooklink)).build();
                    ShareDialog thisShareMessage = new ShareDialog(getActivity());
                    thisShareMessage.show(fbsharecontent, ShareDialog.Mode.AUTOMATIC);}
                }
            });
            return view;
       }
       return view;
    }
    private class infoExchange {
        private Context thisContext;
        public infoExchange(Context context) {
            this.thisContext = context;
        }
        @JavascriptInterface
        public void sharethis(String name) {
            Facebooklink = name;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    secondBar.setVisibility(View.GONE);
                }
            });
        }
        @JavascriptInterface
        public void onErrorHandling(String thismessage){
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    secondBar.setVisibility(View.GONE);
                    errorTextView.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
