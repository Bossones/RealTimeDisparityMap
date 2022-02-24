package oneFileCode;

import imageProcessing.ImageProcessor;
import org.opencv.calib3d.Calib3d;
import org.opencv.calib3d.StereoBM;
import org.opencv.calib3d.StereoSGBM;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import ui.main.StartUI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static utils.Utils.createCalibMatFile;
import static utils.Utils.parseMatrix;
import static utils.Utils.printMat;

public class OneFileClass {
    static MatOfPoint3f obj = new MatOfPoint3f();
    static List<Mat> objectPoints = new ArrayList<>();
    static List<Mat> pointsL = new ArrayList<>();
    static List<Mat> pointsR = new ArrayList<>();
    static MatOfPoint2f cornersL = new MatOfPoint2f();
    static MatOfPoint2f cornersR = new MatOfPoint2f();
    static final int[] CHECKERBOARD = {9, 6};
    static Size boardSize = new Size(9, 6);
    static Mat imgL = new Mat();
    static Mat imgR = new Mat();
    static Mat imgGrayL = new Mat();
    static Mat imgGrayR = new Mat();
    static TermCriteria termCriteria = new TermCriteria(TermCriteria.EPS, 30, 0.1);


    static List<Mat> r_R = new ArrayList<>();
    static List<Mat> r_L = new ArrayList<>();
    static List<Mat> t_R = new ArrayList<>();
    static List<Mat> t_L = new ArrayList<>();
/*    static Mat mxtL = new Mat(3, 3, CvType.CV_32FC1);
    static Mat mxtR = new Mat(3, 3, CvType.CV_32FC1);*/
    static Mat mxtL = new Mat();
    static Mat mxtR = new Mat();
    static Mat new_mxtL = new Mat();
    static Mat new_mxtR = new Mat();
    static Mat distR = new Mat();
    static Mat distL = new Mat();

    static Mat Rot = new Mat();
    static Mat Trns = new Mat();
    static Mat Emat = new Mat();
    static Mat Fmat = new Mat();

    static Mat rectL = new Mat();
    static Mat rectR = new Mat();
    static Mat projMatL = new Mat();
    static Mat projMatR = new Mat();
    static Mat Q = new Mat();

    static Mat leftMapX = new Mat();
    static Mat leftMapY = new Mat();
    static Mat rightMapX = new Mat();
    static Mat rightMapY = new Mat();

    static int maxDisparity = 128;
    static int blockSize = 11;
    static int preFilterType = 1;
    static int preFilterSize = 1;
    static int preFilterCap = 4;
    static int minDisparity = -128;
    static int textureThreshold = 10;
    static int uniquenessRatio = 5;
    static int speckleRange = 2;
    static int speckleWindowSize = 200;
    static int disp12MaxDiff = 0;
    static int dispType = CvType.CV_16S;
    static Mat disparityMap = new Mat();
    static double objectSize = 100;
    static Size sizeOfPhoto;
    static int numDisparities = maxDisparity - minDisparity;

    static {
        updateSettings();
    }


    public static void updateSettings() {
        for (int i = 0; i < CHECKERBOARD[1]; i++) {
            for (int j = 0; j < CHECKERBOARD[0]; j++) {
                obj.push_back(new MatOfPoint3f(new Point3(j, i,0)));
            }
        }
    }

