/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.awt.event.MouseEvent;
import org.jnativehook.mouse.NativeMouseEvent;

/**
 *
 * @author Apollo
 */
public class MouseEventConverter {

    public static int convertToMouseEventButton(int nativeMouseButon) {
        if (nativeMouseButon == NativeMouseEvent.BUTTON1) {
            return MouseEvent.BUTTON1;
        }
        if (nativeMouseButon == NativeMouseEvent.BUTTON3) {
            return MouseEvent.BUTTON2;
        }
        return MouseEvent.BUTTON3;
    }

    public static int convertToNativeMouseEventButton(int mouseEventButton) {
        if (mouseEventButton == MouseEvent.BUTTON1) {
            return NativeMouseEvent.BUTTON1;
        }
        if (mouseEventButton == MouseEvent.BUTTON2) {
            return NativeMouseEvent.BUTTON3;
        }
        return NativeMouseEvent.BUTTON2;
    }
}
