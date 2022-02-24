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
import org.opencv.videoio.VideoCapture;
import params.Params;
import ui.main.StartUI;

import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VideoCalibration {

    public static final int[] CHECKERBOARD = {6, 9};
    private static final List<Point3> objp = new ArrayList<>();
    private static final List<Mat> objectsp = new ArrayList<>();
    private static final List<Mat> imageLeftP = new ArrayList<>();
    private static final List<Mat> imageRightP = new ArrayList<>();
    private static LeftCameraStructure STRUCT_L;
    private static RightCameraStructure STRUCT_R;

    static {
        for (int i = 0; i < CHECKERBOARD[1]; i++) {
            for (int j = 0; j < CHECKERBOARD[0]; j++) {
                objp.add(new Point3(j, i,0));
            }
        }
    }

    public static class LeftCameraStructure implements Serializable {

        private Mat mtxL;
        private Mat distL;
        private List<Mat> R_L;
        private List<Mat> T_L;
        private Mat new_mtxL;

        public LeftCameraStructure(Mat mtxL, Mat distL, List<Mat> r_L, List<Mat> t_L) {
            this.mtxL = mtxL;
            this.distL = distL;
            R_L = r_L;
            T_L = t_L;
        }

        public Mat getMtxL() {
            return mtxL;
        }

        public Mat getDistL() {
            return distL;
        }

        public List<Mat> getR_L() {
            return R_L;
        }

        public List<Mat> getT_L() {
            return T_L;
        }

        public Mat getNew_mtxL() {
            return new_mtxL;
        }

        public void setNew_mtxL(Mat new_mtxL) {
            this.new_mtxL = new_mtxL;
        }
    }

    public static class RightCameraStructure implements Serializable {
        private Mat mtxR;
        private Mat distR;
        private List<Mat> R_R;
        private List<Mat> T_R;
        private Mat new_mtxR;

        public RightCameraStructure(Mat mtxR, Mat distR, List<Mat> r_R, List<Mat> t_R) {
            this.mtxR = mtxR;
            this.distR = distR;
            R_R = r_R;
            T_R = t_R;
        }

        public Mat getMtxR() {
            return mtxR;
        }

        public Mat getDistR() {
            return distR;
        }

        public List<Mat> getR_R() {
            return R_R;
        }

        public List<Mat> getT_R() {
            return T_R;
        }

        public Mat getNew_mtxR() {
            return new_mtxR;
        }

        public void setNew_mtxR(Mat new_mtxR) {
            this.new_mtxR = new_mtxR;
        }
    }

    public static void calibrate() throws InterruptedException, IOException {

        System.out.println(
                MessageFormat.format(
                        "{0} LOG: Старт процесса калибровки по видео захвату",
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
        );

        VideoCapture videoCaptureL = new VideoCapture(1);
        VideoCapture videoCaptureR = new VideoCapture(2);

        Mat imageL = new Mat();
        Mat imageR = new Mat();

        Mat grayImgL = new Mat();
        Mat grayImgR = new Mat();

        MatOfPoint2f corners_L = new MatOfPoint2f();
        MatOfPoint2f corners_R = new MatOfPoint2f();
        TermCriteria crit = null;
        int counterImages = 10;
        int counterTime = 100;
        int countI = 0;
        int countT = 0;

        while (true) {
            videoCaptureL.grab();
            videoCaptureL.retrieve(imageL);
            videoCaptureR.grab();
            videoCaptureR.retrieve(imageR);

            StartUI.drawImage(ImageProcessor.toBufferedImage(imageL), 1);
            StartUI.drawImage(ImageProcessor.toBufferedImage(imageR), 2);

            if (countT <= counterTime) {countT++; continue;} else {countT = 0;}
            if (countI == counterImages) break;

            System.out.println("Захват кадра");

            Imgcodecs.imwrite(MessageFormat.format("resources/leftCalibration/defaultImages/{0}.png", countI), imageL);
            Imgcodecs.imwrite(MessageFormat.format("resources/rightCalibration/defaultImages/{0}.png", countI), imageR);

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

                Imgcodecs.imwrite(MessageFormat.format("resources/leftCalibration/withChessCorners/{0}.png", countI), imageL);
                Imgcodecs.imwrite(MessageFormat.format("resources/rightCalibration/withChessCorners/{0}.png", countI), imageR);

                objectsp.add(new MatOfPoint3f(copyToPoint3Array(objp)));
                imageLeftP.add(corners_L);
                imageRightP.add(corners_R);
                countI++;
            }

        }

        var structL = calibrateCameraL(grayImgL);
        structL.setNew_mtxL(
                Calib3d.getOptimalNewCameraMatrix(
                        structL.mtxL,
                        structL.distL,
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
                        structR.mtxR,
                        structR.distR,
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

    public static LeftCameraStructure calibrateCameraL(Mat greyL) {
        Mat mxtL = new Mat();
        Mat distL = new Mat();
        List<Mat> R_L = new ArrayList<>();
        List<Mat> T_L = new ArrayList<>();

        Calib3d.calibrateCamera(objectsp, imageLeftP, greyL.size(), mxtL, distL, R_L, T_L);

        return new LeftCameraStructure(mxtL, distL, R_L, T_L);
    }

    public static RightCameraStructure calibrateCameraR(Mat greyR) {
        Mat mxtR = new Mat();
        Mat distR = new Mat();
        List<Mat> R_R = new ArrayList<>();
        List<Mat> T_R = new ArrayList<>();

        Calib3d.calibrateCamera(objectsp, imageRightP, greyR.size(), mxtR, distR, R_R, T_R);

        return new RightCameraStructure(mxtR, distR, R_R, T_R);
    }

    public static Point3[] copyToPoint3Array(List<Point3> point3List) {
        var pointArr = new Point3[point3List.size()];
        for (int i = 0; i < point3List.size(); i++) {
            pointArr[i] = point3List.get(i).clone();
        }
        return pointArr;
    }
}