    public static void startPhotoCalibration() throws IOException {

        try (Stream<Path> streamL = Files.walk(Paths.get("resources/leftCalibration/defaultImages"), 1);
             Stream<Path> streamR = Files.walk(Paths.get("resources/rightCalibration/defaultImages"), 1)
        ){
            var listL = streamL.collect(Collectors.toList());
            var listR = streamR.collect(Collectors.toList());

            var min = Math.min(listL.size(), listR.size());

            for (int i = 1; i < min; i++) {
                imgL = Imgcodecs.imread("" + listL.get(i));
                imgR = Imgcodecs.imread("" + listR.get(i));

                if (sizeOfPhoto == null || sizeOfPhoto.empty()) {
                    sizeOfPhoto = imgL.size();
                }

/*                Imgproc.cvtColor(imgL, imgGrayL, Imgproc.COLOR_BGR2GRAY);
                Imgproc.cvtColor(imgR, imgGrayR, Imgproc.COLOR_BGR2GRAY);*/

                boolean foundL = Calib3d.findChessboardCorners(
                        imgL,
                        boardSize,
                        cornersL,
                        Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE
                );

                boolean foundR = Calib3d.findChessboardCorners(
                        imgR,
                        boardSize,
                        cornersR,
                        Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE
                );

                if (foundL && foundR) {
                    Mat greyL = new Mat();
                    Mat greyR = new Mat();

                    Imgproc.cvtColor(imgL, greyL, Imgproc.COLOR_BGR2GRAY);
                    Imgproc.cvtColor(imgR, greyR, Imgproc.COLOR_BGR2GRAY);

                    Imgproc.cornerSubPix(greyL, cornersL, new Size(11, 11), new Size(-1, -1), termCriteria);
                    Imgproc.cornerSubPix(greyR, cornersR, new Size(11, 11), new Size(-1, -1), termCriteria);

/*                    Calib3d.drawChessboardCorners(imgL, boardSize, cornersL, foundL);
                    Calib3d.drawChessboardCorners(imgR, boardSize, cornersR, foundR);

                    Imgcodecs.imwrite(MessageFormat.format("resources/leftCalibration/withChessCorners/{0}.png", i), imgL);
                    Imgcodecs.imwrite(MessageFormat.format("resources/rightCalibration/withChessCorners/{0}.png", i), imgR);*/

                    pointsL.add(cornersL);
                    pointsR.add(cornersR);
                    objectPoints.add(obj);
                    greyL.release();
                    greyR.release();
                } else {
                    cornersL.release();
                    cornersR.release();
                }
                imgL.release();
                imgR.release();
            }
        }
    }

    public static void createVideoImagesForCalibrating() {
        VideoCapture camL = new VideoCapture(1);
        VideoCapture camR = new VideoCapture(2);
        int counI = 0;
        int countT = 0;
        int amountI = 13;
        int amountT = 100;

        Mat toDispL = new Mat();
        Mat toDispR = new Mat();


        while (true) {
            camL.grab();
            camR.grab();
            camL.retrieve(imgL);
            camR.retrieve(imgR);

            if (sizeOfPhoto == null || sizeOfPhoto.empty()) {
                sizeOfPhoto = imgL.size();
            }

            Imgproc.cvtColor(imgL, imgGrayL, Imgproc.COLOR_BGR2GRAY);
            Imgproc.cvtColor(imgR, imgGrayR, Imgproc.COLOR_BGR2GRAY);

            imgL.convertTo(toDispL, CvType.CV_8SC(1));
            StartUI.drawImage(ImageProcessor.toBufferedImage(toDispL), 1);
            imgR.convertTo(toDispR, CvType.CV_8SC(1));
            StartUI.drawImage(ImageProcessor.toBufferedImage(toDispR), 2);

            if (countT < amountT) {
                countT++;
                continue;
            } else {
                countT = 0;
            }

            if (counI < amountI) {
                Imgcodecs.imwrite(MessageFormat.format("resources/leftCalibration/defaultImages/{0}.png", counI), imgL);
                Imgcodecs.imwrite(MessageFormat.format("resources/rightCalibration/defaultImages/{0}.png", counI), imgR);

                boolean foundL = Calib3d.findChessboardCorners(
                        imgGrayL,
                        boardSize,
                        cornersL,
                        Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE
                );

                boolean foundR = Calib3d.findChessboardCorners(
                        imgGrayR,
                        boardSize,
                        cornersR,
                        Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE
                );

                if (foundL && foundR) {
                    Imgproc.cornerSubPix(imgGrayL, cornersL, new Size(11, 11), new Size(-1, -1), termCriteria);
                    Imgproc.cornerSubPix(imgGrayR, cornersR, new Size(11, 11), new Size(-1, -1), termCriteria);

                    Calib3d.drawChessboardCorners(imgL, boardSize, cornersL, foundL);
                    Calib3d.drawChessboardCorners(imgR, boardSize, cornersR, foundR);

                    Imgcodecs.imwrite(MessageFormat.format("resources/leftCalibration/withChessCorners/{0}.png", counI), imgL);
                    Imgcodecs.imwrite(MessageFormat.format("resources/rightCalibration/withChessCorners/{0}.png", counI), imgR);

                    pointsL.add(cornersL);
                    pointsR.add(cornersR);
                    objectPoints.add(obj);
                    counI++;
                }

            } else {
                break;
            }

        }
    }

    public static void calibrateCamerasFromFile() {
        Calib3d.stereoRectify(
                new_mxtL,
                distL,
                new_mxtR,
                distR,
                sizeOfPhoto,
                Rot,
                Trns,
                rectL,
                rectR,
                projMatL,
                projMatR,
                Q
        );

        Calib3d.initUndistortRectifyMap(
                new_mxtL,
                distL,
                rectL,
                projMatL,
                sizeOfPhoto,
                CvType.CV_16SC2,
                leftMapX,
                leftMapY
        );

        Calib3d.initUndistortRectifyMap(
                new_mxtR,
                distR,
                rectR,
                projMatR,
                sizeOfPhoto,
                CvType.CV_16SC2,
                rightMapX,
                rightMapY
        );

        printAllParameters();
    }

