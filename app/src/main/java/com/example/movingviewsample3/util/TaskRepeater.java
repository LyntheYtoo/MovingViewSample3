package com.example.movingviewsample3.util;

import org.jetbrains.annotations.NotNull;

/**
 * 해당 Task를 반복시켜주는 객체
 */
public class TaskRepeater {
    public static final int DELAY = 24;

    // 해당 태스크
    private Runnable mTask = null;
    // 반복 끝나고 실행할 콜백
    private Runnable mCallback;
    // 태스크를 반복시켜서 실행하게 하는 스레드
    private RepeatThread mThread;
    // 현재 스레드가 실행중인지 알려주는 변수
    private boolean mRunning = false;

    /**
     * 리피터 생성자
     * 반복할 태스크를 assign 한다
     *
     * @param task 반복할 task
     */
    public void setTask(@NotNull Runnable task) {
        mTask = task;
    }

    /**
     * 리피터를 시작한다
     * 설정된 Task 혹은 Thread가 null 일때 예외를 던진다
     */
    public void startRepeater() {
        if(mTask == null) throw new NullPointerException("Task is null");

        mRunning = true;
        mThread = new RepeatThread();
        mThread.start();
    }

    /**
     * 리피터를 종료한다
     */
    public void stopRepeater() {
        if(mThread == null) return;

        mRunning = false;
        mThread.interrupt();
        mThread = null;
    }

    /**
     * 현재 실행중인지 알려주는 메서드
     * @return 실행 중 여부
     */
    public boolean isRunning() { return mRunning; }

    /**
     * 반복이 끝나고 실행할 콜백을 등록한다
     * @param callback 콜백 Runnable 객체
     */
    public void setCallback(@NotNull Runnable callback) {
        mCallback = callback;
    }

    /**
     * 태스크를 반복시켜주는 쓰레드 클래스
     */
    class RepeatThread extends Thread {
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {

                    mTask.run();

                    Thread.sleep(DELAY);
                }
            } catch (InterruptedException e) {
                // 반복이 끝나고 콜백이 있을시 실행
                if(mCallback != null) mCallback.run();
            }
        }
    }
}
