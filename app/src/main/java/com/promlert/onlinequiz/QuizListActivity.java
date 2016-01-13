package com.promlert.onlinequiz;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.promlert.onlinequiz.model.Quiz;
import com.promlert.onlinequiz.model.ResponseStatus;
import com.promlert.onlinequiz.net.WebServices;

import java.io.IOException;
import java.util.ArrayList;

public class QuizListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = QuizListActivity.class.getSimpleName();

    private ArrayList<Quiz> mQuizArrayList = null;
    private ListView mQuizzesListView;
    private ProgressBar mProgressBar;
    private View mRetryLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mQuizzesListView = (ListView) findViewById(R.id.quizzes_list_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRetryLayout = findViewById(R.id.retry_layout);

        Button retryButton = (Button) findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadQuizzes();
            }
        });

        loadQuizzes();
    }

    private void loadQuizzes() {
        mProgressBar.setVisibility(View.VISIBLE);
        mQuizzesListView.setVisibility(View.GONE);
        mRetryLayout.setVisibility(View.GONE);

        WebServices.getQuizzes(new WebServices.GetQuizzesCallback() {
            @Override
            public void onFailure(IOException e) {
                mProgressBar.setVisibility(View.GONE);
                mQuizzesListView.setVisibility(View.GONE);
                mRetryLayout.setVisibility(View.VISIBLE);

                String msg = "Network Connection Error:\n" + e.getMessage();
                TextView errorMessageTextView = (TextView) findViewById(R.id.error_message);
                errorMessageTextView.setText(msg);
                Log.e(TAG, msg);
                Toast.makeText(QuizListActivity.this, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(ResponseStatus responseStatus, ArrayList<Quiz> quizArrayList) {
                if (responseStatus.success) {
                    mProgressBar.setVisibility(View.GONE);
                    mQuizzesListView.setVisibility(View.VISIBLE);
                    mRetryLayout.setVisibility(View.GONE);

                    Log.i(TAG, "Total quizzes: " + quizArrayList.size());

                    mQuizArrayList = quizArrayList;

                    ArrayAdapter<Quiz> adapter = new ArrayAdapter<Quiz>(
                            QuizListActivity.this,
                            android.R.layout.simple_list_item_1,
                            mQuizArrayList
                    );
                    mQuizzesListView.setAdapter(adapter);
                    mQuizzesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(
                                    QuizListActivity.this,
                                    mQuizArrayList.get(position).toString(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mQuizzesListView.setVisibility(View.GONE);
                    mRetryLayout.setVisibility(View.VISIBLE);

                    TextView errorMessageTextView = (TextView) findViewById(R.id.error_message);
                    errorMessageTextView.setText(responseStatus.message);
                    Log.e(TAG, responseStatus.message);
                    Toast.makeText(QuizListActivity.this, responseStatus.message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quiz_list, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
