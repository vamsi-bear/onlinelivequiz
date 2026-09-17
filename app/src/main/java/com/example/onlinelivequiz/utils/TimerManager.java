package com.example.onlinelivequiz.utils;

import android.os.CountDownTimer;

public class TimerManager {

    private CountDownTimer countDownTimer;

    private long remainingTime;

    private TimerListener timerListener;

    public TimerManager(long durationMillis,
                        TimerListener timerListener) {

        this.remainingTime = durationMillis;
        this.timerListener = timerListener;
    }

    public void start() {

        countDownTimer = new CountDownTimer(
                remainingTime,
                1000
        ) {

            @Override
            public void onTick(long millisUntilFinished) {

                remainingTime = millisUntilFinished;

                long minutes =
                        millisUntilFinished / 60000;

                long seconds =
                        (millisUntilFinished % 60000) / 1000;

                timerListener.onTick(
                        minutes,
                        seconds
                );
            }

            @Override
            public void onFinish() {

                remainingTime = 0;

                timerListener.onFinish();
            }

        };

        countDownTimer.start();
    }

    public void pause() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void resume() {

        start();
    }

    public void cancel() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public long getRemainingTime() {

        return remainingTime;
    }

    public interface TimerListener {

        void onTick(
                long minutes,
                long seconds
        );

        void onFinish();
    }
}