    public static void calibrateCameras() throws IOException {
        TermCriteria term = new TermCriteria(TermCriteria.MAX_ITER + TermCriteria.EPS, 30, 1e-6);
        Calib3d.calibrateCamera(objectPoints, pointsL, sizeOfPhoto, mxtL, distL, r_L, t_L);
        Calib3d.calibrateCamera(objectPoints, pointsR, sizeOfPhoto, mxtR, distR, r_R, t_R);
//        mxtR=mxtL;
//        distR=distL;
        new_mxtL = Calib3d.getOptimalNewCameraMatrix(mxtL, distL, sizeOfPhoto, 1, sizeOfPhoto, null, false);
        new_mxtR = Calib3d.getOptimalNewCameraMatrix(mxtR, distR, sizeOfPhoto, 1, sizeOfPhoto, null, false);
//        new_mxtR=new_mxtL;

/*        Calib3d.initInverseRectificationMap(
                mxtL,
                distL,
                new Mat(),
                new_mxtL,
                sizeOfPhoto,
                5,
                leftMapX,
                leftMapY
        );

        Calib3d.initInverseRectificationMap(
                mxtR,
                distR,
                new Mat(),
                new_mxtR,
                sizeOfPhoto,
                5,
                rightMapX,
                rightMapY
        );*/

        Calib3d.stereoCalibrate(
                objectPoints,
                pointsL,
                pointsR,
                new_mxtL,
                distL,
                new_mxtR,
                distR,
                sizeOfPhoto,
                Rot,
                Trns,
                Emat,
                Fmat,
                0 | Calib3d.CALIB_FIX_INTRINSIC,
                term
        );

        Calib3d.stereoRectify(
                new_mxtL,
                distL,
                new_mxtR,
                distR,
                sizeOfPhoto,
                Rot,
                Trns,
                rectL,
                rectR,
                projMatL,
                projMatR,
                Q
        );

        Calib3d.initUndistortRectifyMap(
                new_mxtL,
                distL,
                rectL,
                projMatL,
                sizeOfPhoto,
                CvType.CV_16SC2,
                leftMapX,
                leftMapY
        );

        Calib3d.initUndistortRectifyMap(
                new_mxtR,
                distR,
                rectR,
                projMatR,
                sizeOfPhoto,
                CvType.CV_16SC2,
                rightMapX,
                rightMapY
        );

        printAllParameters();
        createAllCalibFiles();
    }

    public static void startCalibrate(CalibParameters param) throws IOException {
        switch (param) {
            case FILE:
                loadParams();
                calibrateCamerasFromFile();
                break;
            case VIDEO:
                createVideoImagesForCalibrating();
                calibrateCameras();
                break;
            case PHOTO:
                startPhotoCalibration();
                calibrateCameras();
                break;
        }
    }

    public static void startSystem() {
        startDisparityProcessing();
    }

    public static void setParametersBM(StereoBM stereoBM) {
        stereoBM.setMinDisparity(minDisparity);
        stereoBM.setNumDisparities(numDisparities);
        stereoBM.setBlockSize(blockSize);
/*        stereoBM.setPreFilterType(preFilterType);
        stereoBM.setTextureThreshold(textureThreshold);
        stereoBM.setPreFilterCap(preFilterCap);
        stereoBM.setPreFilterSize(preFilterSize);*/
        stereoBM.setSpeckleRange(speckleRange);
        stereoBM.setSpeckleWindowSize(speckleWindowSize);
        stereoBM.setUniquenessRatio(uniquenessRatio);
    }

    public static void setParametersBM(StereoSGBM stereoBM) {
        stereoBM.setMinDisparity(minDisparity);
        stereoBM.setNumDisparities(numDisparities);
        stereoBM.setBlockSize(blockSize);
/*        stereoBM.setPreFilterType(preFilterType);
        stereoBM.setTextureThreshold(textureThreshold);
        stereoBM.setPreFilterSize(preFilterSize);*/
//        stereoBM.setPreFilterCap(preFilterCap);
/*        stereoBM.setSpeckleRange(speckleRange);
        stereoBM.setSpeckleWindowSize(speckleWindowSize);*/
        stereoBM.setUniquenessRatio(uniquenessRatio);
        stereoBM.setSpeckleRange(speckleRange);
        stereoBM.setSpeckleWindowSize(speckleWindowSize);
        stereoBM.setDisp12MaxDiff(disp12MaxDiff);
    }

