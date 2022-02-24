package test;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class CalibTest {

    public static void main(String[] args) {

        var leftImage = Imgcodecs.imread("resources/left.jpg");
        var rightImage = Imgcodecs.imread("resources/right.jpg");

        var imgL_gray = new Mat();
        var imgR_gray = new Mat();

        Imgproc.cvtColor(leftImage, imgL_gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(rightImage, imgR_gray, Imgproc.COLOR_BGR2GRAY);

        var left_nice = new Mat();
        var right_nice = new Mat();



    }
}
