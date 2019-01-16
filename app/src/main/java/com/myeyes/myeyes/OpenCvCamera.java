package com.myeyes.myeyes;

import android.graphics.Bitmap;
import android.view.SurfaceView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class OpenCvCamera extends Imagen  implements CameraBridgeViewBase.CvCameraViewListener2  {

    CameraBridgeViewBase cameraBridgeViewBase;
    Mat mat1, mat2, mat3;
    Scalar scalarLow, scalarHight;
    MainActivity mainActivity = null;


    BaseLoaderCallback baseLoaderCallback;
    private Mat gray;
    private Mat CannyImg;

    public OpenCvCamera(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

    }


    public void iniciar(){

        cameraBridgeViewBase = (JavaCameraView) this.mainActivity.findViewById(R.id.myCameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);

        scalarLow = new Scalar(45, 20, 10);
        scalarHight = new Scalar(75, 255, 255);
        cameraBridgeViewBase.setCvCameraViewListener(this);


        baseLoaderCallback = new BaseLoaderCallback(    this.mainActivity) {
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

        if(!OpenCVLoader.initDebug()){
            Toast.makeText(this.mainActivity.getApplicationContext(),"Hay un problema con OpenCV",Toast.LENGTH_LONG).show();

        }else{
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
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
        //Camnny
        mat1 = inputFrame.rgba();

        Mat Rgabt = mat1.t();
        Core.flip(mat1.t(),Rgabt,1);
        Imgproc.resize(Rgabt,Rgabt,mat1.size());
        Imgproc.cvtColor(Rgabt,gray,Imgproc.COLOR_BGR2GRAY);


        //Rota el marco 90 grados
        Core.transpose(mat1,mat2);
        Imgproc.resize(mat2,mat3,mat3.size(),0,0,0);
        Core.flip(mat3,mat1,1);

        /* reduce the noise so we avoid false circle detection */
        Imgproc.GaussianBlur(gray, gray, new Size(9, 9), 2, 2);


        return this.getCircles(mat1,gray);


    }
}
