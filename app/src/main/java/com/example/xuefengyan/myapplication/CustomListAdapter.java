package com.example.xuefengyan.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by xuefengyan on 26/11/2017.
 */

public class CustomListAdapter extends ArrayAdapter {
    private final Activity context;
    private final Integer[] imagePostion;
    private final String[] nameString;
    private final String[] currentnumber;

    public CustomListAdapter(Activity context, String[] names, String[] texts, Integer[] poisitionForImage){

        super(context,R.layout.listview_row , names);
        this.context=context;
        this.imagePostion = poisitionForImage;
        this.nameString = names;
        this.currentnumber = texts;
    }
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();

        View rowView=inflater.inflate(R.layout.listview_row, null,true);

        TextView nameTextField = (TextView) rowView.findViewById(R.id.firstText);
        TextView infoTextField = (TextView) rowView.findViewById(R.id.secondText);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imagesection);

        nameTextField.setText(nameString[position]);
        infoTextField.setText(currentnumber[position]);
        imageView.setImageResource(imagePostion[position]);

        return rowView;
    };
}
