package com.apu.apfood.helpers;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class ImageHelper {
    public void setFrameIcon(JFrame frame, String filepath) {
        URL resource = getClass().getResource(filepath);
        ImageIcon icon = new ImageIcon(resource);
        Image image = icon.getImage();
        frame.setIconImage(image);
    }
}
