package rectification;

import matrix.MatrixStructure;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.Size;


import static org.opencv.imgproc.Imgproc.warpPerspective;

/**
 * Класс для работы с ректификацией изображений.
 */
public class Rectification {

    private Mat leftImg;
    private Mat rightImg;
    private Mat leftRectifiedImg;
    private Mat rightRectifiedImg;


    public Rectification(Mat leftImg, Mat rightImg) {
        this.leftImg = leftImg;
        this.rightImg = rightImg;
        this.leftRectifiedImg = new Mat();
        this.rightRectifiedImg = new Mat();
    }

    /**
     * Процесс ректификации
     *
     * @param matrixStructure информация о фундаментальной матрицы.
     * @return  информация о ректифицированных изображений.
     */
    public RectificationStructure startRectification(MatrixStructure matrixStructure) {
        Mat H1 = new Mat();
        Mat H2 = new Mat();

        uncalibratedRectification(
                matrixStructure.getPointMat1(),
                matrixStructure.getPointMat2(),
                matrixStructure.getFundamentalMatrix(),
                rightImg.size(),
                H1, H2
        );
        warpPerspective(leftImg, leftRectifiedImg, H1, leftImg.size());
        warpPerspective(rightImg, rightRectifiedImg, H2, rightImg.size());

        return new RectificationStructure(leftRectifiedImg, rightRectifiedImg);
    }

    /**
     * Ректификация некалиброванных изображений.
     * @param points1 - ключевые точки левого изображения.
     * @param points2 - ключевые точки правого изображения.
     * @param fundamentalMatrix - фундаментальная матрица.
     * @param imgSize - размер изображения.
     * @param H1 - выходной параметр 1 для дальнейшей ректификации.
     * @param H2 - выходной параметр 2 для дальнейшей ректификации.
     * @return true в случае успешной ректификации, иначе - false.
     */
    private boolean uncalibratedRectification(Mat points1,
                                              Mat points2,
                                              Mat fundamentalMatrix,
                                              Size imgSize,
                                              Mat H1,
                                              Mat H2) {
        return Calib3d.stereoRectifyUncalibrated(points1, points2, fundamentalMatrix, imgSize, H1, H2);
    }
}