    public static void startDisparityProcessing() {
        TermCriteria term = new TermCriteria(TermCriteria.MAX_ITER + TermCriteria.EPS, 30, 1e-6);
        sizeOfPhoto = new Size(640, 480);

//        var stereo = StereoBM.create();
        var stereo = StereoSGBM.create();
        setParametersBM(stereo);
        int core = 3;

        VideoCapture camL = new VideoCapture(1);
        VideoCapture camR = new VideoCapture(2);

        Mat remappedL = new Mat();
        Mat remappedR = new Mat();
        Mat toDisplayDisp = new Mat();
        Mat toDispL = new Mat();
        Mat toDispR = new Mat();
        Mat undistL = new Mat();
        Mat undistR = new Mat();
        Mat depthMap = new Mat(new Size(640, 480), CvType.CV_16SC1);

        Mat mask = new Mat();
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stddev = new MatOfDouble();
        Mat mask2 = new Mat();
        float depth_thresh = 100.0f;

        while (true) {
            camL.grab();
            camR.grab();
            camL.retrieve(imgL);
            camR.retrieve(imgR);

            Imgproc.cvtColor(imgL, imgGrayL, Imgproc.COLOR_BGR2GRAY);
            Imgproc.cvtColor(imgR, imgGrayR, Imgproc.COLOR_BGR2GRAY);

/*            imgGrayL.convertTo(toDispL, CvType.CV_8SC(1));
            StartUI.drawImage(ImageProcessor.toBufferedImage(toDispL), 1);*/

/*            Calib3d.undistort(
                    imgGrayL,
                    undistL,
                    mxtL,
                    distL,
                    new_mxtL
            );

            Calib3d.undistort(
                    imgGrayR,
                    undistR,
                    mxtR,
                    distR,
                    new_mxtR
            );*/

/*            Imgproc.remap(
                    imgGrayL,
                    remappedL,
                    leftMapX,
                    leftMapY,
                    Imgproc.INTER_LINEAR
            );

            Imgproc.remap(
                    imgGrayR,
                    remappedR,
                    rightMapX,
                    rightMapY,
                    Imgproc.INTER_LINEAR
            );*/

/*            Calib3d.undistort(
                    remappedL,
                    undistL,
                    mxtL,
                    distL,
                    new_mxtL
            );

            Calib3d.undistort(
                    remappedR,
                    undistR,
                    mxtR,
                    distR,
                    new_mxtR
            );*/

            imgGrayL.convertTo(toDispL, CvType.CV_8SC(1));
            imgGrayR.convertTo(toDispR, CvType.CV_8SC(1));
            StartUI.drawImage(ImageProcessor.toBufferedImage(toDispL), 1);
            StartUI.drawImage(ImageProcessor.toBufferedImage(toDispR), 2);

            stereo.compute(imgGrayL, imgGrayR, disparityMap);

            Core.normalize(disparityMap, disparityMap, 0, 256, Core.NORM_MINMAX);

            Imgproc.erode(disparityMap, disparityMap, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(core, core)));
            Imgproc.dilate(disparityMap, disparityMap, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(core, core)));
/*            Imgproc.dilate(disparityMap, disparityMap, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(core, core)));
            Imgproc.erode(disparityMap, disparityMap, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(core, core)));*/
/*            if (Objects.nonNull(disparityMap)) {
                Mat submat = disparityMap.submat(220, 260, 300, 340);
                double val = 0;
                for (int i = 0; i < submat.width(); i++) {
                    for (int j = 0; j < submat.height(); j++) {
                        val += submat.get(i, j)[0];
                    }
                }
                System.out.println("Disp value in the center:" + val / (submat.width() * submat.height()));
            }*/
            for (int i = 0; i < disparityMap.height(); i++) {
                for (int j = 0; j < disparityMap.width(); j++) {
                    var value = Math.pow(disparityMap.get(i,j)[0], -0.6393) * 34.1139;
                    System.out.println(value);
                    var isFinite = Double.isFinite(value);
                    depthMap.put(i, j, isFinite ? value : 0.0);
                }
            }

            Core.inRange(depthMap, new Scalar(10), new Scalar(depth_thresh), mask);
            double s = Core.sumElems(mask).val[0] / 255.0;
            double img_area = mask.rows() * mask.cols();

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();

