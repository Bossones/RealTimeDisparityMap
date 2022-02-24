package ui.main;

import oneFileCode.CalibParameters;
import oneFileCode.OneFileClass;
import oneFileCode.OneFileOrb;
import org.opencv.core.Core;
import system.SystemApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class StartUI {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        systemApp = SystemApp.create();
    }

    private static final JFrame frame = getFrame();
    private static final JTextArea text = getTextArea();
    private static final JPanel panel = getPanel();
    private static final JPanel stopPanel = getPanel();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            "dd.MM.yyyy  HH:mm:ss",
            new Locale("ru", "RU")
    );
    private static final ImageIcon image = getImage();
    private static final SystemApp systemApp;

    public static void main(String[] args) {

        var startButton = new JButton("Запуск системы (после калибровки)");
        var startOrbButton = new JButton("Запуск системы (детектор ORB)");
        var stopButton = new JButton("Стоп");
        var calibrateNew = new JButton("Калибровка камер (видео)");
        var calibratePhoto = new JButton("Калибровка камер (по изображениям)");
        var loadCalibParams = new JButton("Загрузить параметры калибровки из файла");
        var scrollPane = new JScrollPane(text);

        startOrbButton.addActionListener(StartUI::startOrbSystem);
        startButton.addActionListener(StartUI::startSystem);
        stopButton.addActionListener(a -> SystemApp.stopSystem());
        calibrateNew.addActionListener(StartUI::startVideoCalibrate);
        calibratePhoto.addActionListener(StartUI::startPhotoCalibrate);
        loadCalibParams.addActionListener(StartUI::loadParamsAction);

        panel.add(startButton);
        panel.add(startOrbButton);
        panel.add(scrollPane);
//        panel.add(stopButton);
        panel.add(calibrateNew);
        panel.add(calibratePhoto);
        panel.add(loadCalibParams);

        frame.add(panel);
        panel.revalidate();

    }

    private static JFrame getFrame() {
        JFrame jFrame = new JFrame("Построение карты глубины местности");
        jFrame.setVisible(true);
        jFrame.setSize(1200, 700);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return jFrame;
    }

    private static JTextArea getTextArea() {
        JTextArea jTextArea = new JTextArea(10, 60);
        jTextArea.setLineWrap(true);
        jTextArea.setEditable(false);
        return jTextArea;
    }

    private static JPanel getPanel() {
        return new JPanel();
    }

    private static ImageIcon getImage() {
        return new ImageIcon();
    }

    private static void startVideoCalibrate(ActionEvent e) {
        try {
            text.append(MessageFormat.format("\n{0} LOG: Запуск видео каллибровки", LocalDateTime.now().format(formatter)));
/*            VideoCalibration.calibrate();
            CommonCalib.startProcessingAfterCalibration();*/
            OneFileClass.startCalibrate(CalibParameters.VIDEO);
            text.append(MessageFormat.format("\n{0} LOG: Видео каллибровка завершена", LocalDateTime.now().format(formatter)));
        } catch (Exception ex) {
            text.append(MessageFormat.format("\n{0} DEBUG: Видео каллибровка не завершена", LocalDateTime.now().format(formatter)));
            for (var traceElem : ex.getStackTrace()) {
                text.append("\n" + traceElem.toString());
            }
            ex.printStackTrace();
        }
    }

    private static void startPhotoCalibrate(ActionEvent e) {
        try {
            text.append(MessageFormat.format("\n{0} LOG: Запуск фото каллибровки", LocalDateTime.now().format(formatter)));
/*            PhotoCalibration.calibrate();
            CommonCalib.startProcessingAfterCalibration();*/
            OneFileClass.startCalibrate(CalibParameters.PHOTO);
            text.append(MessageFormat.format("\n{0} LOG: Фото каллибровка завершена", LocalDateTime.now().format(formatter)));
        } catch (Exception ex) {
            text.append(MessageFormat.format("\n{0} DEBUG: Фото каллибровка не завершена", LocalDateTime.now().format(formatter)));
            for (var traceElem : ex.getStackTrace()) {
                text.append("\n" + traceElem.toString());
            }
            ex.printStackTrace();
        }
    }

    private static void startOrbSystem(ActionEvent e) {
        try {
            text.append(MessageFormat.format("\n{0} LOG: Запуск системы (детектор ORB)", LocalDateTime.now().format(formatter)));
            OneFileOrb.startSystem();
            text.append(MessageFormat.format("\n{0} LOG: Остановка системы", LocalDateTime.now().format(formatter)));
        } catch (Exception ex) {
            text.append(MessageFormat.format("\n{0} DEBUG: Процесс запуска системы (детектор ORB) выполнен неудачно", LocalDateTime.now().format(formatter)));
            for (var traceElem : ex.getStackTrace()) {
                text.append("\n" + traceElem.toString());
            }
            ex.printStackTrace();
        }
    }

    public static void startSystem(ActionEvent e) {

        try {
            text.append(MessageFormat.format("\n{0} LOG: Запуск системы", LocalDateTime.now().format(formatter)));
//            SystemApp.startVideoCapture();
            OneFileClass.startSystem();
        } catch (Exception exception) {
            text.append(MessageFormat.format("\n{0} DEBUG: Произошел сбой в процессе работы системы.", LocalDateTime.now().format(formatter)));
            for (var elem : exception.getStackTrace()) {
                text.append("\n" + elem);
            }
            exception.printStackTrace();
        }

    }

    public static void loadParamsAction(ActionEvent e) {
        try {
            text.append(MessageFormat.format("\n{0} LOG: Загрузка параметров калибровки", LocalDateTime.now().format(formatter)));
            OneFileClass.startCalibrate(CalibParameters.FILE);
            text.append(MessageFormat.format("\n{0} LOG: Параметры калибровки успешно загружены", LocalDateTime.now().format(formatter)));
        } catch (Exception exception) {
            text.append(MessageFormat.format("\n{0} DEBUG: Произошел сбой в процессе загрузки.", LocalDateTime.now().format(formatter)));
            for (var elem : exception.getStackTrace()) {
                text.append("\n" + elem);
            }
            exception.printStackTrace();
        }
    }

    public static void drawImage(Image image, int num) {
        Graphics g = panel.getGraphics();
        if (num == 1) {
            g.drawImage(image, 0, 400, null);
        } else if (num == 2) {
            g.drawImage(image, 640, 400, null);
        } else {
            g.drawImage(image, 1280, 400, null);
        }
        panel.paintComponents(g);
    }
}
