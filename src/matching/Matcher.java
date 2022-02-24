package matching;

import keyPontsDetection.KeyPointsDetectionStructure;
import org.opencv.core.Core;
import org.opencv.core.DMatch;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.Point;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.FlannBasedMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для работы с матером ключевых точек.
 */
public class Matcher {

    /**
     * Запускает процесс сопоставления точек и записи "хороших" точек.
     * @param keyPointsDetectionStructure   структура, содержащая информацию о ключевых точках.
     * @return  структура, содержащая информацию о подходящих точках для обработки.
     */
    public MatcherStructure startKeyPointsMatching(KeyPointsDetectionStructure keyPointsDetectionStructure) {

        var flannMatcher = new FlannBasedMatcher();
        var bvMathcer = new BFMatcher();
        var matches = new ArrayList<MatOfDMatch>();

        bvMathcer.knnMatch(
                keyPointsDetectionStructure.getLeftDescriptor(),
                keyPointsDetectionStructure.getRightDescriptor(),
                matches,
                2
        );


        var matchesMask = new ArrayList<MatOfByte>();

        List<DMatch> good = new ArrayList<>();
        List<Point> pts1 = new ArrayList<>();
        List<Point> pts2 = new ArrayList<>();

        for (int i = 0; i< matches.size(); i++) {
            var m = matches.get(i).toArray()[0];
            var n = matches.get(i).toArray()[1];

            if (m.distance < 0.7 * n.distance) {
                matchesMask.add(new MatOfByte(new byte[] {1, 0}));
                good.add(m);
                pts2.add(keyPointsDetectionStructure.getRightKP().toArray()[m.trainIdx].pt);
                pts1.add(keyPointsDetectionStructure.getLeftKP().toArray()[n.queryIdx].pt);
            }
        }

        return new MatcherStructure(matches, pts1, pts2, good, matchesMask);
    }
}
