package com.example.nbacktest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {
    private TextView examView;
    private TextView resultView;
    private NBack nBack;
    private Boolean currentAnswer;
    private Boolean mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        mode = intent.getBooleanExtra("mode", true);          // mode: true는 랭킹 모드, false는 연습 모드

        examView = findViewById(R.id.examView);
        resultView = findViewById(R.id.resultView);

        // intent를 통해 가져온 매개변수를 가지고 NBack 객체 초기화
        nBack = new NBack(intent.getIntExtra("n", 2),
                intent.getIntExtra("examLength", 7),
                intent.getIntExtra("delayTime", 1),
                intent.getBooleanExtra("parallel", false));

        Log.i("intent", "mode: "+ mode + "\nN: " + nBack.getN() +
                                        "\nlength: " + nBack.getExamLength() +
                                        "\ndelay: " + nBack.getDelayTime() +
                                        "\nparallel: " + nBack.isParallel());

        currentAnswer = false;
    }

    public void onClickStart(View v) {
        nBack.init();
        StringBuilder userAnswer = new StringBuilder();
        userAnswer.append(new String(new char[nBack.getExamLength()-nBack.getN()]).replace("\0", "X"));   // 답지 길이만큼 "X"로 채우기 => "XXXX" , 코드 나중에 수정할 예정!!

        v.setVisibility(View.GONE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            int i = 0;
            @Override
            public void run() {
                if (i < nBack.getExamLength()) {                                // 문제 푸는 구간
                    if (currentAnswer && i > nBack.getN()) {
                        userAnswer.setCharAt(i - nBack.getN(), 'O');    // i번째 문자 "o"로 치환
                        currentAnswer = false;
                    }
                    examView.setText(nBack.getExam().charAt(i++)+"");
                    handler.postDelayed(this,nBack.getDelayTime());
                }
                else{                                                           // test 끝나고 결과 출력
                    nBack.setUserAnswer(userAnswer.toString());
                    nBack.caculateScore();
                    resultView.setText("문제 : " + nBack.getExam() +
                            "\n정답 : " + nBack.getRightAnswer() +
                            "\nuser 답 : " + userAnswer +
                            "\nScore : " + nBack.getScore());
                    v.setVisibility(View.VISIBLE);
                }
            }
        }, nBack.getDelayTime());
    }

    public void onClickO(View v){
        currentAnswer = true;           // O 버튼 클릭시 해당 변수를 true 바꿔서 위에서 userAnswer 수정
    }
}