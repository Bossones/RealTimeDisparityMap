package calibration;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import params.Params;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StereoCalibration {

    public static void startStereoCalibrate(VideoCalibrationStructure vidStruct) {
        System.out.println(
                MessageFormat.format(
                        "{0} LOG: Старт процесса стерео калибровки ____ {1}",
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "StereoCalibration"));

        Mat     Rot = new Mat(),
                Trns = new Mat(),
                Emat = new Mat(),
                Fmat = new Mat();

        Calib3d.stereoCalibrate(
                vidStruct.getObjectsp(),
                vidStruct.getImageLeftP(),
                vidStruct.getImageRightP(),
                vidStruct.getStruct_l().getNew_mtxL(),
                vidStruct.getStruct_l().getDistL(),
                vidStruct.getStruct_r().getNew_mtxR(),
                vidStruct.getStruct_r().getDistR(),
                vidStruct.getImageL().size(),
                Rot,
                Trns,
                Emat,
                Fmat,
                Calib3d.CALIB_FIX_INTRINSIC,
                new TermCriteria(TermCriteria.MAX_ITER + TermCriteria.EPS, 30, 1e-6)
        );

        Params.STER_CAL_STRUCT = new StereoCalibrationStructure(vidStruct.getImageL(), Rot, Trns, Emat, Fmat, vidStruct.getStruct_l(), vidStruct.getStruct_r());
    }
}
