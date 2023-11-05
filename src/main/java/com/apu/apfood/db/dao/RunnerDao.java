/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.User;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bryan
 */
public class RunnerDao extends APFoodDao<User> {

    private static final String USER_FILEPATH = "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\RunnerTasks.txt";
    private static final String HEADERS = "id| deliveryRunnerID| location| status| orderID| deliveryFeedback| vendor\n";

    public RunnerDao() {
        super(USER_FILEPATH, HEADERS);
    }

    @Override
    protected String serialize(User user) {
        return user.getName() + "| " + user.getEmail() + "| " + new String(user.getPassword()) + user.getRole() + "\n";
    }

    @Override
    public void update(User user) {

    }

    public Object[][] getDeliveryHistory(User user) {

        // Declare 2D array
        Object[][] deliveryHistory;

        // Create an empty list to store matching rows
        List<String[]> rows = new ArrayList<>();

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\| ");
                if (values[1].equals(String.valueOf(user.getId())) && values[3].equals("Accepted")) {
                    String[] row = {values[2], values[3], values[5], values[6]};
                    rows.add(row);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert the list of matching rows to a 2D array
        deliveryHistory = new Object[rows.size()][5];
        for (int i = 0; i < rows.size(); i++) {
            deliveryHistory[i] = rows.get(i);
        }

        return deliveryHistory;
    }


}
