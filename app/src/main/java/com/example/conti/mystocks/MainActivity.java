package com.example.conti.mystocks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements DownloadCallback
{
    // Reference to the TextView showing fetched data, so we can clear it with a button as necessary.
    private TextView mReceivedData;

    // Keep a reference to the NetworkFragment which owns the AsyncTask object
    private NetworkFragment mNetworkFragment;

    // flag indicating if a download is in progress
    private boolean mDownloading = false;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory.
     */
    private MyPagerAdapter mMyPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mMyPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mMyPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mReceivedData = new TextView(getApplicationContext());
        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "http://finance.yahoo.com"); // "https://www.google.com");
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_refresh) {
            Toast.makeText(getApplicationContext(), "Refresh", Toast.LENGTH_SHORT).show();
            if (!mDownloading && mNetworkFragment != null) {
                // Execute the async download.
                mNetworkFragment.startDownload();
                mDownloading = true;
            }
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateFromDownload(String result) {
        if (result != null) {
            mReceivedData.setText(result);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        } else {
            mReceivedData.setText("### Connection Error ###"); //getString(R.string.connection_error));
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:
                break;
            case Progress.CONNECT_SUCCESS:
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                mReceivedData.setText("" + percentComplete + "%");
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                break;
        }
    }


    /**
     * A fragment containing a simple view.
     */
    public static class MyPageFragment extends Fragment
    {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public MyPageFragment ()
        {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MyPageFragment newInstance (int sectionNumber)
        {
            MyPageFragment fragment = new MyPageFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            int pagenumber = getArguments().getInt(ARG_SECTION_NUMBER);
            if (pagenumber == 1)
            {
                TextView textView = rootView.findViewById(R.id.section_label);
                textView.setText("Watchlist");

                String [] saAktienliste = {
                        "Adidas - Kurs: 73,45 €",
                        "Allianz - Kurs: 145,12 €",
                        "BASF - Kurs: 84,27 €",
                        "Bayer - Kurs: 128,60 €",
                        "Beiersdorf - Kurs: 80,55 €",
                        "BMW St. - Kurs: 104,11 €",
                        "Commerzbank - Kurs: 12,47 €",
                        "Continental - Kurs: 209,94 €",
                        "Continental - Kurs: 209,94 €",
                        "Continental - Kurs: 209,94 €",
                        "Continental - Kurs: 209,94 €",
                        "Continental - Kurs: 209,94 €",
                        "Daimler - Kurs: 84,33 €"
                };

                List <String> stockList = new ArrayList<>(Arrays.asList(saAktienliste));
                ArrayAdapter <String> stockListAdapter = new ArrayAdapter<>(
                                getActivity(), // context
                                R.layout.list_item_stock, // ID xml-layout file
                                R.id.textview_list_item_stock, // ID of TextView
                                stockList);

                ListView listViewStocks = rootView.findViewById(R.id.section_listview);
                listViewStocks.setAdapter(stockListAdapter);
            }
            else if (pagenumber == 2)
            {
                TextView textView = rootView.findViewById(R.id.section_label);
                textView.setText("Depot");
//                textView.setText(mDataText.getText());
            }
            else
            {
                TextView textView = rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    class MyPagerAdapter extends FragmentPagerAdapter
    {

        MyPagerAdapter (FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem (int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            return MyPageFragment.newInstance(position + 1);
        }

        @Override
        public int getCount ()
        {
            // Show 3 total pages.
            return 3;
        }
    }
}
