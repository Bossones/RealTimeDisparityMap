package calibration;

import org.opencv.core.Mat;

import java.util.List;


public class VideoCalibrationStructure {

    private Mat grayImagL;
    private List<Mat> objectsp;
    private List<Mat> imageLeftP;
    private List<Mat> imageRightP;
    private VideoCalibration.RightCameraStructure struct_r;
    private VideoCalibration.LeftCameraStructure struct_l;

    public VideoCalibrationStructure(Mat imageL, List<Mat> objectsp, List<Mat> imageLeftP, List<Mat> imageLeftR, VideoCalibration.RightCameraStructure struct_r, VideoCalibration.LeftCameraStructure struct_l) {
        this.grayImagL = imageL;
        this.objectsp = objectsp;
        this.imageLeftP = imageLeftP;
        this.imageRightP = imageLeftR;
        this.struct_r = struct_r;
        this.struct_l = struct_l;
    }

    public Mat getImageL() {
        return grayImagL;
    }

    public List<Mat> getObjectsp() {
        return objectsp;
    }

    public List<Mat> getImageLeftP() {
        return imageLeftP;
    }

    public List<Mat> getImageRightP() {
        return imageRightP;
    }

    public VideoCalibration.RightCameraStructure getStruct_r() {
        return struct_r;
    }

    public VideoCalibration.LeftCameraStructure getStruct_l() {
        return struct_l;
    }
}
