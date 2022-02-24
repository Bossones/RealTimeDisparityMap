package params;

import calibration.StereoCalibrationStructure;
import calibration.VideoCalibration;
import calibration.VideoCalibrationStructure;
import org.opencv.core.Mat;
import rectification.StereoRectificationStructure;

public class Params {

    public static VideoCalibrationStructure VID_CAL_STRUCT;
    public static StereoCalibrationStructure STER_CAL_STRUCT;
    public static StereoRectificationStructure STER_RECT_STRUCT;
    public static VideoCalibration.RightCameraStructure CAM_STRUCT_R;
    public static VideoCalibration.LeftCameraStructure CAM_STRUCT_L;
    public static Mat STEREO_MAP_L_X;
    public static Mat STEREO_MAP_L_Y;
    public static Mat STEREO_MAP_R_X;
    public static Mat STEREO_MAP_R_Y;
    public static Mat BEST_LEFT;
    public static Mat BEST_RIGHT;
    public static Mat DISPARITY_MAP;
}
