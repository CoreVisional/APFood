/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.User;
import com.apu.apfood.helpers.FileHelper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bryan
 */
public class RunnerAvailabilityDao extends APFoodDao<User> {

    private static final String USER_FILEPATH = "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\RunnerAvailability.txt";
    private static final String HEADERS = "id| deliveryRunnerId| status\n";
    private final UserDao userDao = new UserDao();

    FileHelper fileHelper = new FileHelper();

    public RunnerAvailabilityDao() {
        super(USER_FILEPATH, HEADERS);
    }

    @Override
    protected String serialize(User user) {
        return user.getName() + "| " + user.getEmail() + "| " + new String(user.getPassword()) + user.getRole() + "\n";
    }

    @Override
    public void update(User user) {
    }

    public Object[][] getAllRunnerAvailability() {
        // Declare 2D array
        Object[][] runnerAvailability;

        // Create an empty list to store matching rows
        List<String[]> rows = new ArrayList<>();

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\| ");

                // Retrieve orderid, vendor name, delivery location
                String id = values[0];
                String runnerId = values[1];
                String status = values[2];
                String runnerName = userDao.getUserName(runnerId);
                // Populate table row
                String[] row = {runnerName, runnerId, status};
                rows.add(row);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert the list of matching rows to a 2D array
        runnerAvailability = new Object[rows.size()][3];
        for (int i = 0; i < rows.size(); i++) {
            runnerAvailability[i] = rows.get(i);
        }

        return runnerAvailability;
    }

    public String getAvailability(User user) {
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\| ");
                if (values[1].equals(String.valueOf(user.getId()))) {
                    return values[2];
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addNewRunnerAvailability(String userId) {
        fileHelper.writeFile(filePath, new File(filePath), HEADERS, true, userId + "| Available");
    }

    public void updateAvailability(User user, String newAvailability) {

        List<String> updatedLines = new ArrayList<>();

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            updatedLines.add(br.readLine()); // Add header row

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\| ");
                int id = Integer.parseInt(values[0]);
                String deliveryRunnerID = values[1];
                String availability = values[2];
                if (values[1].equals(String.valueOf(user.getId()))) {
                    availability = newAvailability;
                }
                updatedLines.add(id + "| " + deliveryRunnerID + "| " + availability);
            }
            br.close();

            FileWriter fw = new FileWriter(filePath, false);
            BufferedWriter bw = new BufferedWriter(fw);

            // Write the updated lines to the file
            for (String updatedLine : updatedLines) {
                bw.write(updatedLine);
                bw.newLine(); // Add a newline character to separate lines
            }

            // Close the BufferedWriter to save the changes
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
