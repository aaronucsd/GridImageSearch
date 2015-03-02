package com.codepath.gridimagesearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.codepath.gridimagesearch.R;
import com.codepath.gridimagesearch.adapters.ImageResultsAdapter;
import com.codepath.gridimagesearch.listeners.EndlessScrollListener;
import com.codepath.gridimagesearch.models.ImageResult;
import com.codepath.gridimagesearch.models.SettingsResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity implements SettingsFragment.SettingsFragmentListener {

    private EditText etQuery;
    private GridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    private SettingsResult settingsResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Putting back ActionBar icon - Displaying ActionBar Icon
        //http://guides.codepath.com/android/Defining-The-ActionBar#displaying-actionbar-icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_search);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        setupViews();
        //1. init arrayList - creates the data source
        imageResults = new ArrayList<ImageResult>();
        //2. adapter - Attaches the date source (above) to an adapter
        aImageResults = new ImageResultsAdapter(this, imageResults);
        //3. Link the adapter to the adapterview (gridview)
        gvResults.setAdapter(aImageResults);

        //infinite scroll
        GridView lvItems = (GridView) findViewById(R.id.gvResults);
        // Attach the listener to the AdapterView onCreate
        lvItems.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });

    }

    private void setupViews(){
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);

        //setup the passing from parent to child activity (intent) onclick listener
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch the image display activity

                // Create an intent
                Intent childView = new Intent(SearchActivity.this, ImageDisplayActivity.class);//routes parent=>child

                // Get the image result to display
                ImageResult result = imageResults.get(position);//get at that position clicked

                // Pass image result into the intent
                childView.putExtra("result", result);//pass result object to the child (Parcelable - package for transfer)

                //Launch the child activity
                startActivity(childView);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    // Fired whenever the button is pressed (android:onclic property)
    public void onImageSearch(View v){
        //Grab hold of the query from search input
        String query = etQuery.getText().toString();
        //Toast.makeText(this, "Search for: "+query, Toast.LENGTH_SHORT).show();

        //Make connection to get JSON data from api
        AsyncHttpClient client = new AsyncHttpClient();
        /*https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8&q=android
         Size - imgsz=icon (small, medium,large, xlarge, )
         Color filter - imgcolor=black ((black, blue, brown, gray, green, etc...))
         Type = imgtype=(face, photo, clipart, lineart)
         Site - as_sitesearch=photobucket.com
        */
        String _size = "";
        String _color = "";
        String _type = "";
        String _site = "";

        if(this.settingsResult != null) {
            if(settingsResult.size != null && !settingsResult.size.isEmpty() && settingsResult.size != "any"){
                //Toast.makeText(this, "settingsResult.size: " + settingsResult.size, Toast.LENGTH_SHORT).show();
                _size = "&imgsz="+settingsResult.size;
            }
            if(settingsResult.color != null && !settingsResult.color.isEmpty() && settingsResult.color != "any"){
                _color = "&imgcolor="+settingsResult.color;
            }
            if(settingsResult.type != null && !settingsResult.type.isEmpty() && settingsResult.type != "any"){
                _type = "&imgtype="+settingsResult.type;
            }
            if(settingsResult.site != null && !settingsResult.site.isEmpty()){
                //Toast.makeText(this, "settingsResult.site: " + settingsResult.site, Toast.LENGTH_SHORT).show();
                _site = "&as_sitesearch="+settingsResult.site;
            }
        }

        String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8"+
                _size+
                _color+
                _type+
                _site+
                "&q="+query;
        Log.d("INFO", searchUrl);
        //due to async call we will get the callback on success for each
        client.get(searchUrl, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                //parse the data from the JSON data
                JSONArray imageResultJson = null;
                try {
                    imageResultJson = response.getJSONObject("responseData").getJSONArray("results");
                    //imageResults.clear();// clear the existing images from the arrayList (case new search)
                    // note for pagination/infinite scroll later
                    // Now populate the arrayList
                    //imageResults.addAll(ImageResult.fromJSONArray(imageResultJson));

                    //notify the adapter there is a change in the arrayList (data source)
                    // Make changes to the adapater it will modify the data source (arrayList) - they are linked
                    //replaces the lines above for arraylist directly, and use adapter's version of those methods.
                    aImageResults.clear();
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultJson));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("INFO", imageResults.toString());
            }
        });
    }

    /*
 * http://guides.codepath.com/android/Using-DialogFragment
 * http://developer.android.com/reference/android/app/DialogFragment.html#BasicDialog
 * */
    private void showEditDialog(String value) {
        FragmentManager fm = getSupportFragmentManager();
        SettingsFragment settingsDialog = SettingsFragment.newInstance("Some Title");
        //editNameDialog.setTextValue(value);
        settingsDialog.show(fm, "fragment_settings");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            showEditDialog("show");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishEditDialog(SettingsResult settingsResult) {
        this.settingsResult = settingsResult;
    }

    // Append more data into the adapter - for infinitescroll
    public void customLoadMoreDataFromApi(int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
    }
}
