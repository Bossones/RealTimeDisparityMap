package system;

import calibration.PhotoCalibration;
import calibration.StereoCalibration;
import calibration.StereoCalibrationStructure;
import calibration.VideoCalibration;
import calibration.VideoCalibrationStructure;
import depthMap.DepthMap;
import depthMap.DepthMapStructure;
import imageProcessing.ImageProcessor;
import keyPontsDetection.KeyPointsDetection;
import keyPontsDetection.KeyPointsDetectionStructure;
import mapping.RemappingImages;
import mapping.UndistortionRectifyMap;
import matching.Matcher;
import matching.MatcherStructure;
import matrix.MatrixComputing;
import matrix.MatrixStructure;
import org.opencv.calib3d.Calib3d;
import org.opencv.calib3d.StereoBM;
import org.opencv.calib3d.StereoSGBM;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import params.Params;
import postProcessing.PostFilter;
import preProcessing.PreProcessing;
import preProcessing.PreProcessingStructure;
import rectification.Rectification;
import rectification.RectificationStructure;
import rectification.StereoRectification;
import rectification.StereoRectificationStructure;
import test.RunTestSystem;
import ui.main.StartUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Timer;
import java.util.stream.Collectors;

public class SystemApp {

    private static Mat leftImage = new Mat();
    private static Mat rightImage = new Mat();
    private static boolean stopSystem = false;

    private static SystemApp _this;

    private SystemApp() {}

    public synchronized static SystemApp create() {
        if (Objects.nonNull(_this)) {
            return _this;
        } else {
            return _this = new SystemApp();
        }
    }

    public static void startVideoCapture() throws Exception {
        VideoCapture leftCapture = new VideoCapture(1);
        VideoCapture rightCapture = new VideoCapture(2);

        stopSystem = false;
        while (!stopSystem){
            leftCapture.grab();
            rightCapture.grab();
            leftCapture.retrieve(leftImage);
            rightCapture.retrieve(rightImage);
//            var structure = startSystem(leftImage, leftImage);

            var structure = PreProcessing.startProcessingImages(leftImage, rightImage);

            StartUI.drawImage(ImageProcessor.toBufferedImage(structure.getLeftFilteredImage()), 1);
            StartUI.drawImage(ImageProcessor.toBufferedImage(structure.getRightFilteredImage()), 2);
            startWithCalibration(structure.getLeftFilteredImage(), structure.getRightFilteredImage());

//            var matrix = structure.getDepthMap();
//            matrix = PostFilter.getColoredDepthMap(matrix);
//            matrix.convertTo(matrix, CvType.CV_8SC(1));
        }
    }

    public static void startReadingViedoFromFileSystem() throws Exception {
        var pathLeft = Paths.get("resources/images/left");
        var pathRight = Paths.get("resources/images/right");

        try (var streamLeft = Files.walk(pathLeft, 1);
            var streamRight = Files.walk(pathRight, 1)) {

            var leftList = streamLeft.collect(Collectors.toList());
            var rightList = streamRight.collect(Collectors.toList());

            int size = Math.min(leftList.size(), rightList.size());

            for (int i = 1; i < size; i++) {
                leftImage = Imgcodecs.imread("" + leftList.get(i));
                rightImage = Imgcodecs.imread("" + rightList.get(i));



                var struct = PreProcessing.startProcessingImages(leftImage, rightImage);
                StartUI.drawImage(ImageProcessor.toBufferedImage(struct.getLeftFilteredImage()), 1);
                StartUI.drawImage(ImageProcessor.toBufferedImage(struct.getRightFilteredImage()), 2);

                var stereo = StereoBM.create(48, 11);
                stereo.setUniquenessRatio(60);
                var disp = new Mat();
                stereo.compute(struct.getLeftFilteredImage(), struct.getRightFilteredImage(), disp);
                Imgproc.medianBlur(disp, disp, 3);
                Core.normalize(disp, disp, 0, 255, Core.NORM_MINMAX);
                PostFilter.getColoredDepthMap(disp);
                Imgcodecs.imwrite("resources/justTest2.png", disp);
                var roDisp = new Mat();
                PostFilter.getColoredDepthMap(disp).convertTo(roDisp, CvType.CV_8SC(1), 1.0);
                StartUI.drawImage(ImageProcessor.toBufferedImage(roDisp), 3);

                Thread.sleep(30);
            }
        }
    }

    public DepthMapStructure startSystem(Mat leftImg, Mat rightImg) {

        var timeInit = System.currentTimeMillis();

/*        PreProcessingStructure preProc = new PreProcessing(leftImg, rightImg).startPreProcessing();
        KeyPointsDetectionStructure keyPointStructure = KeyPointsDetection
                .detectKeyPointsAndDescriptors(preProc.getLeftFilteredImage(), preProc.getRightFilteredImage());
        MatcherStructure matcherStructure = new Matcher().startKeyPointsMatching(keyPointStructure);
        MatrixStructure matrixStructure = new MatrixComputing().startComputingFundamentalMatrix(matcherStructure);
        RectificationStructure rectificationStructure = new Rectification(
                preProc.getLeftFilteredImage(),
                preProc.getRightFilteredImage()
        ).startRectification(matrixStructure);*/

        var timeEnd = System.currentTimeMillis();
        System.out.println("Время на кадр: " + (timeEnd - timeInit));

//        return new DepthMap().startComputingDepthMap(rectificationStructure);
        return null;
    }

    public static void startWithCalibration(Mat imageL, Mat imageR) throws Exception {
        Mat undistL = new Mat(), undistR = new Mat();

/*        Calib3d.undistort(imageL, undistL, Params.CAM_STRUCT_L.getMtxL(), Params.CAM_STRUCT_L.getDistL(), Params.CAM_STRUCT_L.getNew_mtxL());
        Calib3d.undistort(imageR, undistR, Params.CAM_STRUCT_R.getMtxR(), Params.CAM_STRUCT_R.getDistR(), Params.CAM_STRUCT_R.getNew_mtxR());*/

        RemappingImages.startRemappingImages(imageL, imageR);
        DepthMap.startComputingDisparityMap();
        var matrix = new Mat();
        var toDisp = new Mat();
        Imgcodecs.imwrite("resources/justTest.png", Params.DISPARITY_MAP);
        Imgproc.resize(Params.DISPARITY_MAP, matrix, new Size(680, 420));
        var colored = PostFilter.getColoredDepthMap(matrix);
        Imgcodecs.imwrite("resources/justTestColored.png", colored);

        matrix.convertTo(toDisp, CvType.CV_8SC(1), 1.0);
        StartUI.drawImage(ImageProcessor.toBufferedImage(toDisp), 3);
    }

    public static void stopSystem() {
        stopSystem = true;
    }
}
