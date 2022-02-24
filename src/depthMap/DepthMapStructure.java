package depthMap;

import org.opencv.core.Mat;

public class DepthMapStructure {

    private Mat depthMap;
    private Mat normalizedDepthMap;

    public DepthMapStructure(Mat depthMap, Mat normalizedDepthMap) {
        this.depthMap = depthMap;
        this.normalizedDepthMap = normalizedDepthMap;
    }

    public Mat getDepthMap() {
        return depthMap;
    }

    public Mat getNormalizedDepthMap() {
        return normalizedDepthMap;
    }
}
