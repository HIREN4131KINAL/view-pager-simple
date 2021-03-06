package com.example.viewpagerdemo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    ViewPager viewPager;
    MyPagerAdapter myPagerAdapter;
    String Response;
    List<String> imageList = new ArrayList<String>();
    List<String> title = new ArrayList<String>();

    public static int NumberOfPages ;
    DisplayTimer displayTimer;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.myviewpager);
        new FetchImage().execute();

    }

    private class MyPagerAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return NumberOfPages;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            int[] androidColors = getResources().getIntArray(R.array.androidcolors);
            int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];

            TextView textView = new TextView(MainActivity.this);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(20);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
//            textView.setText(String.valueOf(position));
            textView.setText(title.get(position));

            ImageView imageView = new ImageView(MainActivity.this);
            Picasso.with(MainActivity.this).load(imageList.get(position)).fit().into(imageView);
//            imageView.setImageResource(res[position]);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(imageParams);

            LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            layout.setBackgroundColor(randomAndroidColor);
            layout.setLayoutParams(layoutParams);
            layout.addView(textView);
            layout.addView(imageView);

            final int page = position;
            layout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this,
                            "Page " + page + " clicked",
                            Toast.LENGTH_LONG).show();
                }
            });

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    public class FetchImage extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            JSONParser jsonParser = new JSONParser();
            Response = jsonParser.makeServiceCall("http://api.androidhive.info/json/movies.json");
            Log.d("", "doInBackground: " + Response);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {

                JSONArray jsonArray = new JSONArray(Response);
                NumberOfPages=jsonArray.length();
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String image = jsonObject.getString("image");
                    imageList.add(image);
                   String name =  jsonObject.getString("title");
                    title.add(name);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            myPagerAdapter = new MyPagerAdapter();
            viewPager.setAdapter(myPagerAdapter);
            viewPager.setPageTransformer(true ,new ZoomOutPageTransformer());

            displayTimer = new DisplayTimer(5000, 1000);
            displayTimer.start();

        }
    }


    public class DisplayTimer extends CountDownTimer {


        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public DisplayTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {

            if(position == NumberOfPages){

                viewPager.setAdapter(myPagerAdapter);
                position = 0;
                viewPager.setCurrentItem(position, true);
                position++;
                displayTimer = new DisplayTimer(5000, 2000);
                displayTimer.start();

            }else {

                viewPager.setCurrentItem(position, true);
                position++;
                displayTimer = new DisplayTimer(5000, 1000);
                displayTimer.start();

            }

        }
    }

}
