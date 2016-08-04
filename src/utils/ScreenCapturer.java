/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Apollo
 */
public class ScreenCapturer {

    private static final Rectangle SCREEN_RECTANGLE = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

    public static void createScreenCapture(File output, String format) throws RuntimeException {
        try {
            BufferedImage capture = new Robot().createScreenCapture(SCREEN_RECTANGLE);
            output.mkdirs();
            ImageIO.write(capture, format, output);
        } catch (IOException | AWTException ex) {
            Logger.getLogger(ScreenCapturer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
