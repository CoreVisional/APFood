/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.gui.auth;

import javax.swing.JFrame;

/**
 *
 * @author Bryan
 */
public class AuthenticationManager {

    public void logout(JFrame frame) {
        LoginForm login = new LoginForm();
        login.setVisible(true);
        frame.dispose();
    }
}