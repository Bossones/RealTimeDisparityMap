package mapping;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import params.Params;

import java.util.Objects;

public class RemappingImages {

    public static void startRemappingImages(Mat imageL, Mat imageR) throws Exception {
        if (Objects.isNull(Params.STEREO_MAP_L_X) || Objects.isNull(Params.STEREO_MAP_L_Y)
                || Objects.isNull(Params.STEREO_MAP_R_X) || Objects.isNull(Params.STEREO_MAP_R_Y)) {
            throw new Exception("Отсутствуют параметры каллибровки у левой или правой камеры");
        }

        Mat bestL = new Mat(), bestR = new Mat();

        Imgproc.remap(
                imageL,
                bestL,
                Params.STEREO_MAP_L_X,
                Params.STEREO_MAP_L_Y,
                Imgproc.INTER_LANCZOS4,
                Core.BORDER_CONSTANT,
                new Scalar(0)
        );

        Imgproc.remap(
                imageR,
                bestR,
                Params.STEREO_MAP_R_X,
                Params.STEREO_MAP_R_Y,
                Imgproc.INTER_LANCZOS4,
                Core.BORDER_CONSTANT,
                new Scalar(0)
        );

        Params.BEST_LEFT = bestL;
        Params.BEST_RIGHT = bestR;
    }
}
