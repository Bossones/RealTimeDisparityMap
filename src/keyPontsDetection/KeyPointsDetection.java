package keyPontsDetection;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.AKAZE;
import org.opencv.features2d.AgastFeatureDetector;
import org.opencv.features2d.BRISK;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.GFTTDetector;
import org.opencv.features2d.KAZE;
import org.opencv.features2d.MSER;
import org.opencv.features2d.ORB;
import org.opencv.features2d.SIFT;

import java.util.Objects;

public class KeyPointsDetection {

    private static KeyPointsDetection _this;

    private KeyPointsDetection() {}

    /**
     * Singleton паттерн для создания класса.
     * @return детектор ключевых точек изображений.
     */
    public synchronized static KeyPointsDetection create() {
        if (Objects.isNull(_this)) {
            return _this = new KeyPointsDetection();
        } else {
            return _this;
        }
    }

    /**
     * Детектирование ключевых точек левого и правого изображений.
     * Создание дескрипторов для ключевых точек левого и правого изображений.
     *
     * @param leftImg   левое изображение.
     * @param rightImg  правое изображение.
     * @return          {@see KeyPointsDetectionStructure}
     */
    public static KeyPointsDetectionStructure detectKeyPointsAndDescriptors(Mat leftImg, Mat rightImg) {

        var leftKP = new MatOfKeyPoint();
        var rightKP = new MatOfKeyPoint();

        var leftDescriptor = new Mat();
        var rightDescriptor = new Mat();

        var orb = ORB.create();

        orb.detectAndCompute(leftImg, new Mat(), leftKP, leftDescriptor);
        orb.detectAndCompute(rightImg, new Mat(), rightKP, rightDescriptor);

        return new KeyPointsDetectionStructure(leftKP, rightKP, leftDescriptor, rightDescriptor);
    }
}

