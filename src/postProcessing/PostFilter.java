package postProcessing;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class PostFilter {

    private PostFilter() {}

    private static Mat getFilteredDisparityMap(Mat disparityMap) {
        var erode = new Mat();
        var dilate = new Mat();
        Imgproc.erode(disparityMap, erode, Mat.ones(new Size(3, 3), CvType.CV_8UC(1)));
        Imgproc.dilate(erode, dilate, Mat.ones(new Size(3, 3), CvType.CV_8UC(1)));
        return dilate;
    }

    private static Mat getColoredDisparityMap(Mat bilateralDisparityMap) {

        var colored = new Mat();
        bilateralDisparityMap.convertTo(colored, CvType.CV_8UC(1));
        Imgproc.applyColorMap(colored, colored, Imgproc.COLORMAP_JET);

        return colored;
    }

    public static Mat getColoredDepthMap(Mat depthMap) {
        return getColoredDisparityMap(getFilteredDisparityMap(depthMap));
    }
}
