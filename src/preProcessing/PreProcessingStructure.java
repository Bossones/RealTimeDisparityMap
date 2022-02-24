package preProcessing;

import org.opencv.core.Mat;

public class PreProcessingStructure {

    private Mat leftFilteredImage;
    private Mat rightFilteredImage;

    public PreProcessingStructure(Mat leftFilteredImage, Mat rightFilteredImage) {
        this.leftFilteredImage = leftFilteredImage;
        this.rightFilteredImage = rightFilteredImage;
    }

    public Mat getLeftFilteredImage() {
        return leftFilteredImage;
    }

    public Mat getRightFilteredImage() {
        return rightFilteredImage;
    }
}
