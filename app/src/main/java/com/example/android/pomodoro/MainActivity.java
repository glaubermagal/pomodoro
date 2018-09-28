package com.example.android.pomodoro;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView countDownText;
    private TextView countDownCycleText;
    private TextView captionText;
    private Button countDownButton;
    private Button resetButton;
    private Button stopButton;
    private CountDownTimer countDownTimer;
    private int defaultWorkTimeLeftMS = 1500000;
    private int defaultShortBreakTimeLeftMS = 300000;
    private int defaultLongBreakTimeLeftMS = 1800000;
    private int countDownCounter = 1;
    private long timeLeftMS = defaultWorkTimeLeftMS;
    private boolean timerRunning;
    private String typeTimerRunning = "WORK"; //WORK, SHORT_BREAK, LONG_BREAK
    ToneGenerator toneG;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countDownText = findViewById(R.id.countdown_text);
        countDownCycleText = findViewById(R.id.countdown_cycle);
        captionText = findViewById(R.id.caption_text);
        countDownButton = findViewById(R.id.countdown_button);
        resetButton = findViewById(R.id.button_reset);
        stopButton = findViewById(R.id.button_stop);
        mProgressBar = findViewById(R.id.timer_progress_bar);
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

        setCyclePosition(1);

        countDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPause();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                countDownButton.setText(R.string.caption_start);
                timerRunning = false;
                mProgressBar.setProgress(0);

                switch (typeTimerRunning) {
                    case "WORK":
                        captionText.setText(R.string.caption_work);
                        countDownText.setText(R.string.work_timer);
                        timeLeftMS = defaultWorkTimeLeftMS;
                        break;
                    case "SHORT_BREAK":
                        captionText.setText(R.string.caption_short_break);
                        countDownText.setText(R.string.short_break_timer);
                        timeLeftMS = defaultShortBreakTimeLeftMS;
                        break;
                    default:
                        captionText.setText(R.string.caption_long_break);
                        countDownText.setText(R.string.long_break_timer);
                        timeLeftMS = defaultLongBreakTimeLeftMS;
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                countDownButton.setText(R.string.caption_start);
                captionText.setText(R.string.caption_work);
                countDownText.setText(R.string.work_timer);
                timerRunning = false;
                countDownCounter = 1;
                mProgressBar.setProgress(0);
                timeLeftMS = defaultWorkTimeLeftMS;
                setCyclePosition(1);
            }
        });
    }

    public void startPause() {
        if (timerRunning) {
            pauseTimer();
        } else {
            switch (typeTimerRunning) {
                case "WORK":
                    startWorkTimer();
                    break;
                case "SHORT_BREAK":
                    startShortBreakTimer();
                    break;
                default:
                    startLongBreakTimer();
            }
            countDownButton.setText(R.string.caption_pause);
            timerRunning = true;
        }
    }

    public void startWorkTimer() {
        captionText.setText(R.string.caption_work);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftMS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMS = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

                if (countDownCounter >= 4) {
                    countDownCycleText.setText("");
                    countDownCounter = 1;
                    timeLeftMS = defaultLongBreakTimeLeftMS;
                    typeTimerRunning = "LONG_BREAK";
                    startLongBreakTimer();
                } else {
                    setCyclePosition(countDownCounter);
                    timeLeftMS = defaultShortBreakTimeLeftMS;
                    typeTimerRunning = "SHORT_BREAK";
                    startShortBreakTimer();
                }
            }
        }.start();
    }

    public void startShortBreakTimer() {
        captionText.setText(R.string.caption_short_break);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftMS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMS = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                countDownCounter++;
                setCyclePosition(countDownCounter);

                timeLeftMS = defaultWorkTimeLeftMS;
                typeTimerRunning = "WORK";

                startWorkTimer();
            }
        }.start();
    }

    public void startLongBreakTimer() {
        captionText.setText(R.string.caption_long_break);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftMS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMS = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                setCyclePosition(1);

                timeLeftMS = defaultWorkTimeLeftMS;
                typeTimerRunning = "WORK";

                startWorkTimer();
            }
        }.start();
    }

    public void pauseTimer() {
        countDownTimer.cancel();
        countDownButton.setText(R.string.caption_start);
        timerRunning = false;
    }

    public void setCyclePosition(int cycle) {
        String formattedCounter = getString(R.string.cowntdown_counter,"" + cycle);
        countDownCycleText.setText(formattedCounter);
    }

    public void updateTimer() {
        int minutes = (int) timeLeftMS / 60000;
        int seconds = (int) timeLeftMS % 60000 / 1000;
        double progressCalculus;
        int progress;
        String timeLeftText;

        switch (typeTimerRunning) {
            case "WORK":
                progressCalculus = (double) (defaultWorkTimeLeftMS - timeLeftMS) / defaultWorkTimeLeftMS * 100;
                break;
            case "SHORT_BREAK":
                progressCalculus = (double) (defaultShortBreakTimeLeftMS - timeLeftMS) / defaultShortBreakTimeLeftMS * 100;
                break;
            default:
                progressCalculus = (double) (defaultLongBreakTimeLeftMS - timeLeftMS) / defaultLongBreakTimeLeftMS * 100;
        }

        progress = (int) progressCalculus;
        mProgressBar.setProgress(progress);

        timeLeftText = "";
        if (minutes < 10) timeLeftText += "0";
        timeLeftText += minutes;
        timeLeftText += ":";
        if (seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;

        countDownText.setText(timeLeftText);
    }

}
