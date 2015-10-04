package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {

    private  TwitterClient client;

    private  TweetsArrayAdaptor aTweets;

    private ArrayList<Tweet> tweets;

    private ListView lvTweets;

    private final int REQUEST_CODE = 25;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        getSupportActionBar().setTitle(R.string.home);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_action_tweet);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Find List View

        lvTweets= (ListView) findViewById(R.id.lvTweets);

        // Create the arraylist (data source)

        tweets=new ArrayList<>();

        // Construct the adaptor from data sources

        aTweets=new TweetsArrayAdaptor(this,tweets);

       // Connect Adapter to List view

        lvTweets.setAdapter(aTweets);


        client=TwitterApplication.getRestClient();// Singleton

        populateTimeline(client.DEFAULT_MAX_ID,true);

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int max_id, int totalItemsCount) {

                //  Toast.makeText(this,"onLoadMore" , Toast.LENGTH_SHORT).show();
                Toast.makeText(getBaseContext(), "EndlessScrollListener Test", Toast.LENGTH_SHORT).show();

                customLoadMoreDataFromApi(max_id);

                return true;
            }
        });
        
    }

    private void customLoadMoreDataFromApi(int max_id) {

        //Toast.makeText(this, "scroll", Toast.LENGTH_SHORT).show();

        //requestImages(page * 8, false);

        populateTimeline(getMax_id(),false);

        Log.i("DEBUG", "page====>" + getMax_id());


    }

    private String getMax_id(){
        String max_id=client.DEFAULT_MAX_ID;


        if(tweets!=null){

            int lastTweet = tweets.size() - 1;
            max_id = String.valueOf(tweets.get(lastTweet).getuId());



        }

        Log.d("DEBUG","max_id-------->"+max_id);


        return max_id;

    }
    // Send an API request to get timeline Json
    //Fill the Listview by creating the tweet objects from json


    private void populateTimeline(String max_id,final Boolean clearResults) {

        Log.d("DEBUG", "populateTimeline-----max_id-->"+ max_id +"clearResults>"+clearResults);

        client.getTimeline( max_id,new JsonHttpResponseHandler() {


            // Success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {

                Log.d("DEBUG", "onSuccess----->"+json.toString());

                //JSON here
                //deSerialize JSON
                //Create Models and add them to adaptor
                //Load Models into ListView

                ArrayList<Tweet> tweets = Tweet.fromJSONArray(json);

                if (clearResults) {
                    // Clear out the old data, because this is a different search.


                   aTweets.clear();
                }


                aTweets.addAll(tweets);

              //  aTweets.notifyDataSetChanged();


            }


            //Failure


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                Log.d("DEBUG", "errorResponse--->"+errorResponse.toString());
                Log.d("DEBUG","onFailure------statusCode>>>>>>>"+statusCode);


            }



 });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void compostTweet(MenuItem item) {

        Toast.makeText(this, "Setting CLICKED", Toast.LENGTH_SHORT).show();

        // create an intent

        Intent intent = new Intent(this, ComposeTweetActivity.class);
        //start the new Activity

        // startActivity(intent);

        startActivityForResult(intent, REQUEST_CODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (RESULT_OK == resultCode && requestCode == REQUEST_CODE) {


            Toast.makeText(getBaseContext(),"Tweet Posted", Toast.LENGTH_SHORT).show();

            populateTimeline(client.DEFAULT_MAX_ID, true);

        }

    }



}
