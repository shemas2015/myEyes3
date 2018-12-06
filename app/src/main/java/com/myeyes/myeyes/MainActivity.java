package com.myeyes.myeyes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
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
        Location location  = new Location(getApplicationContext());





        //Botón de capturar imagen
        final Button captura = findViewById(R.id.btnCaptura);
        captura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImgInfo();

                Map<String, String> postData = new HashMap<>();
                postData.put("tipo", "1");
                postData.put("longitud", "2");
                postData.put("latitud", "3");
                postData.put("usuario", "1");
                Server task = new Server(postData);
                task.execute("obstaculo/crear");
            }
        });


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

        if (bool)
            Toast.makeText(getApplicationContext(),"Imagen almacenada",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(),"Imagen no almacenada",Toast.LENGTH_LONG).show();
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
        mat1 = inputFrame.rgba();
        Mat Rgabt = mat1.t();
        Core.flip(mat1.t(),Rgabt,1);
        Imgproc.resize(Rgabt,Rgabt,mat1.size());
        Imgproc.cvtColor(Rgabt,gray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(gray,CannyImg,100,80);



        return CannyImg;





    }
}
