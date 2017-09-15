package com.example.leomorales.genericlistwear;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
    private MyAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mAdapter = new MyAdapter();
        setListAdapter(this.mAdapter);
    }

    public void add_an_item(View v){
        this.mAdapter.add("New Movie");
        Toast.makeText(this, "Agregar", Toast.LENGTH_SHORT).show();
    }

    /**
     * A simple array adapter that creates a list of cheeses.
     */
    private class MyAdapter extends BaseAdapter {

        public MyAdapter() {
            for(String s : Movies.MOVIES_DB)
                Movies.MOVIES.add(s);
        }

        @Override
        public int getCount() {
            return Movies.MOVIES.size();
        }

        @Override
        public String getItem(int position) {

            return Movies.MOVIES.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Movies.MOVIES.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, container, false);
            }

            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(getItem(position));
            return convertView;
        }

        public void add(String movie){
            Movies.MOVIES.add(movie);
            this.notifyDataSetChanged();
        }
    }
}
