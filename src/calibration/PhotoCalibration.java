package calibration;

import imageProcessing.ImageProcessor;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import params.Params;
import ui.main.StartUI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static calibration.VideoCalibration.copyToPoint3Array;

public class PhotoCalibration {

    private static final int[] CHECKERBOARD = {6, 9};
    private static final List<Point3> objp = new ArrayList<>();
    private static final List<Mat> objectsp = new ArrayList<>();
    private static final List<Mat> imageLeftP = new ArrayList<>();
    private static final List<Mat> imageRightP = new ArrayList<>();
    private static VideoCalibration.LeftCameraStructure STRUCT_L;
    private static VideoCalibration.RightCameraStructure STRUCT_R;

    static {
        for (int i = 0; i < CHECKERBOARD[1]; i++) {
            for (int j = 0; j < CHECKERBOARD[0]; j++) {
                objp.add(new Point3(j, i,0));
            }
        }
    }

    public static void calibrate() throws InterruptedException, Exception {

        System.out.println(
                MessageFormat.format(
                        "{0} LOG: Старт процесса калибровки по существующим изображениям",
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
        );
        Path calImgL = Paths.get("resources/leftCalibration/defaultImages");
        Path calImgR = Paths.get("resources/rightCalibration/defaultImages");

        Mat grayImgL = new Mat();
        Mat grayImgR = new Mat();
        try (var streamL = Files.walk(calImgL, 1);
             var streamR = Files.walk(calImgR, 1);
        ) {

            var listL = streamL.collect(Collectors.toList());
            var listR = streamR.collect(Collectors.toList());

            var countMin = Math.min(listL.size(), listR.size());

            if (countMin < 10) {
                throw new Exception("Недостаточно изображений для каллибровки. Текущее количество: " + countMin + ". Необходимое количество: 10");
            }

            Mat imageL;
            Mat imageR;

            MatOfPoint2f corners_L = new MatOfPoint2f();
            MatOfPoint2f corners_R = new MatOfPoint2f();
            TermCriteria crit = null;

            for (int i = 1; i < countMin; i++) {
                imageL = Imgcodecs.imread("" + listL.get(i));
                imageR = Imgcodecs.imread("" + listR.get(i));

                Imgproc.resize(imageL, imageL, new Size(640, 480));
                Imgproc.resize(imageR, imageR, new Size(640, 480));

                Imgproc.cvtColor(imageL, grayImgL, Imgproc.COLOR_BGR2GRAY);
                Imgproc.cvtColor(imageR, grayImgR, Imgproc.COLOR_BGR2GRAY);

                var success_L = Calib3d.findChessboardCorners(grayImgL, new Size(CHECKERBOARD[0], CHECKERBOARD[1]), corners_L);
                var success_R = Calib3d.findChessboardCorners(grayImgR, new Size(CHECKERBOARD[0], CHECKERBOARD[1]), corners_R);

                if (success_L && success_R) {
                    crit = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.001);

                    Imgproc.cornerSubPix(grayImgL, corners_L, new Size(11, 11), new Size(-1, -1), crit);
                    Imgproc.cornerSubPix(grayImgR, corners_R, new Size(11, 11), new Size(-1, -1), crit);

                    Calib3d.drawChessboardCorners(imageL, new Size(CHECKERBOARD[0], CHECKERBOARD[1]), corners_L, success_L);
                    Calib3d.drawChessboardCorners(imageR, new Size(CHECKERBOARD[0], CHECKERBOARD[1]), corners_R, success_R);

                    objectsp.add(new MatOfPoint3f(copyToPoint3Array(objp)));
                    imageLeftP.add(corners_L);
                    imageRightP.add(corners_R);
                }
            }
        }

        var structL = calibrateCameraL(grayImgL);
        structL.setNew_mtxL(
                Calib3d.getOptimalNewCameraMatrix(
                        structL.getMtxL(),
                        structL.getDistL(),
                        grayImgL.size(),
                        1,
                        grayImgL.size(),
                        null,
                        false
                )
        );

        var structR = calibrateCameraR(grayImgR);
        structR.setNew_mtxR(
                Calib3d.getOptimalNewCameraMatrix(
                        structR.getMtxR(),
                        structR.getDistR(),
                        grayImgR.size(),
                        1,
                        grayImgR.size(),
                        null,
                        false
                )
        );

        STRUCT_L = structL;
        STRUCT_R = structR;
        Params.VID_CAL_STRUCT = new VideoCalibrationStructure(grayImgL, objectsp, imageLeftP, imageRightP, STRUCT_R, STRUCT_L);
        Params.CAM_STRUCT_L = STRUCT_L;
        Params.CAM_STRUCT_R = STRUCT_R;
    }

    public static VideoCalibration.LeftCameraStructure calibrateCameraL(Mat greyL) {
        Mat mxtL = new Mat();
        Mat distL = new Mat();
        List<Mat> R_L = new ArrayList<>();
        List<Mat> T_L = new ArrayList<>();

        Calib3d.calibrateCamera(objectsp, imageLeftP, greyL.size(), mxtL, distL, R_L, T_L);

        return new VideoCalibration.LeftCameraStructure(mxtL, distL, R_L, T_L);
    }

    public static VideoCalibration.RightCameraStructure calibrateCameraR(Mat greyR) {
        Mat mxtR = new Mat();
        Mat distR = new Mat();
        List<Mat> R_R = new ArrayList<>();
        List<Mat> T_R = new ArrayList<>();

        Calib3d.calibrateCamera(objectsp, imageRightP, greyR.size(), mxtR, distR, R_R, T_R);

        return new VideoCalibration.RightCameraStructure(mxtR, distR, R_R, T_R);
    }
}
