package matching;

import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.Point;

import java.util.List;

public class MatcherStructure {

    private List<MatOfDMatch> matches;
    private List<Point> pts1;
    private List<Point> pts2;
    private List<DMatch> good;
    private List<MatOfByte> matchesMask;
    private MatOfDMatch matchesMatch;

    public MatcherStructure(List<MatOfDMatch> matches, List<Point> pts1, List<Point> pts2, List<DMatch> good, List<MatOfByte> matchesMask, MatOfDMatch matchesMatch) {
        this.matches = matches;
        this.pts1 = pts1;
        this.pts2 = pts2;
        this.good = good;
        this.matchesMask = matchesMask;
        this.matchesMatch = matchesMatch;
    }

    public MatcherStructure(List<MatOfDMatch> matches, List<Point> pts1, List<Point> pts2, List<DMatch> good, List<MatOfByte> matchesMask) {
        this.matches = matches;
        this.pts1 = pts1;
        this.pts2 = pts2;
        this.good = good;
        this.matchesMask = matchesMask;
    }

    public List<MatOfDMatch> getMatches() {
        return matches;
    }

    public List<Point> getPts1() {
        return pts1;
    }

    public List<Point> getPts2() {
        return pts2;
    }

    public List<DMatch> getGood() {
        return good;
    }

    public List<MatOfByte> getMatchesMask() {
        return matchesMask;
    }

    public MatOfDMatch getMatchesMatch() {
        return matchesMatch;
    }
}
