package com.example.xuefengyan.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by xuefengyan on 27/11/2017.
 */

public class CustomNewsListAdapter extends ArrayAdapter {
    private final Activity context;

    //to store the animal images
    private final String[] imageIDarray;

    //to store the list of countries
    private final String[] nameArray;

    //to store the list of countries
    private final String[] infoArray;

    public CustomNewsListAdapter(Activity context, String[] nameArrayParam, String[] infoArrayParam, String[] imageIDArrayParam){
        super(context,R.layout.listview_news , nameArrayParam);
        this.context=context;
        this.imageIDarray = imageIDArrayParam;
        this.nameArray = nameArrayParam;
        this.infoArray = infoArrayParam;
    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listview_news, null,true);

        TextView nameTextField = (TextView) rowView.findViewById(R.id.title_section);
        TextView infoTextField = (TextView) rowView.findViewById(R.id.author_section);
        TextView imageView = (TextView) rowView.findViewById(R.id.date_section);

        nameTextField.setText(nameArray[position]);
        infoTextField.setText(infoArray[position]);
        imageView.setText(imageIDarray[position]);

        return rowView;
    };

}
