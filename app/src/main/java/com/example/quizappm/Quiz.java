package com.example.quizappm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.quizappm.Model.QuestionAnswer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Quiz extends AppCompatActivity {

    private Button buttonNext;
    private RadioButton answer1;
    private RadioButton answer2;
    private RadioButton answer3;
    private RadioButton answer4;
    private TextView question;
    private ImageView image;
    private int score=0;
    private int cmpt=0;
    QuestionAnswer questionAnswer;
    DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        answer1=(RadioButton) findViewById(R.id.radio_1);
        answer2=(RadioButton) findViewById(R.id.radio_2);
        answer3=(RadioButton) findViewById(R.id.radio_3);
        answer4=(RadioButton) findViewById(R.id.radio_4);
        question=(TextView) findViewById(R.id.tvQuestion);
        image=(ImageView) findViewById(R.id.ivImage);
        buttonNext= (Button) findViewById(R.id.bNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateView();
            }
        });



        updateView();

    }

    private void updateView() {

        if(cmpt<3) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("questions").child(String.valueOf(cmpt));
            //le listner attend la valeur cmpt change
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                //ici on est sur que les donnees change
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //il met les donnees qui sont au sein de snapshot dans questionAnswer
                    questionAnswer = snapshot.getValue(QuestionAnswer.class);

                    question.setText(questionAnswer.getQuestion());
                    answer1.setText(questionAnswer.getAnswer1());
                    answer2.setText(questionAnswer.getAnswer2());
                    answer3.setText(questionAnswer.getAnswer3());
                    answer4.setText(questionAnswer.getAnswer4());

                    Picasso.with(getApplicationContext()).load(questionAnswer.getImage()).into(image);

                    answer1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (questionAnswer.getCorrectIndex() == 2) score++;

                        }
                    });
                    answer2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (questionAnswer.getCorrectIndex() == 3) score++;

                        }
                    });
                    answer3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (questionAnswer.getCorrectIndex() == 2) score++;

                        }
                    });
                    answer4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (questionAnswer.getCorrectIndex()== 2) score++;

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            cmpt++;
        }
        else
        {
            Intent intent=new Intent(this, Score.class);
            intent.putExtra("score", score);
            startActivity(intent);
        }
    }

}