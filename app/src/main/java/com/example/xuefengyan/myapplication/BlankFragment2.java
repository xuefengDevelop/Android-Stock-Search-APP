package com.example.xuefengyan.myapplication;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment2 extends Fragment {
    private ArrayAdapter<String> listAdapter ;
    private View view;
    ProgressBar progressBarOnHis;
    TextView errMessage;
    public BlankFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank_fragment2, container, false);
        progressBarOnHis = (ProgressBar) view.findViewById(R.id.progressBarOnHis);
        String tokens = getActivity().getIntent().getExtras().getString("inputString");
        String[] token = tokens.toString().split("-");
        final String companyName = token[0];
        errMessage = (TextView)view.findViewById(R.id.hisErrorMessage);
        final WebView webView = (WebView)view.findViewById(R.id.webViewWithHis);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/his.html");
        webView.addJavascriptInterface(new infoExchange(getContext()), "mytext");
        progressBarOnHis.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                webView.loadUrl("javascript:init('" + companyName + "')");
            }
        });
        return view;
    }
    private class infoExchange {
        private Context thisContext;
        public infoExchange(Context context) {
            this.thisContext = context;
        }
        @JavascriptInterface
        public void sharethis(String name) {
            String myname = name;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    progressBarOnHis.setVisibility(View.GONE);
                }
            });
        }
        @JavascriptInterface
        public void onErrorHandling(String thismessage){
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    progressBarOnHis.setVisibility(View.GONE);
                    errMessage.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
