package com.example.leomorales.genericlistwear;

import android.app.ListActivity;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.StandardCharsets;

import static com.google.android.gms.wearable.DataMap.TAG;

public class MainActivity extends ListActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener {

    private MyAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private static final int REQUEST_RESOLVE_ERROR = 1000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mAdapter = new MyAdapter();
        setListAdapter(this.mAdapter);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    public void add_an_item(View v){
        this.mAdapter.add("New Movie");
        Toast.makeText(this, "Agregar", Toast.LENGTH_SHORT).show();
    }

    /*
     * Callback section
     * BEGIN
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LOGD(TAG, "Google API Client was connected");
        mResolvingError = false;
        Wearable.MessageApi.addListener(mGoogleApiClient, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        LOGD(TAG, "Connection to Google API client was suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!mResolvingError) {

            if (connectionResult.hasResolution()) {
                try {
                    mResolvingError = true;
                    connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
                } catch (IntentSender.SendIntentException e) {
                    // There was an error with the resolution intent. Try again.
                    mGoogleApiClient.connect();
                }
            } else {
                Log.e(TAG, "Connection to Google API client has failed");
                mResolvingError = false;
            }
        }

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        LOGD(TAG, "onMessageReceived() A message from watch was received:"
                + messageEvent.getRequestId() + " " + messageEvent.getPath() + " " + messageEvent.getData().toString());
        String message = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        Toast.makeText(MainActivity.this, "From wear: " + message, Toast.LENGTH_SHORT).show();
        this.mAdapter.add("New Movie from WEAR");
    }
    /*
     * Callback section
     * END
     */

    /**
     * As simple wrapper around Log.d
     */
    private static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }


    /**
     * A simple array adapter that creates a list of movies.
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
