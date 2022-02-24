package test;

import depthMap.DepthMap;
import depthMap.DepthMapStructure;
import keyPontsDetection.KeyPointsDetection;
import keyPontsDetection.KeyPointsDetectionStructure;
import matching.Matcher;
import matching.MatcherStructure;
import matrix.MatrixComputing;
import matrix.MatrixStructure;
import org.opencv.core.Core;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import postProcessing.PostFilter;
import preProcessing.PreProcessing;
import preProcessing.PreProcessingStructure;
import rectification.Rectification;
import rectification.RectificationStructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.line;

public final class RunTestSystem {

    private Path pathToLeftImage;
    private Path pathToRightImage;
    private Path leftPathKP = Paths.get("resources/test/keyPoints/leftKP.png");
    private Path rightPathKP = Paths.get("resources/test/keyPoints/rightKP.png");

    private Mat leftImage;
    private Mat rightImage;
    private Mat leftGrayFilteredImg;
    private Mat rightGrayFilteredImg;
    private KeyPointsDetectionStructure kpStructure;
    private MatcherStructure matcherStructure;
    private MatrixStructure matrixStructure;
    private RectificationStructure rectificationStructure;
    private DepthMapStructure depthMapStructure;

    private RunTestSystem(Path pathToLeftImage, Path pathToRightImage) {
        this();
        if (Objects.nonNull(pathToLeftImage) && Objects.nonNull(pathToRightImage)) {
            this.pathToLeftImage = pathToLeftImage;
            this.pathToRightImage = pathToRightImage;
        }
    }

    private RunTestSystem() {
        this.pathToLeftImage = Paths.get("resources/test/left.jpg");
        this.pathToRightImage = Paths.get("resources/test/right.jpg");
        this.leftGrayFilteredImg = new Mat();
        this.rightGrayFilteredImg = new Mat();
    }

    /**
     * Тестирует работоспособность стандартных методов загрузки и обработки изображений при помощи openCV.
     * @throws NoSuchFileException - если нет тестовых изображений.
     */
    private void downloadImageFromFileSystem() throws NoSuchFileException, InterruptedException {
        System.out.println("Старт тестирования загрузки изображений из файловой системы");
        if (!Files.exists(pathToLeftImage)) {
            throw new NoSuchFileException("Нет левого изображения в файловой системе");
        }
        if (!Files.exists(pathToRightImage)) {
            throw new NoSuchFileException("Нет правого изображения в файловой системе");
        }
        this.leftImage = Imgcodecs.imread(pathToLeftImage.toString());
        this.rightImage = Imgcodecs.imread(pathToRightImage.toString());

        PreProcessing preProc = new PreProcessing(this.leftImage, this.rightImage);
        PreProcessingStructure preProcessingStructure = preProc.startPreProcessing();
        leftGrayFilteredImg = preProcessingStructure.getLeftFilteredImage();
        rightGrayFilteredImg = preProcessingStructure.getRightFilteredImage();

        Imgcodecs.imwrite("resources/test/leftResizedGrayImage.png", leftGrayFilteredImg);
        Imgcodecs.imwrite("resources/test/rightResizedGrayImage.png", rightGrayFilteredImg);

        System.out.println("Базовое тестирвание загрузки и обработки изображений завершено");
        Thread.sleep(1000);
    }

