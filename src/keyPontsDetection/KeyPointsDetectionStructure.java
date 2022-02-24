package keyPontsDetection;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

/**
 * Стуктура, описывающая ключевые точки левого и правого изображений и их дескрипторы.
 */
public class KeyPointsDetectionStructure {

    private MatOfKeyPoint leftKP;
    private MatOfKeyPoint rightKP;
    private Mat leftDescriptor;
    private Mat rightDescriptor;

    public KeyPointsDetectionStructure(MatOfKeyPoint leftKP, MatOfKeyPoint rightKP, Mat leftDescriptor, Mat rightDescriptor) {
        this.leftKP = leftKP;
        this.rightKP = rightKP;
        this.leftDescriptor = leftDescriptor;
        this.rightDescriptor = rightDescriptor;
    }

    public MatOfKeyPoint getLeftKP() {
        return leftKP;
    }

    public MatOfKeyPoint getRightKP() {
        return rightKP;
    }

    public Mat getLeftDescriptor() {
        return leftDescriptor;
    }

    public Mat getRightDescriptor() {
        return rightDescriptor;
    }
}
