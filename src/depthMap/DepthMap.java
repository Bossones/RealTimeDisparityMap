package depthMap;

import org.opencv.calib3d.StereoBM;
import org.opencv.calib3d.StereoSGBM;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import params.Params;
import rectification.RectificationStructure;

import java.util.Objects;

public class DepthMap {

    private final static int block_size = 9;
    private final static int min_disp = -24;
    private final static int max_disp = 24;
    private final static int num_disp = 32;
    private final static int uniquenessRatio = 40;
    private final static int speckleWindowsSize = 100;
    private final static int speckleRange = 5;
    private final static int disp12MaxDiff = 5;

    private static StereoSGBM stereoSGBM;
    private static StereoBM stereoBM;

    private static synchronized StereoSGBM createStereoSGBM() {
        if (Objects.isNull(stereoSGBM)) {
            return stereoSGBM = StereoSGBM.create(
                    min_disp,
                    num_disp,
                    block_size,
                    8 * block_size * block_size,
                    32 * block_size * block_size,
                    disp12MaxDiff,
                    1,
                    uniquenessRatio,
                    speckleWindowsSize,
                    speckleRange
            );
        } else {
            return stereoSGBM;
        }
    }

    private static synchronized StereoBM createStereoBM() {
        if (Objects.isNull(stereoBM)) {
            return stereoBM = StereoBM.create(
                    num_disp,
                    block_size
            );
        } else {
            return stereoBM;
        }
    }


    public DepthMapStructure startComputingDepthMap(RectificationStructure rectificationStructure) {

        var depthMap = new Mat();
        var normalizedDepthMap = new Mat();

        var stereo = createStereoSGBM();
        var stereoBM = createStereoBM();
//        stereoBM.setUniquenessRatio(15);

        stereoBM.compute(
                rectificationStructure.getLeftRectifiedImg(),
                rectificationStructure.getRightRectifiedImg(),
                depthMap
        );

/*        Imgproc.erode(depthMap, depthMap, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(7, 7)));
        Imgproc.dilate(depthMap, depthMap, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(7, 7)));
        Imgproc.dilate(depthMap, depthMap, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(7, 7)));
        Imgproc.erode(depthMap, depthMap, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(7, 7)));*/

        Core.normalize(depthMap, normalizedDepthMap, 255, 0, Core.NORM_MINMAX);

        return new DepthMapStructure(depthMap, normalizedDepthMap);
    }

    public static void startComputingDisparityMap() throws Exception {
        if (Objects.isNull(Params.BEST_LEFT) || Objects.isNull(Params.BEST_RIGHT)) {
            throw new Exception("Нет ректифицированного левого и правого изображений");
        }
        Mat disparityMap = new Mat();
        var stereo = createStereoBM();
        stereo.compute(Params.BEST_LEFT, Params.BEST_RIGHT, disparityMap);

        Params.DISPARITY_MAP = disparityMap;
    }
}
