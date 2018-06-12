package com.example.xuefengyan.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by xuefengyan on 27/11/2017.
 */

public class CustomFavListAdapter extends ArrayAdapter {
    private final Activity context;

    //to store the titles
    private final String[] titlearray;

    //to store the prices array
    private final String[] priceArray;

    //to store the currenr change
    private final String[] changeNumberArray;

    //change in double
    private final Double[] changeArray;

    public CustomFavListAdapter(Activity context, String[] nameArrayParam, String[] infoArrayParam, String[] imageIDArrayParam,Double[] changeArray){
        super(context,R.layout.listview_news , nameArrayParam);
        this.context=context;
        this.titlearray = nameArrayParam;
        this.priceArray = infoArrayParam;
        this.changeNumberArray = imageIDArrayParam;
        this.changeArray = changeArray;
    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listview_fav, null,true);

        TextView titleTextField = (TextView) rowView.findViewById(R.id.labelName);
        TextView priceTextField = (TextView) rowView.findViewById(R.id.priceSection);
        TextView changeTextField = (TextView) rowView.findViewById(R.id.changeSection);
        if(changeArray[position]>0){
            changeTextField.setTextColor(context.getResources().getColor(R.color.green));}
        else{
            changeTextField.setTextColor(context.getResources().getColor(R.color.red));
        }
        titleTextField.setText(titlearray[position]);
        priceTextField.setText(priceArray[position]);
        changeTextField.setText(changeNumberArray[position]);

        return rowView;
    };

}
