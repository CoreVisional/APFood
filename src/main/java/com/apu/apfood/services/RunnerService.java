/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.services;

import com.apu.apfood.db.dao.RunnerDao;
import com.apu.apfood.db.models.User;

/**
 *
 * @author Bryan
 */
public class RunnerService {

    private User runner;

    public RunnerService(User runner) {
        this.runner = runner;
    }
    
    public Object[][] getDeliveryHistory () {
        RunnerDao rd = new RunnerDao ();
        return rd.getDeliveryHistory(this.runner);
    }
}
