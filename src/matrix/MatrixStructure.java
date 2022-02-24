package matrix;

import org.opencv.core.Mat;

public final class MatrixStructure {

    private Mat pointMat1;
    private Mat pointMat2;
    private Mat fundamentalMatrix;

    public MatrixStructure(Mat pointMat1, Mat pointMat2, Mat fundamentalMatrix) {
        this.pointMat1 = pointMat1;
        this.pointMat2 = pointMat2;
        this.fundamentalMatrix = fundamentalMatrix;
    }

    public Mat getPointMat1() {
        return pointMat1;
    }

    public Mat getPointMat2() {
        return pointMat2;
    }

    public Mat getFundamentalMatrix() {
        return fundamentalMatrix;
    }
}
