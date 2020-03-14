package com.example.week2flashcard;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.plattysoft.leonids.ParticleSystem;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    boolean isShowingAnswers=true;

    public int getRandomNumber(int minNumber, int maxNumber) {
        Random rand = new Random();
        return rand.nextInt((maxNumber - minNumber) + 1) + minNumber;
    }

    void DisplayScreen () {
        //Reset color
        ((TextView) findViewById(R.id.txtAnswerChoice1)).setBackgroundColor(getResources().getColor(R.color.colorPeach));
        ((TextView) findViewById(R.id.txtAnswerChoice2)).setBackgroundColor(getResources().getColor(R.color.colorPeach));
        ((TextView) findViewById(R.id.txtAnswerChoice3)).setBackgroundColor(getResources().getColor(R.color.colorPeach));
        ((TextView) findViewById(R.id.txtQuestion)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.txtAnswerCorrect)).setVisibility(View.INVISIBLE);

        //Setup Screen
        final Animation leftOutAnim = AnimationUtils.loadAnimation(this, R.anim.left_out);
        final Animation rightInAnim = AnimationUtils.loadAnimation(this, R.anim.right_in);
        leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // this method is called when the animation first starts
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // this method is called when the animation is finished playing
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // we don't need to worry about this method
            }
        });

        if (allFlashcards != null && allFlashcards.size() > 0) {
            findViewById(R.id.txtQuestion).startAnimation(leftOutAnim);
            ((TextView) findViewById(R.id.txtQuestion)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
            findViewById(R.id.txtQuestion).startAnimation(rightInAnim);
            ((TextView) findViewById(R.id.txtAnswerCorrect)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());

            int AnswerIndex = getRandomNumber(1,3);
            ((TextView) findViewById(R.id.txtQuestion)).setTag(AnswerIndex);
            switch (AnswerIndex) {
                case 1:
                    ((TextView) findViewById(R.id.txtAnswerChoice1)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());
                    ((TextView) findViewById(R.id.txtAnswerChoice2)).setText(allFlashcards.get(currentCardDisplayedIndex).getWrongAnswer1());
                    ((TextView) findViewById(R.id.txtAnswerChoice3)).setText(allFlashcards.get(currentCardDisplayedIndex).getWrongAnswer2());
                    break;
                case 2:
                    ((TextView) findViewById(R.id.txtAnswerChoice1)).setText(allFlashcards.get(currentCardDisplayedIndex).getWrongAnswer1());
                    ((TextView) findViewById(R.id.txtAnswerChoice2)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());
                    ((TextView) findViewById(R.id.txtAnswerChoice3)).setText(allFlashcards.get(currentCardDisplayedIndex).getWrongAnswer2());
                    break;
                case 3:
                    ((TextView) findViewById(R.id.txtAnswerChoice1)).setText(allFlashcards.get(currentCardDisplayedIndex).getWrongAnswer1());
                    ((TextView) findViewById(R.id.txtAnswerChoice2)).setText(allFlashcards.get(currentCardDisplayedIndex).getWrongAnswer2());
                    ((TextView) findViewById(R.id.txtAnswerChoice3)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());
            }
        }else {
            ((TextView) findViewById(R.id.txtQuestion)).setText("");
            ((TextView) findViewById(R.id.txtAnswerChoice1)).setText("");
            ((TextView) findViewById(R.id.txtAnswerChoice2)).setText("");
            ((TextView) findViewById(R.id.txtAnswerChoice3)).setText("");
            currentCardDisplayedIndex  =-1;
            Toast.makeText(getApplicationContext(), "No more questions.", Toast.LENGTH_SHORT).show();
        }

    }

    void CheckAnswer(int answer)
    {
        if(((TextView) findViewById(R.id.txtQuestion)).getTag().equals(answer)) {
            switch (answer) {
                case 1:
                    ((TextView) findViewById(R.id.txtAnswerChoice1)).setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    break;
                case 2:
                    ((TextView) findViewById(R.id.txtAnswerChoice2)).setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    break;
                case 3:
                    ((TextView) findViewById(R.id.txtAnswerChoice3)).setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    break;
            }

            new ParticleSystem(MainActivity.this, 100, R.drawable.confetti, 3000)
                    .setSpeedRange(0.2f, 0.5f)
                    .oneShot(findViewById(R.id.txtAnswerChoice2), 100);
        }
        else
        {
            if(((TextView) findViewById(R.id.txtQuestion)).getTag().equals(1))
                ((TextView) findViewById(R.id.txtAnswerChoice1)).setBackgroundColor(getResources().getColor(R.color.colorGreen));
            else
                ((TextView) findViewById(R.id.txtAnswerChoice1)).setBackgroundColor(getResources().getColor(R.color.colorRed));
            if(((TextView) findViewById(R.id.txtQuestion)).getTag().equals(2))
                ((TextView) findViewById(R.id.txtAnswerChoice2)).setBackgroundColor(getResources().getColor(R.color.colorGreen));
            else
                ((TextView) findViewById(R.id.txtAnswerChoice2)).setBackgroundColor(getResources().getColor(R.color.colorRed));
            if(((TextView) findViewById(R.id.txtQuestion)).getTag().equals(3))
                ((TextView) findViewById(R.id.txtAnswerChoice3)).setBackgroundColor(getResources().getColor(R.color.colorGreen));
            else
                ((TextView) findViewById(R.id.txtAnswerChoice3)).setBackgroundColor(getResources().getColor(R.color.colorRed));
        }
    }

    private void startTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }

    FlashcardDatabase flashcardDatabase;    //Declare Database
    List<Flashcard> allFlashcards;          //A list of all questions
    int currentCardDisplayedIndex = 0;      //Current question index
    CountDownTimer countDownTimer;          //Timer

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashcardDatabase = new FlashcardDatabase(this);    //Initialize Database
        allFlashcards = flashcardDatabase.getAllCards();            //Get the records and store in allFlashcards

        //DisplayScreen();
        //startTimer();

        findViewById(R.id.txtAnswerChoice1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckAnswer(1);
            }
        });

        findViewById(R.id.txtAnswerChoice2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckAnswer(2);
            }
        });

        findViewById(R.id.txtAnswerChoice3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckAnswer(3);
            }
        });

        findViewById(R.id.toggle_choices_visibility).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.txtQuestion)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.txtAnswerCorrect)).setVisibility(View.INVISIBLE);

                if (isShowingAnswers) {
                    ((ImageView) findViewById(R.id.toggle_choices_visibility)).setImageResource(R.drawable.show_icon);

                    ((TextView) findViewById(R.id.txtQuestion)).setClickable(true);
                    ((TextView) findViewById(R.id.txtAnswerChoice1)).setVisibility(View.INVISIBLE);
                    ((TextView) findViewById(R.id.txtAnswerChoice2)).setVisibility(View.INVISIBLE);
                    ((TextView) findViewById(R.id.txtAnswerChoice3)).setVisibility(View.INVISIBLE);
                     isShowingAnswers = false;
                }
                else {
                    ((ImageView) findViewById(R.id.toggle_choices_visibility)).setImageResource(R.drawable.hide_icon);

                    ((TextView) findViewById(R.id.txtQuestion)).setClickable(false);
                    ((TextView) findViewById(R.id.txtAnswerChoice1)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.txtAnswerChoice2)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.txtAnswerChoice3)).setVisibility(View.VISIBLE);
                    isShowingAnswers = true;
                }
            }
        });

        findViewById(R.id.btnPlus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Pauline", "edit");
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                intent.putExtra("Question", ((TextView) findViewById(R.id.txtQuestion)).getText().toString());

                if (((TextView) findViewById(R.id.txtQuestion)).getTag().equals(1)) {
                    intent.putExtra("AnswerCorrect", ((TextView) findViewById(R.id.txtAnswerChoice1)).getText().toString());
                    intent.putExtra("AnswerChoice2", ((TextView) findViewById(R.id.txtAnswerChoice2)).getText().toString());
                    intent.putExtra("AnswerChoice3", ((TextView) findViewById(R.id.txtAnswerChoice3)).getText().toString());
                }else if(((TextView) findViewById(R.id.txtQuestion)).getTag().equals(2)) {
                    intent.putExtra("AnswerChoice2", ((TextView) findViewById(R.id.txtAnswerChoice1)).getText().toString());
                    intent.putExtra("AnswerCorrect", ((TextView) findViewById(R.id.txtAnswerChoice2)).getText().toString());
                    intent.putExtra("AnswerChoice3", ((TextView) findViewById(R.id.txtAnswerChoice3)).getText().toString());
                }else if(((TextView) findViewById(R.id.txtQuestion)).getTag().equals(3)) {
                    intent.putExtra("AnswerChoice2", ((TextView) findViewById(R.id.txtAnswerChoice1)).getText().toString());
                    intent.putExtra("AnswerChoice3", ((TextView) findViewById(R.id.txtAnswerChoice2)).getText().toString());
                    intent.putExtra("AnswerCorrect", ((TextView) findViewById(R.id.txtAnswerChoice3)).getText().toString());
                }
                startActivityForResult(intent, 101);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                DisplayScreen();
            }
        });

        findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // advance our pointer index so we can show the next card
                currentCardDisplayedIndex = getRandomNumber(0,allFlashcards.size() - 1);

                // make sure we don't get an IndexOutOfBoundsError if we are viewing the last indexed card in our list
                if (currentCardDisplayedIndex > allFlashcards.size() - 1) {
                    currentCardDisplayedIndex = 0;
                }

                // set the question and answer TextViews with data from the database
                DisplayScreen();
                startTimer();
            }
        });

        findViewById(R.id.txtQuestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Circle Animation
                View answerSideView = findViewById(R.id.txtAnswerCorrect);

                // get the center for the clipping circle
                int cx = answerSideView.getWidth() / 2;
                int cy = answerSideView.getHeight() / 2;

                // get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx, cy);

                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(answerSideView, cx, cy, 0f, finalRadius);

                 // hide the question and show the answer to prepare for playing the animation!
                ((TextView) findViewById(R.id.txtQuestion)).setVisibility(View.INVISIBLE) ;
                answerSideView.setVisibility(View.VISIBLE);

                anim.setDuration(3000);
                anim.start();

//Card flip animation
/*
                View questionSideView = findViewById(R.id.txtQuestion);
                questionSideView.animate()
                        .rotationY(90)
                        .setDuration(200)
                        .withEndAction(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        findViewById(R.id.txtQuestion).setVisibility(View.INVISIBLE);
                                        findViewById(R.id.txtAnswerCorrect).setVisibility(View.VISIBLE);
                                        // second quarter turn
                                        findViewById(R.id.txtAnswerCorrect).setRotationY(-90);
                                        findViewById(R.id.txtAnswerCorrect).animate()
                                                .rotationY(0)
                                                .setDuration(200)
                                                .start();
                                    }
                                }
                        ).start();
*/

            }
        });

        findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardDatabase.deleteCard(((TextView) findViewById(R.id.txtQuestion)).getText().toString());
                allFlashcards = flashcardDatabase.getAllCards();

                // make sure we don't get an IndexOutOfBoundsError if we are viewing the last indexed card in our list
                if (currentCardDisplayedIndex > allFlashcards.size() - 1) {
                    currentCardDisplayedIndex = allFlashcards.size() - 1;
                }

                // set the question and answer TextViews with data from the database
                DisplayScreen();
            }
        });

        countDownTimer = new CountDownTimer(16000, 1000) {
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.txtTimer)).setText("Timer: " + millisUntilFinished / 1000 + " seconds");
            }

            public void onFinish() {
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String question = data.getExtras().getString("Question");
        String correct = data.getExtras().getString("AnswerCorrect");
        String answer2 = data.getExtras().getString("AnswerChoice2");
        String answer3 = data.getExtras().getString("AnswerChoice3");

        if (requestCode == 100 && resultCode ==RESULT_OK) {
            flashcardDatabase.insertCard(new Flashcard(question, correct, answer2, answer3));
            allFlashcards = flashcardDatabase.getAllCards();
            currentCardDisplayedIndex = allFlashcards.size()-1;
            Snackbar.make(findViewById(R.id.RootView),
                    "Card Added Successfully",
                    Snackbar.LENGTH_SHORT)
                    .show();

        }
        if (requestCode == 101 && resultCode ==RESULT_OK)
        {
            List<Flashcard> allCards = flashcardDatabase.getAllCards();
            for (Flashcard cardToEdit : allCards) {
                if (cardToEdit.getQuestion().equals(allFlashcards.get(currentCardDisplayedIndex).getQuestion())) {
                    cardToEdit.setQuestion(question);
                    cardToEdit.setAnswer(correct);
                    cardToEdit.setWrongAnswer1(answer2);
                    cardToEdit.setWrongAnswer2(answer3);
                    flashcardDatabase.updateCard(cardToEdit);
                    allFlashcards = flashcardDatabase.getAllCards();
                }
            }

            Snackbar.make(findViewById(R.id.RootView),
                    "Card Changed Successfully",
                    Snackbar.LENGTH_SHORT)
                    .show();

        }

        DisplayScreen();
    }

}
