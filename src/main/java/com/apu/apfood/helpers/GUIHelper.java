package com.apu.apfood.helpers;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUIHelper {

    // Center, Display and Focus Jframe
    public static void JFrameSetup(JFrame jframe) {
        // Center the JFrame
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);
        // Auto focus on Jframe content pane
        jframe.getContentPane().requestFocusInWindow();
    }
        
    public void panelSwitcher(JButton button, JPanel parentPanel, String targetPanel) {
        button.addActionListener((ActionEvent e) -> {
            CardLayout card = (CardLayout)parentPanel.getLayout();
            card.show(parentPanel, targetPanel);
        });
    }
    
    public void panelSwitcher(JPanel parentPanel, String targetPanel) {
        CardLayout card = (CardLayout) parentPanel.getLayout();
        card.show(parentPanel, targetPanel);
    }
}
