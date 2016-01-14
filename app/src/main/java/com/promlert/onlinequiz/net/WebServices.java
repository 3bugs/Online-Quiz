package com.promlert.onlinequiz.net;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.promlert.onlinequiz.model.Choice;
import com.promlert.onlinequiz.model.Question;
import com.promlert.onlinequiz.model.Quiz;
import com.promlert.onlinequiz.model.ResponseStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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

    private static ResponseStatus mResponseStatus;
    private static ArrayList<Quiz> mQuizArrayList;
    private static ArrayList<Question> mQuestionArrayList;

    public interface GetQuizzesCallback {
        void onFailure(IOException e);
        void onResponse(ResponseStatus responseStatus, ArrayList<Quiz> quizArrayList);
    }

    public interface GetQuestionsCallback {
        void onFailure(IOException e);
        void onResponse(ResponseStatus responseStatus, ArrayList<Question> questionArrayList);
    }

    public static void getQuizzes(final GetQuizzesCallback callback) {
        Request request = new Request.Builder()
                .url(GET_QUIZZES_URL)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                new Handler(Looper.getMainLooper()).post(
                        new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(e);
                            }
                        }
                );
            }

            @Override
            public void onResponse(Response response) throws IOException {
                delay(1);

                final String jsonResult = response.body().string();
                Log.d(TAG, jsonResult);

                try {
                    JSONObject jsonObject = new JSONObject(jsonResult);
                    int success = jsonObject.getInt("success");

                    if (success == 1) {
                        mResponseStatus = new ResponseStatus(true, null);
                        mQuizArrayList = new ArrayList<>();

                        parseJsonQuizData(jsonObject.getJSONArray("quiz_data"));
                    } else if (success == 0) {
                        mResponseStatus = new ResponseStatus(false, jsonObject.getString("message"));
                        mQuizArrayList = null;
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON.");
                    e.printStackTrace();
                }

                new Handler(Looper.getMainLooper()).post(
                        new Runnable() {
                            @Override
                            public void run() {
                                callback.onResponse(mResponseStatus, mQuizArrayList);
                            }
                        }
                );
            }
        });
    }

    public static void getQuestions(int quizId, final GetQuestionsCallback callback) {
        Request request = new Request.Builder()
                .url(String.format(GET_QUESTIONS_URL, quizId))
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                new Handler(Looper.getMainLooper()).post(
                        new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(e);
                            }
                        }
                );
            }

            @Override
            public void onResponse(Response response) throws IOException {
                delay(1);

                final String jsonResult = response.body().string();
                Log.d(TAG, jsonResult);

                try {
                    JSONObject jsonObject = new JSONObject(jsonResult);
                    int success = jsonObject.getInt("success");

                    if (success == 1) {
                        mResponseStatus = new ResponseStatus(true, null);
                        mQuestionArrayList = new ArrayList<>();

                        parseJsonQuestionData(
                                jsonObject.getJSONArray("question_data"),
                                jsonObject.getInt("quiz_id")
                        );
                    } else if (success == 0) {
                        mResponseStatus = new ResponseStatus(false, jsonObject.getString("message"));
                        mQuestionArrayList = null;
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON.");
                    e.printStackTrace();
                }

                new Handler(Looper.getMainLooper()).post(
                        new Runnable() {
                            @Override
                            public void run() {
                                callback.onResponse(mResponseStatus, mQuestionArrayList);
                            }
                        }
                );
            }
        });
    }

    private static void delay(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void parseJsonQuizData(JSONArray jsonArrayQuizData) throws JSONException {
        for (int i = 0; i < jsonArrayQuizData.length(); i++) {
            JSONObject jsonQuiz = jsonArrayQuizData.getJSONObject(i);

            Quiz quiz = new Quiz(
                    jsonQuiz.getInt("quiz_id"),
                    jsonQuiz.getString("title"),
                    jsonQuiz.getString("detail"),
                    jsonQuiz.getInt("number_of_questions")
            );
            mQuizArrayList.add(quiz);
        }
    }

    private static void parseJsonQuestionData(JSONArray jsonArrayQuestionData, int quizId) throws JSONException {
        for (int i = 0; i < jsonArrayQuestionData.length(); i++) {
            JSONObject jsonQuestion = jsonArrayQuestionData.getJSONObject(i);

            String pictureFilename = null;
            if (!jsonQuestion.isNull("picture")) {
                pictureFilename = IMAGES_BASE_URL
                        + String.valueOf(quizId).trim()
                        + "/"
                        + jsonQuestion.getString("picture");
            }

            Question question = new Question(
                    jsonQuestion.getInt("question_id"),
                    jsonQuestion.getString("title"),
                    jsonQuestion.getString("detail").replace("\\n", "\n"),
                    pictureFilename
            );

            JSONArray jsonArrayChoiceData = jsonQuestion.getJSONArray("choice_data");
            for (int j = 0; j < jsonArrayChoiceData.length(); j++) {
                JSONObject jsonChoice = jsonArrayChoiceData.getJSONObject(j);

                Choice choice = new Choice(
                        jsonChoice.getInt("choice_id"),
                        jsonChoice.getString("text"),
                        jsonChoice.getBoolean("is_answer")
                );
                question.choiceArrayList.add(choice);
            }

            mQuestionArrayList.add(question);
        }
    }
}
