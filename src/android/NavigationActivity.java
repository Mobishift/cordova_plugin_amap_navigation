package com.mobishift.cordova.plugins.navigationService;

import java.util.ArrayList;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.util.Log;
import android.widget.Toast;


import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;

import com.autonavi.tbt.NaviStaticInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.SpeechUtility;
import com.mobishift.cordova.plugins.amapnavigation.AMapNavigation;

public class NavigationActivity extends Activity implements
        AMapNaviListener,AMapNaviViewListener{
    //导航View{
    private AMapNaviView mAmapAMapNaviView;
    //是否为模拟导航
    private boolean mIsEmulatorNavi = false;
    //记录有哪个页面跳转而来，处理返回键
    private int mCode=-1;

    //起点终点
    private NaviLatLng mNaviStart;
    private NaviLatLng mNaviEnd;
    //起点终点列表
    private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
    private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
    // 合成对象.
    private SpeechSynthesizer mSpeechSynthesizer = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("result", "onCreate");
        AMapNavi.getInstance(this).setAMapNaviListener(this);
        Intent intent = getIntent();
        mNaviStart = new NaviLatLng(Float.parseFloat(intent.getStringExtra("NaviStartLat")),Float.parseFloat(intent.getStringExtra("NaviStartLng")));
        mNaviEnd = new NaviLatLng(Float.parseFloat(intent.getStringExtra("NaviEndLat")),Float.parseFloat(intent.getStringExtra("NaviEndLng")));
        LinearLayout l = new LinearLayout(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        l.setLayoutParams(layoutParams);

        mAmapAMapNaviView = new AMapNaviView(this);
        mAmapAMapNaviView.onCreate(savedInstanceState);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        l.addView(mAmapAMapNaviView, lp);

        setContentView(l);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(canAccessLocation()) {
                init();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
        } else {
            init();
        }
    }

    @TargetApi(23)
    private boolean canAccessLocation() {
        return PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if(canAccessLocation()) {
                    init();
                } else {
                    Log.w(TAG, "没有定位权限");
                    this.onDestroy();
                }
        }
    }

    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    private void init() {
        mStartPoints.add(mNaviStart);
        mEndPoints.add(mNaviEnd);
        mAmapAMapNaviView.setAMapNaviViewListener(this);
        AMapNavi.getInstance(this).calculateDriveRoute(mStartPoints,
                mEndPoints, null, AMapNavi.DrivingDefault);
        Log.i("result","注册");

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + AMapNavigation.cordovaWebView.getPreferences().getString("iflytekappid", ""));
        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this,null);
        // 设置发音人
        mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        // 设置语速
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
        // 设置音量
        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "80");

        mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {

        }

        @Override
        public void onCompleted(SpeechError error) {

        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    @Override
    public void onCalculateRouteFailure(int arg0) {
        mSpeechSynthesizer.startSpeaking("路径计算失败，请检查网络或输入参数", mTtsListener);
    }

    @Override
    public void onCalculateRouteSuccess() {
        if (mIsEmulatorNavi) {
            Log.i("result","模拟");
            // 设置模拟速度
            AMapNavi.getInstance(this).setEmulatorNaviSpeed(100);
            // 开启模拟导航
            AMapNavi.getInstance(this).startNavi(NaviType.EMULATOR);

        } else {
            Log.i("result","实时");
            // 开启实时导航
            AMapNavi.getInstance(this).startNavi(NaviType.GPS);
        }
    }

    @Override
    public void onArriveDestination() {
        // TODO Auto-generated method stub
        mSpeechSynthesizer.startSpeaking("到达目的地", mTtsListener);
    }

    @Override
    public void onArriveDestination(NaviStaticInfo naviStaticInfo) {

    }

    @Override
    public void onArrivedWayPoint(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEndEmulatorNavi() {
        // TODO Auto-generated method stub
        mSpeechSynthesizer.startSpeaking("导航结束", mTtsListener);
    }

    @Override
    public void onGetNavigationText(int arg0, String arg1) {
        // TODO Auto-generated method stub
        mSpeechSynthesizer.startSpeaking(arg1, mTtsListener);
    }

    @Override
    public void onGpsOpenStatus(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInitNaviFailure() {
        // TODO Auto-generated method stub
        Log.i("result","导航失败");
    }

    @Override
    public void onInitNaviSuccess() {
        // TODO Auto-generated method stub
        Log.i("result","导航注册成功");
    }

    @Override
    public void onLocationChange(AMapNaviLocation arg0) {
        // TODO Auto-generated method stub
        AMapNavigation.getInstance().keepCallback(arg0.getCoord());
    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        // TODO Auto-generated method stub
        mSpeechSynthesizer.startSpeaking("前方路线拥堵，路线重新规划", mTtsListener);
    }

    @Override
    public void onReCalculateRouteForYaw() {
        // TODO Auto-generated method stub
        mSpeechSynthesizer.startSpeaking("您已偏航", mTtsListener);
    }

    @Override
    public void onStartNavi(int arg0) {
        // TODO Auto-generated method stub
        Log.i("result","启动导航1");
    }

    @Override
    public void onTrafficStatusUpdate() {
        // TODO Auto-generated method stub

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
        this.setResult(RESULT_CANCELED);
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
        mSpeechSynthesizer.stopSpeaking();
    }

    @Override
    public void onLockMap(boolean arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onNaviViewLoaded() {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

}
