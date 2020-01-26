package com.example.deteccaodebordasemtemporeal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private JavaCameraView javaCameraView;
    private BaseLoaderCallback baseLoaderCallback;
    private  Mat matRgba;
    private  Mat matGray;
    private  Mat matCanny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpenCVLoader.initDebug();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                2);

        javaCameraView = (JavaCameraView) findViewById(R.id.cameraOpenCVID);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);


        baseLoaderCallback = new BaseLoaderCallback(this){
            @Override
            public void onManagerConnected(int status){

                switch (status){

                    case BaseLoaderCallback.SUCCESS:{
                        javaCameraView.enableView();
                        break;
                    }

                    default: {
                        super.onManagerConnected(status);
                        break;
                    }

                }

            }

        };

    }

    @Override
    protected void onPause(){
        super.onPause();

        if (javaCameraView!=null){
            javaCameraView.disableView();
        }
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (javaCameraView!=null){
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        if (OpenCVLoader.initDebug()){
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }else{
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION,this,baseLoaderCallback);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

        matRgba = new Mat(height,width, CvType.CV_8UC4);
        matGray = new Mat(height,width, CvType.CV_8UC1);
        matCanny = new Mat(height,width, CvType.CV_8UC1);

    }

    @Override
    public void onCameraViewStopped() {
        matRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        matRgba = inputFrame.rgba();
        Imgproc.cvtColor(matRgba, matGray, Imgproc.COLOR_RGB2GRAY);

        Imgproc.Canny(matGray,	matCanny,	100,	200);


        return matCanny;
    }
}