            if (s > 0.01 * img_area) {
                Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

                contours.sort((a, b) -> (int) (Imgproc.contourArea(b) - Imgproc.contourArea(a)));

                Mat cnt = contours.get(0);

                double cnt_area = Math.abs(Imgproc.contourArea(cnt));
                if (cnt_area > 0.01 * img_area) {
                    Rect box = new Rect();

                    box = Imgproc.boundingRect(cnt);

                    mask2 = new Mat(mask.size(), mask.type());
                    Imgproc.drawContours(mask2, contours, 0, new Scalar(255, 0, 0), -1);

                    Core.meanStdDev(depthMap, mean, stddev, mask2);

                    String text = MessageFormat.format("{0} cm", mean.at(Double.class, 0, 0).toString());

                    Imgproc.putText(imgGrayL, "WARNING!", new Point(box.x + 5, box.y - 40), 1, 2, new Scalar(0, 0, 255), 2, 2);
                    Imgproc.putText(imgGrayL, "Object at!", new Point(box.x + 5, box.y), 1, 2, new Scalar(0, 0, 255), 2, 2);
                    Imgproc.putText(imgGrayL, text, new Point(box.x + 5, box.y + 40), 1, 2, new Scalar(0, 0, 255), 2, 2);
                }

            } else {
                Imgproc.putText(imgGrayL, "SAFE!", new Point(200, 200), 1, 2, new Scalar(0, 255, 0), 2, 2);
            }


            imgGrayL.convertTo(toDispL, CvType.CV_8SC(1));
            imgGrayR.convertTo(toDispR, CvType.CV_8SC(1));
            StartUI.drawImage(ImageProcessor.toBufferedImage(toDispL), 1);
            StartUI.drawImage(ImageProcessor.toBufferedImage(toDispR), 2);
            disparityMap.convertTo(toDisplayDisp, CvType.CV_8SC(1));
            StartUI.drawImage(ImageProcessor.toBufferedImage(toDisplayDisp), 3);
            depthMap.release();
        }
    }


    public static void loadParams() throws IOException {
        try (var streamReader = Files.walk(Paths.get("resources/calibration/forReading"), 1)) {
            streamReader.collect(Collectors.toList()).forEach(OneFileClass::loadFromPath);
        }
        sizeOfPhoto = new Size(640, 480);
        OneFileOrb.isRectified = H1 && H2;
    }

    public static boolean H1 = false;
    public static boolean H2 = false;

    public static void loadFromPath(Path p) {
        if (!p.toString().equals("resources/calibration/forReading")) {
            try (var reader = Files.newBufferedReader(p)) {
                String data;
                data = reader.readLine();
                switch (p.getName(p.getNameCount() - 1).toString()) {
                    case "distL":
                        distL = parseMatrix(data, new Size(5, 1));
                        break;
                    case "distR":
                        distR = parseMatrix(data, new Size(5, 1));
                        break;
                    case "Emat":
                        Emat = parseMatrix(data, new Size(3, 3));
                        break;
                    case "Fmat":
                        Fmat = parseMatrix(data, new Size(3, 3));
                        break;
                    case "mtxL":
                        new_mxtL = parseMatrix(data, new Size(3, 3));
                        break;
                    case "mtxR":
                        new_mxtR = parseMatrix(data, new Size(3, 3));
                        break;
                    case "Rot":
                        Rot = parseMatrix(data, new Size(3, 3));
                        break;
                    case "Trns":
                        Trns = parseMatrix(data, new Size(1, 3));
                        break;
                    case "H1":
                        OneFileOrb.H1 = parseMatrix(data, new Size(3, 3));
                        H1 = true;
                        break;
                    case "H2":
                        OneFileOrb.H2 = parseMatrix(data, new Size(3, 3));
                        H2 = true;
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void printAllParameters() {
        printMat(new_mxtL, "MTXL");
        printMat(new_mxtR, "MTXR");
        printMat(distL, "DistL");
        printMat(distR, "DistR");
        printMat(Emat, "Emat");
        printMat(Fmat, "Fmat");
        printMat(Rot, "Rot");
        printMat(Trns, "Trns");
    }

    public static void createAllCalibFiles() throws IOException {
        createCalibMatFile(new_mxtL, "mtxL");
        createCalibMatFile(new_mxtR, "mtxR");
        createCalibMatFile(distL, "distL");
        createCalibMatFile(distR, "distR");
        createCalibMatFile(Emat, "Emat");
        createCalibMatFile(Fmat, "Fmat");
        createCalibMatFile(Rot, "Rot");
        createCalibMatFile(Trns, "Trns");
    }
}
