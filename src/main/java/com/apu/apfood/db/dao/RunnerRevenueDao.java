/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.User;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.time.LocalDate;

/**
 *
 * @author Bryan
 */
public class RunnerRevenueDao extends APFoodDao<User> {

    private static final String USER_FILEPATH = "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\RunnerDelivery.txt";
    private static final String HEADERS = "id| deliveryRunnerID| status\n";

    public RunnerRevenueDao() {
        super(USER_FILEPATH, HEADERS);
    }

    @Override
    protected String serialize(User user) {
        return user.getName() + "| " + user.getEmail() + "| " + new String(user.getPassword()) + user.getRole() + "\n";
    }

    @Override
    public void update(User user) {
    }

    public String checkTotalRevenue(User user) {
        double earnings = 0;

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String row;

            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");

                // Check RunnerDelivery.txt for any completed tasks
                if (rowArray[2].equals("Completed") && rowArray[4].equals(String.valueOf(user.getId()))) {
                    // Check if the order date is within the past month
                    // Retrieve OrderId and vendor for that RunnerId
                    String orderId = rowArray[1];
                    String vendorName = rowArray[3];

                    FileReader fr2 = new FileReader(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\vendors\\" + vendorName + "\\Orders.txt");
                    BufferedReader br2 = new BufferedReader(fr2);
                    String row2;
                    br2.readLine();
                    while ((row2 = br2.readLine()) != null) {
                        String[] rowArray2 = row2.split("\\| ");
                        String location = rowArray2[11];
                        if (rowArray2[1].equals(orderId)) {
                            switch (location) {
                                case "Block A":
                                    earnings += 5.0;
                                    break;
                                case "Block B":
                                    earnings += 3.0;
                                    break;
                                case "Block D":
                                    earnings += 2.0;
                                    break;
                                case "Block E":
                                    earnings += 2.0;
                                    break;
                                default:
                                    break;
                            }
                            break;
                        }
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(earnings);
    }

    public String checkPastMonthRevenue(User user, int months) {
        double earnings = 0;

        // Calculate the start date for the past month or past year
        // 1 month/12 months
        LocalDate currentDate = LocalDate.now();
        LocalDate startDateOfPastMonth = currentDate.minusMonths(months);

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String row;

            br.readLine();
            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");

                // Check RunnerDelivery.txt for any completed tasks
                if (rowArray[2].equals("Completed") && rowArray[4].equals(String.valueOf(user.getId()))) {

                    // Check if the order date is within the past month
                    // Retrieve OrderId and vendor for that RunnerId
                    String orderId = rowArray[1];
                    String vendorName = rowArray[3];

                    FileReader fr2 = new FileReader(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\vendors\\" + vendorName + "\\Orders.txt");
                    BufferedReader br2 = new BufferedReader(fr2);
                    String row2;

                    br2.readLine();
                    while ((row2 = br2.readLine()) != null) {
                        String[] rowArray2 = row2.split("\\| ");
                        LocalDate orderDate = LocalDate.parse(rowArray2[5]);
                        String location = rowArray2[11];
                        if (rowArray2[1].equals(orderId) && (orderDate.isAfter(startDateOfPastMonth) || orderDate.isEqual(startDateOfPastMonth))) {
                            switch (location) {
                                case "Block A":
                                    earnings += 5.0;
                                    break;
                                case "Block B":
                                    earnings += 3.0;
                                    break;
                                case "Block D":
                                    earnings += 2.0;
                                    break;
                                case "Block E":
                                    earnings += 2.0;
                                    break;
                                default:
                                    break;
                            }
                            break;
                        }
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(earnings);
    }

    public String checkDailyRevenue(User user) {
        double earnings = 0;
        // Calculate the start date for the past month
        LocalDate currentDate = LocalDate.now();

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String row;

            br.readLine();
            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");

                if (rowArray[2].equals("Completed") && rowArray[4].equals(String.valueOf(user.getId()))) {

                    // Retrieve Order Id and vendor for that userID
                    String orderId = rowArray[1];
                    String vendorName = rowArray[3];

                    FileReader fr2 = new FileReader(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\vendors\\" + vendorName + "\\Orders.txt");
                    BufferedReader br2 = new BufferedReader(fr2);
                    String row2;

                    br2.readLine();
                    while ((row2 = br2.readLine()) != null) {
                        String[] rowArray2 = row2.split("\\| ");
                        LocalDate orderDate = LocalDate.parse(rowArray2[5]);
                        String location = rowArray2[11];
                        if (rowArray2[1].equals(orderId) && orderDate.isEqual(currentDate)) {
                            switch (location) {
                                case "Block A":
                                    earnings += 5.0;
                                    break;
                                case "Block B":
                                    earnings += 3.0;
                                    break;
                                case "Block D":
                                    earnings += 2.0;
                                    break;
                                case "Block E":
                                    earnings += 2.0;
                                    break;
                                default:
                                    break;
                            }
                            break;
                        }
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(earnings);
    }

    public static void main(String[] args) {
        RunnerRevenueDao rrd = new RunnerRevenueDao();
        rrd.checkPastMonthRevenue(new User(5, "Alice Johnson", "123@123.com", "qweqweqwe".toCharArray(), "Runner"), 1);

    }
}
