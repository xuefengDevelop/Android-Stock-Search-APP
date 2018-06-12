package com.example.xuefengyan.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.lang.*;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import com.android.volley.DefaultRetryPolicy;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import java.text.DecimalFormat;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import java.util.*;
import android.app.AlertDialog;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.support.v7.widget.PopupMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity{
    AutoCompleteTextView auto;
    List<String> myArr = new ArrayList<String>();
    private RequestQueue requestQueue;
    private TextView textView;
    private  TextView responseName;
    Context context;
    public String myinputText;
    public int count;
    public static final String PREFS_NAME = "my_prefrence_list";
    private String[] title;
    public final String urlForTableContent = "http://stockmarket.us-east-2.elasticbeanstalk.com/pricetable/";
    private String[] price;
    private Runnable thisrefresh;
    private String[] change;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private Double[] changeNumber;
    CustomFavListAdapter mylist;
    private String currentPrice, changetext, open, close, DayRange , volume;
    private String[][] arrForSorting;
    ProgressBar thisbar;
    public final String thisInputUrl = "http://stockmarket.us-east-2.elasticbeanstalk.com/auto/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        final Activity context = this;
        auto = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        thisbar = (ProgressBar)findViewById(R.id.thisProgressBar) ;
        newAdapterWithHttp adapter = new newAdapterWithHttp (this, android.R.layout.select_dialog_item);
        auto.setThreshold(1);
        auto.setAdapter(adapter);
        final ProgressBar thisProgressBar = (ProgressBar)findViewById(R.id.mainPageProgressBar) ;
        Button button = (Button) findViewById(R.id.button1);
        Button clear = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myinputText  = auto.getText().toString();
                if (myinputText.matches("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter a stock name or symbol", Toast.LENGTH_SHORT).show();
                    return;
                } else{
                Intent i = new Intent(getApplicationContext(),mainPageActivity.class);
                i.putExtra("inputString",myinputText);
                startActivity(i);
                }

            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                auto.setText("");
            }
        });

        final SharedPreferences.Editor editor = this.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE).edit();
        final SharedPreferences SP = this.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        final Map<String, ?> allEntries = SP.getAll();
        count = SP.getAll().size();
        title = new String[count];
        price = new String[count];
        change = new String[count];
        changeNumber = new Double[count];
        int i =0;
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String a = entry.getValue().toString();
            String[] parts = entry.getValue().toString().split("\\$");
            title[i] = parts[0];
            price[i] = parts[1];
            change[i] = parts[2];
            changeNumber[i] = Double.parseDouble(parts[3]);
            i++;
        }
        mylist = new CustomFavListAdapter(this, title, price, change,changeNumber);
        final ListView listView = (ListView) findViewById(R.id.favlistView);
        listView.setAdapter(mylist);
        listView.setFocusable(false);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String name = parent.getItemAtPosition(position).toString();
                Intent i = new Intent(getApplicationContext(),mainPageActivity.class);
                i.putExtra("inputString",name);
                startActivity(i);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                PopupMenu popup = new PopupMenu(MainActivity.this, view,Gravity.RIGHT);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                final String name = parent.getItemAtPosition(position).toString();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("yes")) {
                            try {
                                editor.remove(name).commit();
                                count = SP.getAll().size();
                                title = new String[count];
                                price = new String[count];
                                change = new String[count];
                                changeNumber = new Double[count];
                                final Map<String, ?> newEntries = SP.getAll();
                                int i = 0;
                                for (Map.Entry<String, ?> entry : newEntries.entrySet()) {
                                    String a = entry.getValue().toString();
                                    String[] parts = entry.getValue().toString().split("\\$");
                                    title[i] = parts[0];
                                    price[i] = parts[1];
                                    change[i] = parts[2];
                                    changeNumber[i] = Double.parseDouble(parts[3]);
                                    i++;
                                }
                                mylist = new CustomFavListAdapter(context, title, price, change, changeNumber);
                                listView.setAdapter(mylist);
                                mylist.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    }
                });
                popup.getMenu().findItem(R.id.one).setEnabled(false);
                popup.show();//showing popup menu
                return true;
            }
        });
        final Spinner sortSinnper = (Spinner) findViewById(R.id.spinner);
        final Spinner orderSinnper = (Spinner) findViewById(R.id.spinner1);
        orderSinnper.setEnabled(false);
        String[] plants = new String[]{"Sort By","Default","Symbol","Price","Change"};

        final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,plantsList){
            @Override
            public boolean isEnabled(int position) {
                if (position == 0 && position == sortSinnper.getSelectedItemPosition()) {
                    orderSinnper.setEnabled(false);
                    return false;
                } else {
                    orderSinnper.setEnabled(true);
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    tv.setTextColor(Color.LTGRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };


        //working on this section
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        sortSinnper.setAdapter(spinnerArrayAdapter);
        sortSinnper.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                int orderposition = orderSinnper.getSelectedItemPosition();
                if(position != 0 && position != 1){
                    final Map<String, ?> myentries = SP.getAll();
                    int i =0;
                    arrForSorting = new String[count][4];
                    for (Map.Entry<String, ?> entry : myentries.entrySet()) {
                        String a = entry.getValue().toString();
                        String[] parts = entry.getValue().toString().split("\\$");
                        arrForSorting[i][0] = parts[0];
                        arrForSorting[i][1] = parts[1];
                        arrForSorting[i][2] = parts[2];
                        arrForSorting[i][3] = parts[3];
                        i++;
                    }
                    if(orderposition == 0 && position==4){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                Double d1 = Double.valueOf(array1[3]);
                                Double d2 = Double.valueOf(array2[3]);
                                return d1.compareTo(d2);
                            }
                        });}
                    if(position == 3 && orderposition == 0 ){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                Double d1 = Double.valueOf(array1[1]);
                                Double d2 = Double.valueOf(array2[1]);
                                return d1.compareTo(d2);
                            }
                        });
                    }
                    if(position == 2 && orderposition == 0 ){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                String d1 = array1[0];
                                String d2 = array2[0];
                                return d1.compareTo(d2);
                            }
                        });
                    }
                    if(orderposition == 1 && position==4){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                Double d1 = Double.valueOf(array1[3]);
                                Double d2 = Double.valueOf(array2[3]);
                                return d1.compareTo(d2);
                            }
                        });}
                    if(position == 3 && orderposition == 1 ){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                Double d1 = Double.valueOf(array1[1]);
                                Double d2 = Double.valueOf(array2[1]);
                                return d1.compareTo(d2);
                            }
                        });
                    }
                    if(position == 2 && orderposition == 1 ){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                String d1 = array1[0];
                                String d2 = array2[0];
                                return d1.compareTo(d2);
                            }
                        });
                    }

                    if(orderposition == 2 && position==4){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                Double d1 = Double.valueOf(array1[3]);
                                Double d2 = Double.valueOf(array2[3]);
                                return -d1.compareTo(d2);
                            }
                        });}
                    if(position == 3 && orderposition == 2 ){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                Double d1 = Double.valueOf(array1[1]);
                                Double d2 = Double.valueOf(array2[1]);
                                return -d1.compareTo(d2);
                            }
                        });
                    }
                    if(position == 2 && orderposition == 2 ){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                String d1 = array1[0];
                                String d2 = array2[0];
                                return -d1.compareTo(d2);
                            }
                        });
                    }

                    for(int j=0;j<count;j++){
                        title[j] = arrForSorting[j][0];
                        price[j] = arrForSorting[j][1];
                        change[j] = arrForSorting[j][2];
                        changeNumber[j] = Double.parseDouble(arrForSorting[j][3]);
                    }
                    mylist = new CustomFavListAdapter(context, title, price, change,changeNumber);
                    listView.setAdapter(mylist);
                    mylist.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        // section Sinnper
        String[] orderList = new String[]{"Order","Ascending","Descending"};

        final List<String> orderList1 = new ArrayList<>(Arrays.asList(orderList));
        final ArrayAdapter<String> spinnerOrderArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,orderList1){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    tv.setTextColor(Color.LTGRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerOrderArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        orderSinnper.setAdapter(spinnerOrderArrayAdapter);
        orderSinnper.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                int sortposition = sortSinnper.getSelectedItemPosition();
                if(position != 0){
                    final Map<String, ?> myentries = SP.getAll();
                    int i =0;
                    arrForSorting = new String[count][4];
                    for (Map.Entry<String, ?> entry : myentries.entrySet()) {
                        String a = entry.getValue().toString();
                        String[] parts = entry.getValue().toString().split("\\$");
                        arrForSorting[i][0] = parts[0];
                        arrForSorting[i][1] = parts[1];
                        arrForSorting[i][2] = parts[2];
                        arrForSorting[i][3] = parts[3];
                        i++;
                    }
                    if(position == 1 && sortposition==4){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                Double d1 = Double.valueOf(array1[3]);
                                Double d2 = Double.valueOf(array2[3]);
                                return d1.compareTo(d2);
                            }
                        });}
                    if(sortposition == 3 && position == 1 ){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                Double d1 = Double.valueOf(array1[1]);
                                Double d2 = Double.valueOf(array2[1]);
                                return d1.compareTo(d2);
                            }
                        });
                    }
                    if(sortposition == 2 && position == 1 ){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                String d1 = array1[0];
                                String d2 = array2[0];
                                return d1.compareTo(d2);
                            }
                        });
                    }

                    if(position == 2 && sortposition==4){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                Double d1 = Double.valueOf(array1[3]);
                                Double d2 = Double.valueOf(array2[3]);
                                return -d1.compareTo(d2);
                            }
                        });}
                    if(sortposition == 3 && position == 2 ){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                Double d1 = Double.valueOf(array1[1]);
                                Double d2 = Double.valueOf(array2[1]);
                                return -d1.compareTo(d2);
                            }
                        });
                    }
                    if(sortposition == 2 && position == 2 ){
                        Arrays.sort(arrForSorting, new Comparator<String[]>() {
                            @Override
                            public int compare(String[] array1, String[] array2) {
                                // get the second element of each array, andtransform it into a Double
                                String d1 = array1[0];
                                String d2 = array2[0];
                                return -d1.compareTo(d2);
                            }
                        });
                    }

                    for(int j=0;j<count;j++){
                        title[j] = arrForSorting[j][0];
                        price[j] = arrForSorting[j][1];
                        change[j] = arrForSorting[j][2];
                        changeNumber[j] = Double.parseDouble(arrForSorting[j][3]);
                    }
                    mylist = new CustomFavListAdapter(context, title, price, change,changeNumber);
                    listView.setAdapter(mylist);
                    mylist.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        Switch mysimpleSwitch = (Switch) findViewById(R.id.switch2);
        final Handler handler = new Handler();
        mysimpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                Log.d("checked", String.valueOf(isChecked));
                thisrefresh = new Runnable() {
                    @Override
                    public void run() {
                        final Map<String, ?> allEntries = SP.getAll();
                        count = SP.getAll().size();
                        for (int i = 0; i < title.length; i++) {
                            String a = SP.getString(title[i], null);
                            String[] parts = a.split("\\$");
                            title[i] = parts[0];
                            price[i] = parts[1];
                            change[i] = parts[2];
                            changeNumber[i] = Double.parseDouble(parts[3]);
                        }
                        mylist = new CustomFavListAdapter(context, title, price, change, changeNumber);
                        listView.setAdapter(mylist);
                        mylist.notifyDataSetChanged();
                        for (int i = 0; i < title.length; i++) {
                            final String url = urlForTableContent + title[i];
                            final String titleOfCompany = title[i];
                            requestQueue = Volley.newRequestQueue(context);
                            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                JSONObject posts = response.getJSONObject("Time Series (Daily)");
                                                Iterator<String> keys = posts.keys();
                                                String str_Name = keys.next();
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
                                                double changePercent = changeNumber / lastDayClosePrice * 100;
                                                changetext = decimalFormat.format(changeNumber) + '(' + decimalFormat.format(changePercent) + "%)";
                                                open = decimalFormat.format(openPrice);
                                                close = decimalFormat.format(lastDayClosePrice);
                                                DayRange = decimalFormat.format(highPrice) + '-' + decimalFormat.format(lowPrice);
                                                volume = volumeNumber + "";
                                                String mynewString = titleOfCompany + "$" + currentPrice + "$" + changetext + "$" + changePercent;
                                                editor.putString(titleOfCompany, mynewString);
                                                editor.apply();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("error", error.toString());
                                        }
                                    });
                            requestQueue.add(jsonArrayRequest);
                        }
                        if(isChecked == true){
                        handler.postDelayed(this, 5000);}
                        else{
                            return;
                        }
                    }

                };
                if (isChecked == true) {
                    handler.postDelayed(thisrefresh, 5000);

                }else{
                    handler.removeCallbacks(thisrefresh);
                }
            }
        });
        ImageButton refreshImageButton = (ImageButton) findViewById(R.id.imageButton4);
        refreshImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                thisProgressBar.setVisibility(View.VISIBLE);
                for (int i = 0; i < title.length; i++) {
                    final String url = urlForTableContent + title[i];
                    final String titleOfCompany = title[i];
                    requestQueue = Volley.newRequestQueue(context);
                    JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONObject posts = response.getJSONObject("Time Series (Daily)");
                                        Iterator<String> keys = posts.keys();
                                        String str_Name = keys.next();
                                        JSONObject thisObject = posts.getJSONObject(str_Name);
                                        double thisprice = thisObject.getDouble("4. close");
                                        double openPrice = thisObject.getDouble("1. open");
                                        double highPrice = thisObject.getDouble("2. high");
                                        double lowPrice = thisObject.getDouble("3. low");
                                        int volumeNumber = thisObject.getInt("5. volume");
                                        currentPrice = decimalFormat.format(thisprice);
                                        String lastDay = keys.next();
                                        JSONObject lastDayObject = posts.getJSONObject(lastDay);
                                        double lastDayClosePrice = lastDayObject.getDouble("4. close");
                                        double changeNum = thisprice - lastDayClosePrice;
                                        double changePercent = changeNum / lastDayClosePrice * 100;
                                        changetext = decimalFormat.format(changeNum) + '(' + decimalFormat.format(changePercent) + "%)";
                                        open = decimalFormat.format(openPrice);
                                        close = decimalFormat.format(lastDayClosePrice);
                                        DayRange = decimalFormat.format(highPrice) + '-' + decimalFormat.format(lowPrice);
                                        volume = volumeNumber + "";
                                        String mynewString = titleOfCompany + "$" + currentPrice + "$" + changetext + "$" + changePercent;
                                        editor.putString(titleOfCompany, mynewString);
                                        editor.apply();
                                        for (int j = 0; j < title.length; j++) {
                                            if(title[j].equals(titleOfCompany)){
                                            price[j] = currentPrice;
                                            change[j] = changetext;
                                            changeNumber[j] = changePercent;}
                                        }
                                        mylist = new CustomFavListAdapter(context, title, price, change, changeNumber);
                                        listView.setAdapter(mylist);
                                        mylist.notifyDataSetChanged();
                                        thisProgressBar.setVisibility(View.GONE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("error", error.toString());
                                }
                            });
                    requestQueue.add(jsonArrayRequest);
                }
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        newAdapterWithHttp adapter = new newAdapterWithHttp (this, android.R.layout.select_dialog_item);
        auto.setThreshold(1);
        adapter.notifyDataSetChanged();
        auto.setAdapter(adapter);
    }






    public class newAdapterWithHttp extends ArrayAdapter<String> implements Filterable {
        public final String thisInputUrl = "http://stockmarket.us-east-2.elasticbeanstalk.com/auto/";
        public ArrayList<String> myArr;
        private RequestQueue mynewrequest;
        private Context thiscontext;

        newAdapterWithHttp (@NonNull Context context, @LayoutRes int counts)
        {
            super (context, counts);
            this.myArr = new ArrayList<>();
            thiscontext = context;
        }
        @Override
        public int getCount()
        {
            if(myArr.size() >5){
                return 5;
            }
            else{
                return myArr.size();}
        }

        @Nullable
        @Override
        public String getItem (int currentindex)
        {
            return myArr.get (currentindex);
        }

        @Override
        public long getItemId(int thisposition) {
            return thisposition;
        }

        public Filter getFilter()
        {
            Filter newFilterReturnResult = new Filter()
            {
                @Override
                protected FilterResults performFiltering (CharSequence inputtext)
                {
                    final Boolean[] finsihedprogram = {false};

                    final FilterResults outputArrayList = new FilterResults();
                    if (inputtext != null){
                        //thisbar.setVisibility(View.VISIBLE);
                        final String url = thisInputUrl + inputtext.toString();
                        mynewrequest = Volley.newRequestQueue(getContext());
                        JsonArrayRequest thisrequest = new JsonArrayRequest(Request.Method.GET, url,null ,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        ArrayList<String> tempArrList = new ArrayList<>();
                                        try {
                                            for(int i = 0; i < response.length(); i++) {
                                                // FilterResults outputArrayList = new FilterResults();
                                                //teacher's code for requesting
                                                StringBuilder names = new StringBuilder();
                                                JSONObject jresponse = response.getJSONObject(i);
                                                String symbol = jresponse.getString("Symbol");
                                                names.append(symbol).append(" - ");
                                                String name = jresponse.getString("Name");
                                                names.append(name).append(" ");
                                                String exchange = jresponse.getString("Exchange");
                                                names.append("(").append(exchange).append(")");
                                                tempArrList.add(names.toString());
                                            }
                                            finsihedprogram[0] = true;
                                            myArr = tempArrList;
                                            //Toast.makeText(mContext,Integer.toString(thisArray.size()), Toast.LENGTH_SHORT).show();
                                            outputArrayList.values = tempArrList;
                                            outputArrayList.count = tempArrList.size();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            finsihedprogram[0] = true;
                                        }
                                        //thisbar.setVisibility(View.GONE); this does not working properliy
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //thisbar.setVisibility(View.GONE);
                                        Log.d("error","can not request, plaze modify data");
                                    }
                                });
                        mynewrequest.add(thisrequest);
                    } else {
                        finsihedprogram[0] = true;
                    }
                    while(finsihedprogram[0] == false){
                    }
                    return outputArrayList;
                }
                @Override
                protected void publishResults (CharSequence inputstringforresult, FilterResults currentresults)
                {
                    if (currentresults != null && currentresults.count > 0)
                    {
                        myArr = (ArrayList<String>) currentresults.values;
                        notifyDataSetChanged();
                    }
                    else notifyDataSetInvalidated();
                }
            };
            return newFilterReturnResult;
        }
    }
}
