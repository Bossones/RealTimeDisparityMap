package utils;

import oneFileCode.OneFileClass;
import oneFileCode.OneFileOrb;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Utils {

    public static void createCalibMatFile(Mat m, String fileName) throws IOException {
        double[][] convertToDouble = new double[(int)m.size().height][(int)m.size().width];
        for (int i = 0; i < m.size().height; i++) {
            for (int j = 0; j < m.size().width; j++) {
                convertToDouble[i][j] = m.get(i, j)[0];
            }
        }

        try (var writer = Files.newBufferedWriter(Paths.get("resources/calibration/", fileName))) {
            writer.write(
                    Arrays.stream(convertToDouble)
                            .flatMapToDouble(Arrays::stream)
                            .mapToObj(Double::toString)
                            .collect(Collectors.joining(","))
            );
        }
    }

    public static Mat parseMatrix(String data, Size size) {
        Mat matrix = new Mat(size, CvType.CV_64FC1);
        var values = data.split(",");
        for (int i = 0; i < size.height; i++) {
            for (int j = 0; j < size.width; j++) {
                matrix.put(i, j, Double.parseDouble(values[j + (i * (int)size.width)]));
            }
        }
        return matrix;
    }

    public static void printMat(Mat mat, String name) {
        System.out.println(name);
        System.out.println(mat);
        for (int i = 0; i < mat.size().height; i++) {
            for (int j = 0; j < mat.size().width; j++) {
                System.out.printf("%6.2f ", mat.get(i, j)[0]);
            }
            System.out.println();
        }
    }

}
