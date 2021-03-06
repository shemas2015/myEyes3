package com.myeyes.myeyes;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class OpenCvStorage extends Imagen {

    /**
     *
     * @param context
     */
    Context contexto  = null;
    LoaderCallbackInterface mLoaderCallback = null;
    BaseLoaderCallback baseLoaderCallback;

    public OpenCvStorage(Context applicationContext, Context context){

        this.contexto = context;
    }

    public void iniciar() {
        baseLoaderCallback = new BaseLoaderCallback(this.contexto) {
            @Override
            public void onManagerConnected(int status) {

                switch (status){
                    case BaseLoaderCallback.SUCCESS:
                        //cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };

        if(!OpenCVLoader.initDebug()){
            Toast.makeText(this.contexto,"Hay un problema con OpenCV",Toast.LENGTH_LONG).show();

        }else{
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }


        // accumulator value
        double dp = 1.2d;
        // minimum distance between the center coordinates of detected circles in pixels
        double minDist = 20;

        // min and max radii (set these values as you desire)
        int minRadius = 0, maxRadius = 0;
        double param1 = 70, param2 = 72;





        Mat mat1 =null;
        try{
            Bitmap bmp = BitmapFactory.decodeFile("/sdcard/ojos.jpg");

            mat1 = new Mat (bmp.getHeight(), bmp.getWidth(), CvType.CV_8U , new Scalar(4));


            Utils.bitmapToMat(bmp, mat1);

        }catch (Exception ex){
            System.out.println("Error: "+ex.getMessage());
            System.out.println("Error: "+ex.toString());
        }








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

        try{
            Mat mRgba2 = new Mat (mat1.rows(), mat1.cols(), CvType.CV_8U, new Scalar(4));
            Imgproc.cvtColor(mat1, mRgba2, Imgproc.COLOR_BGR2GRAY, 0);
        // find the circle in the image
        Imgproc.HoughCircles(mRgba2, circles,
                Imgproc.CV_HOUGH_GRADIENT, dp, minDist, param1,
                param2, minRadius, maxRadius);
        }catch (Exception err){
            System.out.println("Error: "+err.getMessage());
        }


        // get the number of circles detected
        int numberOfCircles = (circles.rows() == 0) ? 0 : circles.cols();


        // draw the circles found on the image
        for (int i=0; i<numberOfCircles; i++) {
            System.out.println("Errors: we");

            // get the circle details, circleCoordinates[0, 1, 2] = (x,y,r)
            //(x,y) are the coordinates of the circle's center

            double[] circleCoordinates = circles.get(0, i);


            int x = (int) circleCoordinates[0], y = (int) circleCoordinates[1];

            Point center = new Point(x, y);

            int radius = (int) circleCoordinates[2];

            // circle's outline
            Core.circle(mat1, center, radius, new Scalar(0,
                    255, 0), 4);

            // circle's center outline
            Core.rectangle(mat1, new Point(x - 5, y - 5),
                    new Point(x + 5, y + 5),
                    new Scalar(0, 128, 255), -1);
        }
        SaveImage(mat1);
    }

}