    /**
     * Тестирование детектирования ключевых точек изображения и их дектриптора.
     *
     * @throws Exception при отсутствии структуры ключевых точек левого и правого изображений.
     */
    private void testKeyPointsDetection() throws Exception {
        System.out.println("Запуск процесса тестирования детектирования ключевых точек");
        var leftImageKeyPoints = new Mat();
        var rightImageKeyPoints = new Mat();

        kpStructure = KeyPointsDetection.detectKeyPointsAndDescriptors(leftGrayFilteredImg, rightGrayFilteredImg);
        if (Objects.nonNull(kpStructure)) {
            Features2d.drawKeypoints(
                    leftGrayFilteredImg,
                    kpStructure.getLeftKP(),
                    leftImageKeyPoints,
                    new Scalar(0, 0, 255),
                    Features2d.DrawMatchesFlags_DRAW_RICH_KEYPOINTS
            );
            Features2d.drawKeypoints(
                    rightGrayFilteredImg,
                    kpStructure.getRightKP(),
                    rightImageKeyPoints,
                    new Scalar(0, 0, 255),
                    Features2d.DrawMatchesFlags_DRAW_RICH_KEYPOINTS
            );

            Imgcodecs.imwrite(leftPathKP.toString(), leftImageKeyPoints);
            Imgcodecs.imwrite(rightPathKP.toString(), rightImageKeyPoints);
        } else {
            throw new Exception("Процесс детектирования ключевых точек не был выполнен.");
        }
        System.out.println("Завершение тестирования детектирования ключевых точек");
    }

    /**
     * Тестирование процесса сопоставления ключевых точек.
     */
    private void startMatcher() throws InterruptedException {
        System.out.println("Запуск процесса сопоставления ключевых точек изображений");
        Thread.sleep(1000);

        var matcher = new Matcher();
        matcherStructure = matcher.startKeyPointsMatching(kpStructure);

        DMatch[] arrDmatch = new DMatch[matcherStructure.getGood().size()];

        for (int i = 0; i < arrDmatch.length; i++) {
            arrDmatch[i] = matcherStructure.getGood().get(i);
        }
        MatOfDMatch matOfDMatch = new MatOfDMatch(arrDmatch);

        var matchesKnn = new Mat();
        Features2d.drawMatchesKnn(
                leftGrayFilteredImg,
                kpStructure.getLeftKP(),
                rightGrayFilteredImg,
                kpStructure.getRightKP(),
                matcherStructure.getMatches().subList(0, 50),
                matchesKnn,
                new Scalar(0, 255, 0),
                new Scalar(255, 0, 0)
        );

        Imgcodecs.imwrite("resources/test/matcher/matches.png", matchesKnn);

        matchesKnn = new Mat();
        Features2d.drawMatches(
                leftGrayFilteredImg,
                kpStructure.getLeftKP(),
                rightGrayFilteredImg,
                kpStructure.getRightKP(),
                matOfDMatch,
                matchesKnn,
                new Scalar(0, 255, 0),
                new Scalar(255, 0, 0)
        );

        Imgcodecs.imwrite("resources/test/matcher/bestMatches.png", matchesKnn);


        System.out.println("Завершение процесса сопоставления ключевых точек изображений");
    }

    /**
     * Тестирование процесса расчета фундаментальной матрицы и построения эпиполярных линий.
     */
    private void startComputingFundamentalMatrix() throws InterruptedException {
        System.out.println("Старт процесса расчета фундаментальной матрицы и построения эпиполярных линий");
        Thread.sleep(1000);
        var matrixComputing = new MatrixComputing();
        matrixStructure = matrixComputing.startComputingFundamentalMatrix(matcherStructure);
        Mat[] lines = matrixComputing.startComputingCorrespondEpilines(matrixStructure);

        var img1 = drawLines(
                leftGrayFilteredImg,
                rightGrayFilteredImg,
                lines[0],
                matrixStructure.getPointMat1(),
                matrixStructure.getPointMat2()
        );

        var img2 = drawLines(
                rightGrayFilteredImg,
                leftGrayFilteredImg,
                lines[1],
                matrixStructure.getPointMat2(),
                matrixStructure.getPointMat1()
        );

        Imgcodecs.imwrite("resources/test/epilines/leftEpilines.png", img1);
        Imgcodecs.imwrite("resources/test/epilines/rightEpilines.png", img2);
        System.out.println("Завершение процесса расчета фундаментальной матрицы и построения эпиполярных линий");
    }

