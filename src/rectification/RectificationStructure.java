package rectification;

import org.opencv.core.Mat;

public class RectificationStructure {

    private Mat leftRectifiedImg;
    private Mat rightRectifiedImg;

    public RectificationStructure(Mat leftRectifiedImg, Mat rightRectifiedImg) {
        this.leftRectifiedImg = leftRectifiedImg;
        this.rightRectifiedImg = rightRectifiedImg;
    }

    public Mat getLeftRectifiedImg() {
        return leftRectifiedImg;
    }

    public Mat getRightRectifiedImg() {
        return rightRectifiedImg;
    }
}
