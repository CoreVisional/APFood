package com.apu.apfood.helpers;

import javax.swing.JFrame;

public class GUIHelper {

        // Center, Display and Focus Jframe
        public static void JFrameSetup(JFrame jframe) {
            // Center the JFrame
            jframe.setLocationRelativeTo(null);
            jframe.setVisible(true);
            // Auto focus on Jframe content pane
            jframe.getContentPane().requestFocusInWindow();
        }
}
