package matrix;

import matching.MatcherStructure;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для рассчета фундаментальной матрицы двух изодражений и эпиполярных линий.
 */
public class MatrixComputing {

    /**
     * Расчитывает фундаментальную матрицу двух изображений на основе сопоставления ключевых точек изображений.
     *
     * @param matching  информация о сопоставлении ключевых точек.
     * @return  информация о фундаментальной матрице.
     */
    public MatrixStructure startComputingFundamentalMatrix(MatcherStructure matching) {
        MatOfPoint2f points1 = new MatOfPoint2f(copyToArray(matching.getPts1()));
        MatOfPoint2f points2 = new MatOfPoint2f(copyToArray(matching.getPts2()));
        List<Point> modifiedPoints1 = new ArrayList<>();
        List<Point> modifiedPoints2 = new ArrayList<>();

        Mat fundamentalMatrix;
        Mat outputMask = new Mat();

        fundamentalMatrix = Calib3d.findFundamentalMat(
                points1,
                points2,
                Calib3d.FM_RANSAC,
                3,
                0.99,
                outputMask
        );

        for (int i = 0; i < outputMask.rows(); i++) {
            if ((int)outputMask.get(i, 0)[0] == 1) {
                modifiedPoints1.add(matching.getPts1().get(i));
                modifiedPoints2.add(matching.getPts2().get(i));
            }
        }

        Mat pointMat1 = new MatOfPoint2f(copyToArray(modifiedPoints1));
        Mat pointMat2 = new MatOfPoint2f(copyToArray(modifiedPoints2));

        return new MatrixStructure(pointMat1, pointMat2, fundamentalMatrix);
    }

    /**
     * Вычисляет эпиполярные линии двух изображений.
     * @param matrixStructure   стуктура, содержащая информацию о фундаментальной матрице.
     * @return  эпиполярные линии изображений.
     */
    public Mat[] startComputingCorrespondEpilines(MatrixStructure matrixStructure) {
        var lines1 = new Mat();
        var lines2 = new Mat();

        Calib3d.computeCorrespondEpilines(
                matrixStructure.getPointMat2(),
                2,
                matrixStructure.getFundamentalMatrix(),
                lines1
        );

        Calib3d.computeCorrespondEpilines(
                matrixStructure.getPointMat1(),
                1,
                matrixStructure.getFundamentalMatrix(),
                lines2
        );

        return new Mat[] {lines1, lines2};
    }

    private static Point[] copyToArray(List<Point> pointsList) {
        Point[] points = new Point[pointsList.size()];
        for (int i = 0; i < pointsList.size(); i++) {
            points[i] = new Point(pointsList.get(i).x, pointsList.get(i).y);
        }
        return points;
    }
}
