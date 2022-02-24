package ui.main.testDisp;

import imageProcessing.ImageProcessor;
import org.opencv.calib3d.StereoSGBM;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import ui.main.StartUI;

public class DisparityProcessing {

    public static void startDisp() {
        VideoCapture camLeft = new VideoCapture(1);
        VideoCapture camRight = new VideoCapture(2);

        Mat imgL = new Mat();
        Mat imgR = new Mat();
        Mat disp = new Mat();
        Mat to_display = new Mat();
        while (true) {
            camLeft.grab();
            camLeft.retrieve(imgL);
            imgL.convertTo(to_display, CvType.CV_8SC(1));
            StartUI.drawImage(ImageProcessor.toBufferedImage(to_display), 1);
            camRight.grab();
            camRight.retrieve(imgR);
            imgR.convertTo(to_display, CvType.CV_8SC(1));
            StartUI.drawImage(ImageProcessor.toBufferedImage(to_display), 2);

            var ster = StereoSGBM.create();


            ster.compute(imgL, imgR, disp);
            disp.convertTo(to_display, CvType.CV_8SC(1));
            StartUI.drawImage(ImageProcessor.toBufferedImage(to_display), 3);
        }
    }
}
