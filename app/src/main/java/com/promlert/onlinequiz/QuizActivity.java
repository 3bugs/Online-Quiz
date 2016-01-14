package com.promlert.onlinequiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.promlert.onlinequiz.model.Question;
import com.promlert.onlinequiz.model.ResponseStatus;
import com.promlert.onlinequiz.net.WebServices;

import java.io.IOException;
import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = QuizActivity.class.getSimpleName();

    private QuestionsPagerAdapter mAdapter;
    private TabLayout mTabLayout;
    private ProgressBar mProgressBar;
    private ViewPager mViewPager;
    private FloatingActionButton mFab;

    private int mQuizId;
    protected ArrayList<Question> mQuestionArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/

        Intent intent = getIntent();
        mQuizId = intent.getIntExtra("quiz_id", 0);

        setupViews();
        loadQuestions();
    }

    private void setupViews() {
        mAdapter = new QuestionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isQuizComplete()) {

                } else {
                    Snackbar.make(
                            findViewById(R.id.main_content),
                            "คุณยังทำแบบทดสอบไม่ครบทุกข้อ",
                            Snackbar.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    private void loadQuestions() {
        mProgressBar.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);

        WebServices.getQuestions(mQuizId, new WebServices.GetQuestionsCallback() {
            @Override
            public void onFailure(IOException e) {
                mProgressBar.setVisibility(View.GONE);
                mViewPager.setVisibility(View.GONE);

                String msg = "Network Connection Error:\n" + e.getMessage();
                //TextView errorMessageTextView = (TextView) findViewById(R.id.error_message);
                //errorMessageTextView.setText(msg);
                Log.e(TAG, msg);
                Toast.makeText(QuizActivity.this, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(ResponseStatus responseStatus, ArrayList<Question> questionArrayList) {
                if (responseStatus.success) {
                    mProgressBar.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.VISIBLE);

                    //Collections.copy(mQuestionArrayList, questionArrayList);
                    mQuestionArrayList.addAll(questionArrayList);
                    mAdapter.notifyDataSetChanged();
                    mTabLayout.setupWithViewPager(mViewPager);

                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.GONE);

                    //TextView errorMessageTextView = (TextView) findViewById(R.id.error_message);
                    //errorMessageTextView.setText(responseStatus.message);
                    Log.e(TAG, responseStatus.message);
                    Toast.makeText(QuizActivity.this, responseStatus.message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void checkQuizComplete() {
        if (isQuizComplete()) {
            mFab.setVisibility(View.VISIBLE);
        } else {
            mFab.setVisibility(View.GONE);
        }
    }

    private boolean isQuizComplete() {
        for (Question question : mQuestionArrayList) {
            if (question.getSelectedChoiceId() == Question.NO_CHOICE_SELECTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class QuestionsPagerAdapter extends FragmentPagerAdapter {

        public QuestionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return QuestionFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return mQuestionArrayList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.valueOf(position + 1);
        }
    }
}
