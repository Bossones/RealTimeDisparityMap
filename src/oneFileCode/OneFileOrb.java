package oneFileCode;

import imageProcessing.ImageProcessor;
import org.opencv.calib3d.Calib3d;
import org.opencv.calib3d.StereoSGBM;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.ORB;
import org.opencv.features2d.SIFT;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import ui.main.StartUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import static utils.Utils.createCalibMatFile;

public class OneFileOrb {

    public static Mat imgL = new Mat();
    public static Mat imgR = new Mat();
    public static boolean isRectified = false;
    public static Size sizeOfImage = new Size(640, 480);
    public static Mat H1 = new Mat();
    public static Mat H2 = new Mat();
    public static Mat disparityMap = new Mat();

    public static void startSystem() throws IOException {
        VideoCapture camL = new VideoCapture(1);
        VideoCapture camR = new VideoCapture(2);

        Mat toDispL = new Mat();
        Mat imgRectL = new Mat();
        Mat imgRectR = new Mat();

        while (true) {

            if (!isRectified && (H1.empty() || H2.empty())) {
               for (int i = 0; i < 5; i++) {
                   camL.grab();
                   camR.grab();

                   camL.retrieve(imgL, Imgcodecs.IMREAD_GRAYSCALE);
                   camR.retrieve(imgR, Imgcodecs.IMREAD_GRAYSCALE);
               }

                Imgproc.GaussianBlur(imgL, imgL, new Size(3, 3), 0);
                Imgproc.GaussianBlur(imgR, imgR, new Size(3, 3), 0);
/*                var imgL = Imgcodecs.imread("resources\\leftCalibration\\defaultImages\\0.png", Imgcodecs.IMREAD_GRAYSCALE);
                var imgR = Imgcodecs.imread("resources\\rightCalibration\\defaultImages\\0.png", Imgcodecs.IMREAD_GRAYSCALE);*/

                var leftKP = new MatOfKeyPoint();
                var rightKP = new MatOfKeyPoint();

                var leftDescriptor = new Mat();
                var rightDescriptor = new Mat();

                var bvMathcer = new BFMatcher();
                var matches = new ArrayList<MatOfDMatch>();
                var goodMatches = new LinkedList<DMatch>();
                var pts1 = new ArrayList<Point>();
                var pts2 = new ArrayList<Point>();

                var pts1Mat2F = new MatOfPoint2f();
                var pts2Mat2F = new MatOfPoint2f();
                Mat fundamentalMatrix;
                Mat outputMask = new Mat();
                var betterMatches = new LinkedList<DMatch>();
                var betterPts1 = new ArrayList<Point>();
                var betterPts2 = new ArrayList<Point>();
                var matPoints1 = new Mat();
                var matPoints2 = new Mat();

                var orb = SIFT.create();

                orb.detectAndCompute(imgL, new Mat(), leftKP, leftDescriptor);
                orb.detectAndCompute(imgR, new Mat(), rightKP, rightDescriptor);

                bvMathcer.knnMatch(
                        leftDescriptor,
                        rightDescriptor,
                        matches,
                        2
                );

                for (MatOfDMatch matOfDMatch : matches) {
                    if (matOfDMatch.toArray()[0].distance / matOfDMatch.toArray()[1].distance < 0.7) {
                        goodMatches.add(matOfDMatch.toArray()[0]);
                    }
                }

                for (int i = 0; i < goodMatches.size(); i++) {
                    pts1.add(leftKP.toList().get(goodMatches.get(i).queryIdx).pt);
                    pts2.add(rightKP.toList().get(goodMatches.get(i).trainIdx).pt);
                }

                pts1Mat2F.fromList(pts1);
                pts2Mat2F.fromList(pts2);

                fundamentalMatrix = Calib3d.findFundamentalMat(
                        pts1Mat2F,
                        pts2Mat2F,
                        Calib3d.FM_RANSAC,
                        3,
                        0.99,
                        outputMask
                );

                for (int i = 0; i < goodMatches.size(); i++) {
                    if (outputMask.get(i, 0)[0] != 0.0) {
                        betterMatches.add(goodMatches.get(i));
                        betterPts1.add(pts1.get(i));
                        betterPts2.add(pts2.get(i));
                    }
                }

                var m1 = new MatOfPoint2f();
                m1.fromList(betterPts1);
                matPoints1 = m1;
                var m2 = new MatOfPoint2f();
                m2.fromList(betterPts2);
                matPoints2 = m2;

                isRectified = Calib3d.stereoRectifyUncalibrated(matPoints1, matPoints2, fundamentalMatrix, sizeOfImage, H1, H2);
                createCalibMatFile(H1, "H1");
                createCalibMatFile(H2, "H2");
                System.out.println(H1);
            } else {
                camL.grab();
                camR.grab();

                camL.retrieve(imgL, Imgcodecs.IMREAD_GRAYSCALE);
                camR.retrieve(imgR, Imgcodecs.IMREAD_GRAYSCALE);

                Imgproc.GaussianBlur(imgL, imgL, new Size(3, 3), 0);
                Imgproc.GaussianBlur(imgR, imgR, new Size(3, 3), 0);

                Imgproc.warpPerspective(imgL, imgRectL, H1, sizeOfImage);
                Imgproc.warpPerspective(imgR, imgRectR, H2, sizeOfImage);

                imgRectL.convertTo(toDispL, CvType.CV_8SC(1));
                StartUI.drawImage(ImageProcessor.toBufferedImage(toDispL), 1);

                imgRectR.convertTo(toDispL, CvType.CV_8SC(1));
                StartUI.drawImage(ImageProcessor.toBufferedImage(toDispL), 2);

                var stereo = StereoSGBM.create();
                OneFileClass.setParametersBM(stereo);

                stereo.compute(imgRectL, imgRectR, disparityMap);
                Core.normalize(disparityMap, disparityMap, 0, 256, Core.NORM_MINMAX);

                disparityMap.convertTo(toDispL, CvType.CV_8SC(1));
                StartUI.drawImage(ImageProcessor.toBufferedImage(toDispL), 3);
            }

        }
    }
}