    /**
     * Тестирование ректификации изображений.
     */
    private void startRectificationTest() throws InterruptedException {
        System.out.println("Запуск процесса ректификации изображений");
        Thread.sleep(1000);
        var rectification = new Rectification(leftGrayFilteredImg, rightGrayFilteredImg);
        rectificationStructure = rectification.startRectification(matrixStructure);

        Imgcodecs.imwrite("resources/test/rectification/leftRectifiedImg.png", rectificationStructure.getLeftRectifiedImg());
        Imgcodecs.imwrite("resources/test/rectification/rightRectifiedImg.png", rectificationStructure.getRightRectifiedImg());
        System.out.println("Завершение процесса ректификации изображений");
    }

    /**
     * Тестирование построения карты глубины местности.
     */
    private void startDepthMapComputingTest() throws InterruptedException {
        System.out.println("Запуск процесса вычисления карты глубины");
        Thread.sleep(1000);

        var depthMap = new DepthMap();
        depthMapStructure = depthMap.startComputingDepthMap(rectificationStructure);

        Imgcodecs.imwrite("resources/test/depthMap/depthMap.png", depthMapStructure.getDepthMap());
        Imgcodecs.imwrite("resources/test/depthMap/normalizedDepthMap.png", depthMapStructure.getNormalizedDepthMap());
        System.out.println("Завершение процесса построения карты глубины");
    }

    /**
     * Тестирование построения цветной карты глубины.
     */
    private void startColoredMapTest() throws Exception {
        System.out.println("Запуск процесса формировании цветной карты");
        Thread.sleep(1000);

        var colored = PostFilter.getColoredDepthMap(depthMapStructure.getNormalizedDepthMap());
        Imgcodecs.imwrite("resources/test/depthMap/depthMapColored.png", colored);
        System.out.println("Завершение процесса формирования цветной карты глубины");

    }

    /**
     * Запуск процесса тестирования изображений
     */
    public static void startTests() throws InterruptedException, IOException {
        System.out.println("Старт стадии тестирования");
        Thread.sleep(1000);

        var testSystem = new RunTestSystem();
        testSystem.deleteAllTestImages(Paths.get("resources/test"));

        // Старт модуля загрузки изображений.
        try {
            testSystem.downloadImageFromFileSystem();
        } catch (Exception e) {
            System.out.println("Тестирование не завершено из-за некорректной работы базовой загрузки и обработки изображений");
            Thread.sleep(1000);
            e.printStackTrace();
        }

        // Старт модуля детектирования ключевых точек изображений.
        try {
            testSystem.testKeyPointsDetection();
        } catch (Exception e) {
            System.out.println("Тестирование не завершено из-за некорректной работы детектирования ключевых точек изображения");
            Thread.sleep(1000);
            e.printStackTrace();
        }

        // Старт модуля сопоставления ключевых точек изображений.
        try {
            testSystem.startMatcher();
        } catch (Exception e) {
            System.out.println("Тестирование не завершено из-за некорректной работы сопоставления ключевых точек изображения");
            Thread.sleep(1000);
            e.printStackTrace();
        }

        // Старт модуля расчета фундаментальной матрицы.
        try {
            testSystem.startComputingFundamentalMatrix();
        } catch (Exception e) {
            System.out.println("Тестирование не завершено из-за некорректной работы расчета фундаментальной матрицы");
            Thread.sleep(1000);
            e.printStackTrace();
        }

        // Старт модуля ректификации.
        try {
            testSystem.startRectificationTest();
        } catch (Exception e) {
            System.out.println("Тестирование не завершено из-за некорректной работы ректификации изображений");
            Thread.sleep(1000);
            e.printStackTrace();
        }

        // Старт модуля построения карты глубины.
        try {
            testSystem.startDepthMapComputingTest();
        } catch (Exception e) {
            System.out.println("Тестирование не завершено из-за некорректной работы построения карты глубины");
            Thread.sleep(1000);
            e.printStackTrace();
        }

        // Старт модуля колоризации карты глубины.
        try {
            testSystem.startColoredMapTest();
        } catch (Exception e) {
            System.out.println("Тестирование не завершено из-за некорректной работы колоризации карты глубины");
            Thread.sleep(1000);
            e.printStackTrace();
        }

    }

