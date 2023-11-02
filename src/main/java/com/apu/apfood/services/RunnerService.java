/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.services;

import com.apu.apfood.db.dao.RunnerDao;
import com.apu.apfood.db.dao.RunnerAvailabilityDao;
import com.apu.apfood.db.dao.RunnerRevenueDao;
import com.apu.apfood.db.models.User;
import javax.swing.JRadioButton;

/**
 *
 * @author Bryan
 */
public class RunnerService {

    private User runner;
    RunnerAvailabilityDao runnerAvailabilityDao = new RunnerAvailabilityDao();
    RunnerRevenueDao runnerRevenueDao = new RunnerRevenueDao();

    public RunnerService(User runner) {
        this.runner = runner;
    }

    public Object[][] getDeliveryHistory() {
        RunnerDao rd = new RunnerDao();
        return rd.getDeliveryHistory(this.runner);
    }

    public void setAvailabilityRadioButton(User user, JRadioButton availableBtn, JRadioButton unavailableBtn) {
        String status = runnerAvailabilityDao.getAvailability(user);
        if (status == null) {
            runnerAvailabilityDao.addNewRunnerAvailability(user);
            unavailableBtn.setSelected(true);
        } else if (status.equals("unavailable")) {
            unavailableBtn.setSelected(true);
        } else {
            availableBtn.setSelected(true);
        }
    }

    public void modifyAvailability(String status) {
        String availability;
        if (status.equals("Yes")) {
            availability = "available";
        } else {
            availability = "unavailable";
        }
        runnerAvailabilityDao.updateAvailability(runner, availability);
    }

    public String getTotalRevenue(User user) {
        return runnerRevenueDao.checkRevenue(user);
    }

    public String getRevenue(User user, int months) {
        return runnerRevenueDao.checkPastMonthRevenue(user, months);
        
    }

}
