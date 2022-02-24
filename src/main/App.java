package main;

import org.opencv.core.Core;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import system.SystemApp;
import test.RunTestSystem;
import ui.ImageFrame;

import java.io.IOException;

public class App {


    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Hello, OpenCV");

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        RunTestSystem.startTests();

        System.out.println("_____________________________СТАРТ РАБОТЫ________________________________________");
        var sys = SystemApp.create();
/*        var frame = new ImageFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);*/
        sys.startSystem(Imgcodecs.imread("resources/test/left.png"), Imgcodecs.imread("resources/test/right.png"));
    }
}
