package com.mobishift.cordova.plugins.amapnavigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;

public class MainActivity extends Activity implements
        AMapNaviViewListener{
    public static final String ISEMULATOR="isemulator";
    public static final String ACTIVITYINDEX="activityindex";
    //导航View
    private AMapNaviView mAmapAMapNaviView;
    //是否为模拟导航
    private boolean mIsEmulatorNavi = false;
    //记录有哪个页面跳转而来，处理返回键
    private int mCode=-1;

    //起点终点
    private NaviLatLng mNaviStart = new NaviLatLng(39.989614, 116.481763);
    private NaviLatLng mNaviEnd = new NaviLatLng(39.983456, 116.3154950);


    private CameraPreview preview;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout l = new LinearLayout(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        l.setLayoutParams(layoutParams);

        mAmapAMapNaviView = new AMapNaviView(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        l.addView(mAmapAMapNaviView, lp);

        setContentView(l);

        Bundle bundle = getIntent().getExtras();
        processBundle(bundle);
        init(savedInstanceState);

    }

    private void processBundle(Bundle bundle) {
        if (bundle != null) {
            mIsEmulatorNavi = bundle.getBoolean(ISEMULATOR, true);
            mCode=bundle.getInt(ACTIVITYINDEX);
        }
    }

    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    private void init(Bundle savedInstanceState) {
        //mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.simplenavimap);
        mAmapAMapNaviView.onCreate(savedInstanceState);
        mAmapAMapNaviView.setAMapNaviViewListener(this);
        //TTSController.getInstance(this).startSpeaking();

        AMapNavi.getInstance(this).calculateDriveRoute(mStartPoints,
                mEndPoints, null, AMapNavi.DrivingDefault);
    }

    @Override
    public void onCalculateRouteFailure(int arg0) {

    }

    @Override
    public void onCalculateRouteSuccess() {
        if (mIsEmulatorNavi) {
            // 设置模拟速度
            AMapNavi.getInstance(this).setEmulatorNaviSpeed(100);
            // 开启模拟导航
            AMapNavi.getInstance(this).startNavi(AMapNavi.EmulatorNaviMode);

        } else {
            // 开启实时导航
            AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
        }
    }

    //-----------------------------导航界面回调事件------------------------
    /**
     * 导航界面返回按钮监听
     * */
    @Override
    public void onNaviCancel() {
//        Intent intent = new Intent(SimpleNaviActivity.this,
//                MainStartActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        startActivity(intent);
        finish();
    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviMapMode(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onNaviTurnClick() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onNextRoadClick() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onScanViewButtonClick() {
        // TODO Auto-generated method stub
    }
    /**
     *
     * 返回键监听事件
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if(mCode==SIMPLEROUTENAVI){
//                Intent intent = new Intent(SimpleNaviActivity.this,
//                        SimpleNaviRouteActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                startActivity(intent);
//                finish();
//
//            }
//            else if(mCode==SIMPLEGPSNAVI){
//                Intent intent = new Intent(SimpleNaviActivity.this,
//                        SimpleGPSNaviActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                startActivity(intent);
//                finish();
//            }
//            else{
//                finish();
//            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // ------------------------------生命周期方法---------------------------
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAmapAMapNaviView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAmapAMapNaviView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAmapAMapNaviView.onPause();
        AMapNavi.getInstance(this).stopNavi();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAmapAMapNaviView.onDestroy();
        //TTSController.getInstance(this).stopSpeaking();
    }

    @Override
    public void onLockMap(boolean arg0) {
        // TODO Auto-generated method stub
    }
}
