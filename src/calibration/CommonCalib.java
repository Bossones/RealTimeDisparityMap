package calibration;

import mapping.UndistortionRectifyMap;
import params.Params;
import rectification.StereoRectification;

public class CommonCalib {

    public static void startProcessingAfterCalibration() throws Exception {
        StereoCalibration.startStereoCalibrate(Params.VID_CAL_STRUCT);
        StereoRectification.startStereoRectification(Params.STER_CAL_STRUCT);
        UndistortionRectifyMap.startUndistortionRectifyMapping(Params.STER_CAL_STRUCT.getImgRL(), Params.STER_CAL_STRUCT.getImgRL());
    }
}
