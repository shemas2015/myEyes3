package com.myeyes.myeyes;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {


    CameraBridgeViewBase cameraBridgeViewBase;
    Mat mat1,mat2,mat3;
    Scalar scalarLow,scalarHight;


    BaseLoaderCallback baseLoaderCallback;
    private Mat gray;
    private Mat CannyImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.myCameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);

        scalarLow = new Scalar(45,20,10);
        scalarHight = new Scalar(75,255,255);
        cameraBridgeViewBase.setCvCameraViewListener(this);


        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status){
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }

        };


    }

    @Override
    public void onPause() {
        super.onPause();

        if(cameraBridgeViewBase != null){
            cameraBridgeViewBase.disableView();
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        if(!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"Hay un problema con OpenCV",Toast.LENGTH_LONG).show();

        }else{
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }

    }


    public void onDestroy() {
        super.onDestroy();

        if(cameraBridgeViewBase != null){
            cameraBridgeViewBase.disableView();
        }

    }


    @Override
    public void onCameraViewStarted(int width, int height) {

        mat1 = new Mat(width,height,CvType.CV_8UC4);
        mat2 = new Mat(width,height,CvType.CV_8UC4);
        mat3 = new Mat(width,height,CvType.CV_8UC4);
        gray = new Mat(width,height,CvType.CV_8UC1);
        CannyImg = new Mat(width,height,CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mat1.release();
        mat2.release();
        mat3.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mat1 = inputFrame.rgba();

        /*
        //Visualización normal
        Imgproc.cvtColor(inputFrame.rgba(),mat1,Imgproc.COLOR_BGR2HSV);
        Core.inRange(mat1,scalarLow,scalarHight,mat2);
        //Rota el marco 90 grados
        Core.transpose(mat1,mat2);
        Imgproc.resize(mat2,mat3,mat3.size(),0,0,0);
        Core.flip(mat3,mat1,1);


        return mat1;
        */


        //Camnny
        Mat Rgabt = mat1.t();
        Core.flip(mat1.t(),Rgabt,1);
        Imgproc.resize(Rgabt,Rgabt,mat1.size());
        Imgproc.cvtColor(Rgabt,gray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(gray,CannyImg,100,80);
        return CannyImg;





    }
}
