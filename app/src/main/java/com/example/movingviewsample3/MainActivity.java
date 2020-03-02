package com.example.movingviewsample3;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movingviewsample3.data.BackgroundScreen;
import com.example.movingviewsample3.data.MovingBox;
import com.example.movingviewsample3.util.TaskRepeater;

public class MainActivity extends AppCompatActivity {

    // 뒷배경 뷰
    private FrameLayout mScreenView;
    // MovingBox 뷰
    private View mMovingBoxView;
    private FrameLayout.LayoutParams mMovingBoxParams;

    // 데이터
    private BackgroundScreen mScreen = new BackgroundScreen();
    private MovingBox mMovingBox = new MovingBox();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 뷰 초기화 실시
        mScreenView = findViewById(R.id.top_framelayout);
        mMovingBoxView = findViewById(R.id.moving_box_view);


        // 리스너 등록
        mScreenView.setOnTouchListener(new ScreenTouchListener());
        configGridButtons();

        // 뷰가 완전히 그려지길 기다리는 콜백
        mScreenView.post(new Runnable() {
            @Override
            public void run() {
                // MovingBox 파라미터 초기화
                mMovingBoxParams = new FrameLayout.LayoutParams(mMovingBoxView.getWidth(), mMovingBoxView.getHeight());

                // 뷰 정보 데이터 모델에 기입
                mMovingBox.position = new PointF(mMovingBoxView.getX(), mMovingBoxView.getY());
                mMovingBox.size = new PointF(mMovingBoxView.getWidth(), mMovingBoxView.getHeight());

                mScreen.leftTop = new PointF(mScreenView.getX(), mScreenView.getY());
                mScreen.rightBottom = new PointF(mScreenView.getX() + mScreenView.getWidth(), mScreenView.getY() + mScreenView.getHeight());
            }
        });
    }

    /**
     * 그리드 레이아웃의
     * 게임 컨트롤러 버튼을 초기화,
     * 각 버튼에 리스너 등록 한다
     */
    public void configGridButtons() {
        GridLayout gridLayout = findViewById(R.id.bottom_gridlayout);
        GridButtonListener listener = new GridButtonListener();

        // 그리드 레이아웃의 모든 자식뷰( 9버튼 ) 에 지정된 리스너 부착
        for(int i = 0; i < gridLayout.getChildCount(); ++i) {
            View v = gridLayout.getChildAt(i);
            v.setOnTouchListener(listener);

        }
    }

    /**
     * MovingBox의 위치를 바꾼다
     * 스크린의 범위를 빠져나갈시 박스를 스크린 안으로 재위치한다
     * @param position 위치값
     */
    public void setBoxPosition(final PointF position) {
        mMovingBox.position.x = Math.min(mScreen.rightBottom.x - mMovingBox.size.y,
                Math.max(mScreen.leftTop.x, position.x));
        mMovingBox.position.y = Math.min(mScreen.rightBottom.y - mMovingBox.size.y,
                Math.max(mScreen.leftTop.y, position.y));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMovingBoxParams.leftMargin = (int)mMovingBox.position.x;
                mMovingBoxParams.topMargin = (int)mMovingBox.position.y;

                mMovingBoxView.setLayoutParams(mMovingBoxParams);
            }
        });
    }

    /**
     * 박스의 사이즈를 설정한다
     * @param size 사이즈값
     */
    public void setBoxSize(final PointF size) {
        mMovingBox.size.x = size.x;
        mMovingBox.size.y = size.y;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMovingBoxParams.width = (int) mMovingBox.size.x;
                mMovingBoxParams.height = (int) mMovingBox.size.y;

                mMovingBoxView.setLayoutParams(mMovingBoxParams);
            }
        });

    }

    /**
     * 스크린의 컬러를 설정한다
     * @param color 컬러 리소스 상수
     */
    public void setScreenColor(final int color) {
        mScreen.color = color;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mScreenView.setBackgroundColor( getColor(color) );
            }
        });
    }

    /**
     * MovingBox 현재 위치를 기준으로 increment 만큼 MovingBox 좌표를 이동
     * @param increment 증가치
     */
    public void offsetBox(PointF increment) {
        PointF pos = new PointF(mMovingBox.position.x + increment.x,
                mMovingBox.position.y + increment.y);

        setBoxPosition(pos);
    }



    /**
     * 스크린 터치 시
     * 1. 이동 된 거리를 추적하여 이동값을 알아낸뒤
     * 이동값만큼 박스를 움직인다
     *
     * 2. 터치 시작 및 종료시 배경을 해당되는 색깔로 바꾼다
     */
    final class ScreenTouchListener implements View.OnTouchListener {
        // 이전 위치값
        private int mLastX;
        private int mLastY;
        // 이동거리 값
        private PointF mMovement = new PointF();

        /**
         * 터치 핸들링 메서드
         *
         * @param v     터치된 뷰
         * @param event 모션이벤트
         * @return 이벤트 처리여부
         */
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int curX = (int) event.getX();
            int curY = (int) event.getY();

            switch (event.getAction()) {

                // 터치 시작때는 위치기록만
                case MotionEvent.ACTION_DOWN:{
                    mLastX = curX;
                    mLastY = curY;

                    setScreenColor(R.color.move_state);
                }break;

                // 터치이동중에는 이전위치를 추적해서 알아낸 이동거리를 전달
                case MotionEvent.ACTION_MOVE:{
                    mMovement.x = curX - mLastX;
                    mMovement.y = curY - mLastY;

                    mLastX = curX;
                    mLastY = curY;

                    offsetBox(mMovement);
                }break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:{
                    setScreenColor(R.color.stop_state);
                }break;
            }

            return true;
        }
    }

    /**
     * 그리드 버튼 터치 시작 및 종료시 배경을 해당되는 색깔로 바꾼다
     */
    final class GridButtonListener implements View.OnTouchListener {

        // 포인터 DOWN ~ UP 사이에 게임오브젝트를 움직여줄 리피터
        private TaskRepeater mTaskRepeater = new TaskRepeater();

        /**
         * 터치 핸들링 메서드
         *
         * @param v     터치된 뷰
         * @param event 모션이벤트
         * @return 사용안함
         */
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    final PointF increment = getIncrement(v.getId());

                    if(mTaskRepeater.isRunning()) mTaskRepeater.stopRepeater();

                    mTaskRepeater.setTask(new Runnable() {
                        @Override
                        public void run() {
                            offsetBox(increment);
                        }
                    });
                    mTaskRepeater.startRepeater();
                }
                break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    mTaskRepeater.stopRepeater();
                }
                break;
            }

            //true 를 반환하면 이벤트 전달이 되지 않아 버튼의 잔상효과 발생안함
            return false;
        }

        /**
         * id를 통해 x,y 를 매번 얼만큼 움직여야할지 구하는 메서드
         *
         * @param id 뷰의 id
         * @return 얼만큼 움직여야할지 담겨있는 Point객체
         */
        public PointF getIncrement(int id) {
            PointF direction = new PointF(0, 0);

            // 이벤트 아이디로 구별해서 방향을 달리 한다
            switch (id) {
                case R.id.gridcell_top:{
                    direction.y = -mMovingBox.speed;
                }break;
                case R.id.gridcell_bottom:{
                    direction.y = mMovingBox.speed;
                }break;
                case R.id.gridcell_left:{
                    direction.x = -mMovingBox.speed;
                }break;
                case R.id.gridcell_right:{
                    direction.x = mMovingBox.speed;
                }break;


                case R.id.gridcell_lefttop:{
                    direction.x = -mMovingBox.speed;
                    direction.y = -mMovingBox.speed;
                }break;
                case R.id.gridcell_leftbottom:{
                    direction.x = -mMovingBox.speed;
                    direction.y = +mMovingBox.speed;
                }break;


                case R.id.gridcell_righttop:{
                    direction.x = +mMovingBox.speed;
                    direction.y = -mMovingBox.speed;
                }break;
                case R.id.gridcell_rightbottom:{
                    direction.x = +mMovingBox.speed;
                    direction.y = +mMovingBox.speed;
                }break;
            }

            return direction;
        }
    }

}
