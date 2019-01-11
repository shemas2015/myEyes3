package com.myeyes.myeyes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
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
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {


    CameraBridgeViewBase cameraBridgeViewBase;
    Mat mat1, mat2, mat3;
    Scalar scalarLow, scalarHight;


    BaseLoaderCallback baseLoaderCallback;
    private Mat gray;
    private Mat CannyImg;

    private LocationManager locationManager1;
    private LocationListener locationListener;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraBridgeViewBase = (JavaCameraView) findViewById(R.id.myCameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);

        scalarLow = new Scalar(45, 20, 10);
        scalarHight = new Scalar(75, 255, 255);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        //GPS location
        final Location location  = new Location(getApplicationContext());

        Db db = new Db(getApplicationContext());
        final SQLiteDatabase dbs = db.getWritableDatabase();

        //Inicia el usuario por defecto
        //---------------------------------------------------------------
        //El módulo de creación de usuario se implementará en la ejecución del resto del proyecto
        //---------------------------------------------------------------
         try{
             dbs.execSQL("insert into usuario values (1,'Sebastián')");
         }catch (Exception e){}








        baseLoaderCallback = new BaseLoaderCallback(    this) {
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
        switch (requestCode){
            case 10:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                    Toast.makeText(getApplicationContext(),"Listo el gps",Toast.LENGTH_LONG).show();
        }

    }


    public void saveImgInfo(){

        /*
        //Mat mIntermediateMat = new Mat();


        Mat Rgabt = mat1.t();
        Core.flip(mat1.t(),Rgabt,1);
        Imgproc.resize(Rgabt,Rgabt,mat1.size());
        Imgproc.cvtColor(Rgabt,gray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(gray,CannyImg,100,80);

        File path = new File(Environment.getExternalStorageDirectory() + "/Images/");
        path.mkdirs();
        File file = new File(path, "image.png");

        String filename = file.toString();
        Boolean bool = Highgui.imwrite(filename, gray);
        */

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


        /*

        //Camnny
        mat1 = inputFrame.rgba();
        Mat Rgabt = mat1.t();
        Core.flip(mat1.t(),Rgabt,1);
        Imgproc.resize(Rgabt,Rgabt,mat1.size());
        Imgproc.cvtColor(Rgabt,gray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(gray,CannyImg,100,80);
        return CannyImg;
        */



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


        // accumulator value
        double dp = 1.2d;
        // minimum distance between the center coordinates of detected circles in pixels
        double minDist = 20;

        // min and max radii (set these values as you desire)
        int minRadius = 0, maxRadius = 0;
        double param1 = 70, param2 = 72;




        Bitmap bitmap = null;
        Mat tmp = new Mat (mat1.rows(), mat1.cols(), CvType.CV_8U, new Scalar(4));
        try {
            //Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
            bitmap = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(tmp, bitmap);
        }
        catch (CvException ex){ System.out.println("Errors: "+ ex.getMessage());}


        Mat circles = new Mat(bitmap.getWidth(),
                bitmap.getHeight(), CvType.CV_8UC1);

        /* find the circle in the image */
        Imgproc.HoughCircles(gray, circles,
                Imgproc.CV_HOUGH_GRADIENT, dp, minDist, param1,
                param2, minRadius, maxRadius);

        /* get the number of circles detected */
        int numberOfCircles = (circles.rows() == 0) ? 0 : circles.cols();

        /* draw the circles found on the image */
        for (int i=0; i<numberOfCircles; i++) {
            System.out.println("Errors: we");

            /* get the circle details, circleCoordinates[0, 1, 2] = (x,y,r)
             * (x,y) are the coordinates of the circle's center
             */
            double[] circleCoordinates = circles.get(0, i);


            int x = (int) circleCoordinates[0], y = (int) circleCoordinates[1];

            Point center = new Point(x, y);

            int radius = (int) circleCoordinates[2];

            /* circle's outline */
            Core.circle(mat1, center, radius, new Scalar(0,
                    255, 0), 4);

            /* circle's center outline */
            Core.rectangle(mat1, new Point(x - 5, y - 5),
                    new Point(x + 5, y + 5),
                    new Scalar(0, 128, 255), -1);
        }

        return mat1;






    }
}
