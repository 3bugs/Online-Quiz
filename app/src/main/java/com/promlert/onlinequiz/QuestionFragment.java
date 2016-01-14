package com.promlert.onlinequiz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.promlert.onlinequiz.model.Choice;
import com.promlert.onlinequiz.model.Question;

import java.util.ArrayList;

/**
 * Created by Promlert on 1/14/2016.
 */
public class QuestionFragment extends Fragment {

    private static final String TAG = QuestionFragment.class.getSimpleName();

    private static final String ARG_QUESTION_ITEM_POSITION = "question_item_position";

    private TextView mQuestionTitleTextView;
    private ImageView mQuestionPictureImageView;
    private TextView mQuestionDetailTextView;
    private RadioGroup mChoicesRadioGroup;

    private int mQuestionItemPosition;

    public QuestionFragment() {
    }

    public static QuestionFragment newInstance(int questionItemPosition) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_QUESTION_ITEM_POSITION, questionItemPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mQuestionItemPosition = args.getInt(ARG_QUESTION_ITEM_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_question, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mQuestionTitleTextView = (TextView) view.findViewById(R.id.question_title_text_view);
        mQuestionPictureImageView = (ImageView) view.findViewById(R.id.question_picture_image_view);
        mQuestionDetailTextView = (TextView) view.findViewById(R.id.question_detail_text_view);
        mChoicesRadioGroup = (RadioGroup) view.findViewById(R.id.choices_radio_group);

        ArrayList<Question> questionArrayList = ((QuizActivity) getActivity()).mQuestionArrayList;
        final Question question = questionArrayList.get(mQuestionItemPosition);

        mQuestionTitleTextView.setText(question.title);
        mQuestionDetailTextView.setText(question.detail);

        if (question.picture == null) {
            mQuestionPictureImageView.setVisibility(View.GONE);
        } else {
            mQuestionPictureImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(question.picture).into(mQuestionPictureImageView);
        }

        LayoutInflater inflater = getLayoutInflater(null);

        for (Choice choice : question.choiceArrayList) {
            RadioButton choiceRadioButton = (RadioButton)
                    inflater.inflate(R.layout.choice_button, mChoicesRadioGroup, false);

            choiceRadioButton.setId(choice.choiceId);
            choiceRadioButton.setText(choice.text);

            mChoicesRadioGroup.addView(choiceRadioButton);
        }

        mChoicesRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Toast.makeText(getActivity(), "Choice ID: " + checkedId, Toast.LENGTH_SHORT).show();

                question.setSelectedChoiceId(checkedId);
                ((QuizActivity) getActivity()).checkQuizComplete();
            }
        });
    }
}
