package calibration;

import org.opencv.core.Mat;

public class StereoCalibrationStructure {

    private Mat imgRL;
    private Mat Rot, Trns, Emat, Fmat;
    private VideoCalibration.LeftCameraStructure leftCameraStructure;
    private VideoCalibration.RightCameraStructure rightCameraStructure;

    public StereoCalibrationStructure(Mat rot, Mat trns, Mat emat, Mat fmat, VideoCalibration.LeftCameraStructure leftCameraStructure, VideoCalibration.RightCameraStructure rightCameraStructure) {
        Rot = rot;
        Trns = trns;
        Emat = emat;
        Fmat = fmat;
        this.leftCameraStructure = leftCameraStructure;
        this.rightCameraStructure = rightCameraStructure;
    }

    public StereoCalibrationStructure(Mat rot, Mat trns, Mat emat, Mat fmat) {
        Rot = rot;
        Trns = trns;
        Emat = emat;
        Fmat = fmat;
    }

    public StereoCalibrationStructure(Mat imgRL, Mat rot, Mat trns, Mat emat, Mat fmat, VideoCalibration.LeftCameraStructure leftCameraStructure, VideoCalibration.RightCameraStructure rightCameraStructure) {
        this.imgRL = imgRL;
        Rot = rot;
        Trns = trns;
        Emat = emat;
        Fmat = fmat;
        this.leftCameraStructure = leftCameraStructure;
        this.rightCameraStructure = rightCameraStructure;
    }

    public Mat getRot() {
        return Rot;
    }

    public Mat getTrns() {
        return Trns;
    }

    public Mat getEmat() {
        return Emat;
    }

    public Mat getFmat() {
        return Fmat;
    }

    public VideoCalibration.LeftCameraStructure getLeftCameraStructure() {
        return leftCameraStructure;
    }

    public VideoCalibration.RightCameraStructure getRightCameraStructure() {
        return rightCameraStructure;
    }

    public Mat getImgRL() {
        return imgRL;
    }
}