    /**
     * Удаляет все сгенерированные во время тестирования изображения.
     * @param pathToTestResources   путь к тестовым файлам.
     * @throws IOException          выбрасывается, если нет доступа к тестовым изображениям.
     */
    private void deleteAllTestImages(Path pathToTestResources) throws IOException {
        try (Stream<Path> pathStream = Files.walk(pathToTestResources, 1);
             Stream<Path> pathStream1 = Files.walk(pathToTestResources.resolve("keyPoints"), 1);
             Stream<Path> pathStream2 = Files.walk(pathToTestResources.resolve("matcher"), 1);
             Stream<Path> pathStream3 = Files.walk(pathToTestResources.resolve("epilines"), 1);
             Stream<Path> pathStream4 = Files.walk(pathToTestResources.resolve("rectification"), 1);
             Stream<Path> pathStream5 = Files.walk(pathToTestResources.resolve("depthMap"), 1);
        ) {
            pathStream.parallel()
                    .filter(p -> !p.equals(Paths.get("resources/test/left.jpg"))
                            && !p.equals(Paths.get("resources/test/right.jpg")))
                    .forEach(p -> {
                        try {
                            if (!Files.isDirectory(p)) {
                                Files.deleteIfExists(p);
                            }
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    });

            pathStream1.parallel().forEach(p -> {
                try {
                    if (!Files.isDirectory(p)) {
                        Files.deleteIfExists(p);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            });

            pathStream2.parallel().forEach(p -> {
                try {
                    if (!Files.isDirectory(p)) {
                        Files.deleteIfExists(p);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            });

            pathStream3.parallel().forEach(p -> {
                try {
                    if (!Files.isDirectory(p)) {
                        Files.deleteIfExists(p);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            });

            pathStream4.parallel().forEach(p -> {
                try {
                    if (!Files.isDirectory(p)) {
                        Files.deleteIfExists(p);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            });

            pathStream5.parallel().forEach(p -> {
                try {
                    if (!Files.isDirectory(p)) {
                        Files.deleteIfExists(p);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            });
        }
    }

    /**
     * Отображает эпиполярные линии на изображении.
     *
     * @param imgLeftSrc    левое изображение, полученное с камеры.
     * @param imgRightSrc   правое изображеие, полученное с камеры.
     * @param lines         эпиполярные линии
     * @param pointMat1     модифицированные точки левого изображения при расчете фундаментальной мартицы.
     * @param pointMat2     модифицированные точки правого изображения при расчете фундаментальной мартицы.
     * @return              изображение с эпиполярными линиями.
     */
    private Mat drawLines(Mat imgLeftSrc, Mat imgRightSrc, Mat lines, Mat pointMat1, Mat pointMat2) {
        var img1Color = imgLeftSrc.clone();
        var img2Color = imgRightSrc.clone();

        var height = img1Color.size().height;
        var width = img1Color.size().width;

        Point xy0 = new Point();
        Point xy1 = new Point();
        var random = ThreadLocalRandom.current();
        double[] color = new double[3];

        for (int i = 0, k = 0, j = 0; i < lines.rows() && k < pointMat1.rows() && j < pointMat2.rows(); i++, k++, j++) {
            for (int col = 0; col < color.length; col++) {
                color[col] = random.nextInt(255);
            }
            xy0.set(new double[]{0, -lines.get(i, 0)[2]/lines.get(i, 0)[1]});
            xy1.set(new double[]{width, -(lines.get(i, 0)[2] + lines.get(i, 0)[0] * width)/lines.get(i, 0)[1]});

            line(img1Color, xy0, xy1, new Scalar(color), 1);
            circle(img1Color, new Point(pointMat1.get(k, 0)), 5, new Scalar(color), -1);
            circle(img2Color, new Point(pointMat2.get(j, 0)), 5, new Scalar(color), -1);
        }

        return img1Color;
    }
}
