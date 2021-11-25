package com.example.nbacktest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private TextView levelTextView;
    private TextView sumResultView;
    private ArrayList<NBack> nBackList;
    private Boolean mode;
    private int level;
    private boolean parallel;
    private int examNum;
    private final float passScoreRatio = 0.28f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();

        nBackList = new ArrayList<NBack>();
        mode = intent.getBooleanExtra("mode", true);          // mode: true는 랭킹 모드, false는 연습 모드
        parallel = intent.getBooleanExtra("parallel", false);

        // intent를 통해 가져온 매개변수를 가지고 NBack 객체 초기화
        nBackList.add( new NBack(intent.getIntExtra("n", 2),
                                intent.getIntExtra("examLength", 7),
                                intent.getIntExtra("delayTime", 1),
                                findViewById(R.id.examView),
                                findViewById(R.id.resultView)));


        Log.i("intent", "mode: "+ mode + "\nN: " + nBackList.get(0).getN() +
                                        "\nlength: " + nBackList.get(0).getExamLength() +
                                        "\ndelay: " + nBackList.get(0).getDelayTime() +
                                        "\nparallel: " + parallel);

        // 모드를 통한 초기 레벨 설정
        if (mode) {
            levelTextView = findViewById(R.id.levelTextView);
            level = 0;
            levelUP();
        }

        // 병렬 화면 설정
        if (parallel){
            nBackList.add( new NBack(intent.getIntExtra("n", 2),
                    intent.getIntExtra("examLength", 7),
                    intent.getIntExtra("delayTime", 1),
                    findViewById(R.id.examView2),
                    findViewById(R.id.resultView2)));

            sumResultView = findViewById(R.id.sumResultView);
            nBackList.get(1).getExamView().setVisibility(View.VISIBLE);
        }

        Log.i("listSize", "listSize: " + nBackList.size());
    }

    public void onClickStart(View startView) {
        startView.setVisibility(View.GONE);

        if (parallel) {
            sumResultView.setVisibility(View.GONE);
            findViewById(R.id.answerBtn2).setVisibility(View.VISIBLE);
        }

        for(int i = 0; i < nBackList.size(); i++) {
            nBackList.get(i).getResultView().setVisibility(View.GONE);
            nBackList.get(i).init();
        }

        threeCount(startView);
    }

    public void threeCount(View startView){
        nBackList.get(0).getExamView().setTextSize(50);
        if (parallel)
            nBackList.get(1).getExamView().setVisibility(View.GONE);
        new Thread(new Runnable() {                                         // 수정?
            @Override
            public void run() {
                for (int i=4; i>0; i--){
                    int threeCnt = i-1;
                    GameActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(threeCnt==0) {
                                nBackList.get(0).getExamView().setText("시작!");
                                nBackList.get(0).getExamView().setTextSize(100);
                            }
                            else
                                nBackList.get(0).getExamView().setText(threeCnt + "초 후 시작!");
                        }
                    });
                    try {
                        if(i== 1) {
                            // gameStart
                            Thread.sleep(500);
                            GameActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    gameStart(startView);
                                }
                            });
                        }
                        else
                            Thread.sleep(1000);
                    } catch (InterruptedException e) {       // 3초후, 3 ,2, 1,
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void gameStart(View startView){
        examNum = -1;
        for(int i=0; i<nBackList.size(); i++) {
            nBackList.get(i).getExamView().setText("");
            nBackList.get(i).getExamView().setVisibility(View.VISIBLE);
        }

        Log.i("userAnswer","userAnswer: " + nBackList.get(0).getUserAnswer());

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            String[] numColors = {"#4F4B4B", "#E08F31", "#036994"};   // 반복할 글씨의 컬러, 같은 숫자가 연속될 경우 구분하기 위함.

            @Override
            public void run() {

                if (++examNum < nBackList.get(0).getExamLength()) {                                       // 문제 푸는 구간
                    for(int i=0; i<nBackList.size(); i++){                                                          // 문제 표시구간
                        nBackList.get(i).getExamView().setTextColor(Color.parseColor(numColors[examNum % 3]));
                        nBackList.get(i).getExamView().setText(nBackList.get(i).getExam().charAt(examNum)+"");
                    }

                    handler.postDelayed(this, nBackList.get(0).getDelayTime());
                }
                else{
                    // test 끝나고 결과 출력
                    showResult();
                    startView.setVisibility(View.VISIBLE);
                }
            }
        }, nBackList.get(0).getDelayTime());
    }

    public void onClickO(View v){
        if (examNum >= nBackList.get(0).getN() && examNum < nBackList.get(0).getExamLength()) {             // "O" 버튼 클릭시  userAnswer의 "O" 추가
            nBackList.get(0).setUserAnswer(examNum);

            Log.i("userAnswer","userAnswer: " + nBackList.get(0).getUserAnswer() +
                    " examNum: " + examNum +
                    " Exam: " + nBackList.get(0).getExam().charAt(examNum));
        }
    }

    public void onClickO2(View v){
        if (examNum >= nBackList.get(1).getN() && examNum < nBackList.get(1).getExamLength())            // "O" 버튼 클릭시  userAnswer의 "O" 추가
            nBackList.get(1).setUserAnswer(examNum);
    }

    public void showResult(){
        float sumScore = 0f;

        for(int i=0; i<nBackList.size(); i++) {

            nBackList.get(i).caculateScore();

            nBackList.get(i).getResultView().setText("");
            nBackList.get(i).getResultView().append((i+1) + "번 문제 : " + nBackList.get(i).getExam() +
                                                        "\n정답 : " + nBackList.get(i).getRightAnswer() +
                                                        "\nuser 답 : " + nBackList.get(i).getUserAnswer() +
                                                        "\nScore : " + nBackList.get(i).getScore());
            sumScore += nBackList.get(i).getScore();
            nBackList.get(i).getResultView().setVisibility(View.VISIBLE);
        }

        if (nBackList.size()>1){
            sumResultView.setText("Score 합: " + sumScore);
            sumResultView.setVisibility(View.VISIBLE);
        }


        if (sumScore >= Math.floor(nBackList.get(0).getExamLength()*passScoreRatio) && mode)                      // 합격 점수 이상일 때
            levelUP();
    }

    public void levelUP(){

        ((TextView)findViewById(R.id.levelTextView)).setText("랭크게임: LEVEL " + (++level));
        if (level==1)
            return;

        for(int i=0; i<nBackList.size(); i++) {
            if (level < 10)
                nBackList.get(i).plusExamLength();
            else if (level < 20)
                nBackList.get(i).plusN();
            else if (level == 23)
                nBackList.get(i).minusDelayTime();
            else{                                                       // 새로운 nBack 추가
                parallel = true;
                nBackList.add(new NBack(nBackList.get(0).getN(),
                        nBackList.get(0).getExamLength(),
                        nBackList.get(0).getDelayTime(),
                        findViewById(R.id.examView2),
                        findViewById(R.id.resultView2)));
            }
        }
    }


}