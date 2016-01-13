package com.promlert.onlinequiz.net;

import android.util.Log;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Promlert on 1/13/2016.
 */
public class WebServices {

    private static final String TAG = WebServices.class.getSimpleName();

    // AVD: 10.0.2.2, Genymotion: 10.0.3.2
    private static final String BASE_URL = "http://10.0.3.2/online_quiz/";
    private static final String IMAGES_BASE_URL = BASE_URL + "images/";

    private static final String GET_QUIZZES_URL = BASE_URL + "quiz_index.php";
    private static final String GET_QUESTIONS_URL = BASE_URL + "get_questions.php?quiz_id=%d";

    private static final OkHttpClient mClient = new OkHttpClient();

    public static void getQuizzes() {
        Request request = new Request.Builder()
                .url(GET_QUIZZES_URL)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Network connection failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String jsonResult = response.body().string();
                Log.d(TAG, jsonResult);
            }
        });
    }
}
