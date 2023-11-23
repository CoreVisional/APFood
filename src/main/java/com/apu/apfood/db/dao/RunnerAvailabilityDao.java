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
    private static final String HEADERS = "id| deliveryRunnerID| status\n";

    FileHelper fileHelper = new FileHelper();

    public RunnerAvailabilityDao() {
        super(USER_FILEPATH, HEADERS);
    }

    @Override
    protected String serialize(User user) {
        return user.getName() + "| " + user.getEmail() + "| " + new String(user.getPassword()) + user.getRole() + "\n";
    }
    
    @Override
    protected User deserialize(String[] data) {
        return null;
    }

    @Override
    public void update(User user) {
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

    public void addNewRunnerAvailability(User user) {
        fileHelper.writeFile(filePath, new File(filePath), HEADERS, String.valueOf(user.getId()) + "| Available");
    }

    public void updateAvailability(User user, String newAvailability) {

        List<String> updatedLines = new ArrayList<>();

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            updatedLines.add( br.readLine()); // Add header row
            
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

//        String[] updatedLinesArray = updatedLines.toArray(new String[0]);
    }
}
