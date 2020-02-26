package com.example.movingviewsample3.data;

import android.graphics.PointF;

/**
 * 백그라운드 스크린에 대한 데이터클래스
 */
public class BackgroundScreen {

    // 스크린의 위치와 사이즈
    public PointF leftTop = new PointF();
    public PointF rightBottom = new PointF();

    // 스크린의 색상 (RGB 코드)
    public int color = 0x000000;
}
