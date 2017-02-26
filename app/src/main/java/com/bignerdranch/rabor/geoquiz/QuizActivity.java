package com.bignerdranch.rabor.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    // constant variables
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_CHEATER = "cheater";
    private static final int REQUEST_CODE_CHEAT = 0;

    // define widget variables
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPrevButton;
    private Button mScoreButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private TextView mCountText;
    private TextView mAttemptsText;

    // array of questions
    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    // instance variables
    private int mCurrentIndex = 0;
    private int correct = 0;
    private int score = 0;
    private int counter = 3;

    // keep track if the cheat button was selected
    private boolean[] mIsCheater = new boolean[mQuestionBank.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        // check to see if something was saved
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        // get reference and set the Question TextView event listener
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        // get reference and set the TRUE Button event listener
        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
                // disable the buttons once an answer was selected
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
            }
        });

        // get reference and set the FALSE Button event listener
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
                // disable the buttons once an answer was selected
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
            }
        });

        // get reference and set the PREVIEW Button event listener
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // enable the buttons
                mTrueButton.setEnabled(true);
                mFalseButton.setEnabled(true);

                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        // get reference and set the NEXT Button event listener
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // enable the buttons
                mTrueButton.setEnabled(true);
                mFalseButton.setEnabled(true);

                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                //mIsCheater = false;
                updateQuestion();
            }
        });

        // get reference and set the SCORE Button event listener
        mScoreButton = (Button) findViewById(R.id.score_button);
        mScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // display the score as a percentage
                score =  (correct * 100) / mQuestionBank.length;
                Toast.makeText(getApplicationContext(), String.valueOf(score) + "%", Toast.LENGTH_SHORT)
                        .show();

            }
        });

        // get reference to the Count TextView
        mCountText = (TextView)findViewById(R.id.countText);
        mAttemptsText = (TextView) findViewById(R.id.attemptsText);
        mCountText.setVisibility(View.GONE);    // do not display the text
        mAttemptsText.setVisibility(View.GONE); // do not display the text

        // get reference and set the CHEAT Button event listener
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                // open the CheatActivity when the Cheat button is selected
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);

                // display the countdown
                mAttemptsText.setVisibility(View.VISIBLE);
                mCountText.setVisibility(View.VISIBLE);
                mCountText.setTextColor(Color.RED);
                counter--;  // count down
                mCountText.setText(Integer.toString(counter));  // display the count

                // if too many cheat tries then disable the CHEAT Button
                if (counter == 0) {
                    mCheatButton.setEnabled(false);
                    Toast.makeText(getApplicationContext(),
                            "Too many cheating attempts.  Try again in 3 seconds.",
                            Toast.LENGTH_LONG).show();
                    counter = 3;        // reset the counter
                    // wait 3 secconds and then enable the Cheat button
                    mCheatButton.getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCheatButton.setEnabled(true);
                        }
                    }, 30000);
                } // end counter check

            }
        });

        // check to see if something was saved
        if(savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBooleanArray(KEY_CHEATER);
        }

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_CODE_CHEAT) {
            if(data == null) {
                return;
            }

            mIsCheater[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    // values are saved
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBooleanArray(KEY_CHEATER, mIsCheater);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (mIsCheater[mCurrentIndex]) {
            messageResId = R.string.judgement_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                correct++;  // keep track of correct answers
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
                .show();

    }
}
