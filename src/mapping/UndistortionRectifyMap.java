package mapping;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import params.Params;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.isNull;

public class UndistortionRectifyMap {

    public static void startUndistortionRectifyMapping(Mat imageL, Mat imageR) throws Exception {

        System.out.println(
                MessageFormat.format(
                        "{0} LOG: Старт процесса уменьшения дисторсии и построении карты ____ {1}",
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "UndistortRectifyMap"));

        if (isNull(Params.CAM_STRUCT_L) || isNull(Params.STER_RECT_STRUCT) || isNull(Params.STER_CAL_STRUCT)) {
            throw new Exception("Отсутсвуют параметры калибровок после процессов StereoCalibration и StereoRectification");
        }

        Mat stereoMapL_X = new Mat(), stereoMapL_Y = new Mat();
        Mat stereoMapR_X = new Mat(), stereoMapR_Y = new Mat();

        Calib3d.initUndistortRectifyMap(
                Params.CAM_STRUCT_L.getNew_mtxL(),
                Params.CAM_STRUCT_L.getDistL(),
                Params.STER_RECT_STRUCT.getRect_l(),
                Params.STER_RECT_STRUCT.getProj_mat_l(),
                imageL.size(),
                CvType.CV_16SC2,
                stereoMapL_X,
                stereoMapL_Y
        );

        Calib3d.initUndistortRectifyMap(
                Params.CAM_STRUCT_R.getNew_mtxR(),
                Params.CAM_STRUCT_R.getDistR(),
                Params.STER_RECT_STRUCT.getRect_r(),
                Params.STER_RECT_STRUCT.getProj_mat_r(),
                imageR.size(),
                CvType.CV_16SC2,
                stereoMapR_X,
                stereoMapR_Y
        );

        Params.STEREO_MAP_L_X = stereoMapL_X;
        Params.STEREO_MAP_L_Y = stereoMapL_Y;
        Params.STEREO_MAP_R_X = stereoMapR_X;
        Params.STEREO_MAP_R_Y = stereoMapR_Y;
    }
}
