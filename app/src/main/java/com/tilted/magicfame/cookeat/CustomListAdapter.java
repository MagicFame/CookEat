package com.tilted.magicfame.cookeat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomListAdapter extends ArrayAdapter<Recipe> {
    private Activity activity;
    public CustomListAdapter(Activity activity, int resource, List<Recipe> r){
        super(activity, resource, r);
        this.activity = activity;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.item_listview, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }

        Recipe recipe = getItem(position);

        holder.name.setText(recipe.getName());
        holder.id.setText("Calories: " + Float.parseFloat(recipe.getCalories()));

        Picasso.with(activity).load(recipe.getImageURL()).into(holder.image);

        return convertView;
    }

    private static class ViewHolder {
        private TextView name;
        private TextView id;
        private ImageView image;

        public ViewHolder(View v) {
            name = (TextView) v.findViewById(R.id.title);
            image = (ImageView) v.findViewById(R.id.thumbnail);
            id = (TextView) v.findViewById(R.id.id);
        }
    }
}
