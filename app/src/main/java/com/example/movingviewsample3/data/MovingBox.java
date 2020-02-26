package com.example.movingviewsample3.data;

import android.graphics.PointF;


/**
 * 움직이는 박스에 대한 데이터 클래스
 */
public class MovingBox {

    // 박스의 위치
    public PointF position = new PointF();
    // 박스의 사이즈
    public PointF size = new PointF();

    // 박스의 움직이는 속도
    public int speed = 8;
}
