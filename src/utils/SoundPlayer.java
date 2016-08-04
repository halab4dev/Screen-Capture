/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

/**
 *
 * @author Apollo
 */
public class SoundPlayer {

    public static void playSound(AudioInputStream inputAudioStream) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(inputAudioStream);
            clip.start();
        } catch (LineUnavailableException | IOException ex) {
            Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
