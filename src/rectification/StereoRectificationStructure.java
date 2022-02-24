package rectification;

import calibration.StereoCalibrationStructure;
import org.opencv.core.Mat;

public class StereoRectificationStructure {
    private Mat rect_l, rect_r, proj_mat_l, proj_mat_r, Q;
    private StereoCalibrationStructure stereoCalibrationStructure;

    public StereoRectificationStructure(Mat rect_l, Mat rect_r, Mat proj_mat_l, Mat proj_mat_r, Mat q, StereoCalibrationStructure stereoCalibrationStructure) {
        this.rect_l = rect_l;
        this.rect_r = rect_r;
        this.proj_mat_l = proj_mat_l;
        this.proj_mat_r = proj_mat_r;
        Q = q;
        this.stereoCalibrationStructure = stereoCalibrationStructure;
    }

    public Mat getRect_l() {
        return rect_l;
    }

    public Mat getRect_r() {
        return rect_r;
    }

    public Mat getProj_mat_l() {
        return proj_mat_l;
    }

    public Mat getProj_mat_r() {
        return proj_mat_r;
    }

    public Mat getQ() {
        return Q;
    }

    public StereoCalibrationStructure getStereoCalibrationStructure() {
        return stereoCalibrationStructure;
    }
}
