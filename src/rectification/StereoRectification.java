package rectification;

import calibration.StereoCalibrationStructure;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import params.Params;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StereoRectification {

    public static void startStereoRectification(StereoCalibrationStructure structCal) {
        System.out.println(
                MessageFormat.format(
                        "{0} LOG: Старт процесса стерео ректификации ____ {1}",
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "StereoRectification"));

        Mat rect_l = new Mat(), rect_r = new Mat(), proj_mat_l = new Mat(), proj_mat_r = new Mat(), Q = new Mat();

        Calib3d.stereoRectify(
                structCal.getLeftCameraStructure().getNew_mtxL(),
                structCal.getLeftCameraStructure().getDistL(),
                structCal.getRightCameraStructure().getNew_mtxR(),
                structCal.getRightCameraStructure().getDistR(),
                structCal.getImgRL().size(),
                structCal.getRot(),
                structCal.getTrns(),
                rect_l,
                rect_r,
                proj_mat_l,
                proj_mat_r,
                Q,
                1
        );

        Params.STER_RECT_STRUCT =  new StereoRectificationStructure(rect_l, rect_r, proj_mat_l, proj_mat_r, Q, structCal);
    }
}
