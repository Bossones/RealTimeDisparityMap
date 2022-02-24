package preProcessing;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class PreProcessing {

    private Mat leftImage;
    private Mat rightImage;
    private Mat leftGrayFilteredImg;
    private Mat rightGrayFilteredImg;

    public PreProcessing(Mat leftImage, Mat rightImage) {
        this.leftImage = leftImage;
        this.rightImage = rightImage;
        this.leftGrayFilteredImg = new Mat();
        this.rightGrayFilteredImg = new Mat();
    }

    public PreProcessingStructure startPreProcessing() {

        var resizedLeftImage = new Mat();
        var resizedRightImage = new Mat();
        var greyImageLeft = new Mat();
        var gaussianBlurImageLeft = new Mat();
        var greyImageRight = new Mat();
        var gaussianBlurImageRight = new Mat();



//        Imgproc.pyrDown(leftImage, leftImage);
        Imgproc.cvtColor(leftImage, greyImageLeft, Imgproc.COLOR_BGR2GRAY);
/*        Imgproc.GaussianBlur(greyImageLeft, gaussianBlurImageLeft, new Size(3, 3), 0);
        Imgproc.medianBlur(gaussianBlurImageLeft, leftGrayFilteredImg, 3);*/

//        Imgproc.pyrDown(rightImage, rightImage);
        Imgproc.cvtColor(rightImage, greyImageRight, Imgproc.COLOR_BGR2GRAY);
/*        Imgproc.GaussianBlur(greyImageRight, gaussianBlurImageRight, new Size(3, 3), 0);
        Imgproc.medianBlur(gaussianBlurImageRight, rightGrayFilteredImg, 3);*/

        return new PreProcessingStructure(greyImageLeft, greyImageRight);
    }

    public static PreProcessingStructure startProcessingImages(Mat imageL, Mat imageR) {

        Mat grayL = new Mat(), grayR = new Mat();

/*        Imgproc.resize(imageL, imageL, new Size(640, 480));
        Imgproc.resize(imageR, imageR, new Size(640, 480));*/

        Imgproc.cvtColor(imageL, grayL, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(imageR, grayR, Imgproc.COLOR_BGR2GRAY);

/*        Imgproc.GaussianBlur(grayL, grayL, new Size(3, 3), 0);
        Imgproc.GaussianBlur(grayR, grayR, new Size(3, 3), 0);*/

        return new PreProcessingStructure(grayL, grayR);
    }
}
