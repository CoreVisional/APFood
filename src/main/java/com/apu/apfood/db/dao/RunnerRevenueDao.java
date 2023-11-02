/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.User;
import com.apu.apfood.helpers.FileHelper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Bryan
 */
public class RunnerRevenueDao extends APFoodDao<User> {

    private static final String BASE_PATH = System.getProperty("user.dir");
    private static final String USER_FILEPATH = "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\RunnerTasks.txt";
    private static final String HEADERS = "id| deliveryRunnerID| status\n";

    FileHelper fileHelper = new FileHelper();

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

    public String checkRevenue(User user) {
        double earnings = 0;

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String row;

            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");

                if (rowArray[1].equals(String.valueOf(user.getId())) && rowArray[3].equals("Completed")) {
                    // Retrieve Order Id and vendor for that userID
                    String orderId = rowArray[4];
                    String vendorName = rowArray[6];

                    FileReader fr2 = new FileReader(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\vendors\\" + vendorName + "\\OrderHistory.txt");
                    BufferedReader br2 = new BufferedReader(fr2);
                    String row2;

                    while ((row2 = br2.readLine()) != null) {
                        String[] rowArray2 = row2.split("\\| ");

                        if (rowArray2[1].equals(orderId)) {
                            // Increment earnings by 3 if order id exist
                            earnings += 3;
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
        // Define the date format to match the format in your data
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yy");

        // Calculate the start date for the past month
        LocalDate currentDate = LocalDate.now();
        LocalDate startDateOfPastMonth = currentDate.minusMonths(months);

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String row;

            br.readLine();
            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");

                if (rowArray[1].equals(String.valueOf(user.getId())) && rowArray[3].equals("Completed")) {

                    // Check if the order date is within the past month
                    // Retrieve Order Id and vendor for that userID
                    String orderId = rowArray[4];
                    String vendorName = rowArray[6];

                    FileReader fr2 = new FileReader(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\vendors\\" + vendorName + "\\OrderHistory.txt");
                    BufferedReader br2 = new BufferedReader(fr2);
                    String row2;

                    br2.readLine();
                    while ((row2 = br2.readLine()) != null) {
                        String[] rowArray2 = row2.split("\\| ");
                        LocalDate orderDate = LocalDate.parse(rowArray2[5], dateFormatter);

                        if (rowArray2[1].equals(orderId) && (orderDate.isAfter(startDateOfPastMonth) || orderDate.isEqual(startDateOfPastMonth))) {
                            // Increment earnings by 3 if order id exists
                            earnings += 3;
                            break;
                        }
                    }

                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(earnings);
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(earnings);
    }
    

    public static void main(String[] args) {
//        String vendorName = "Picante";
//        System.out.println(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\vendors\\" + vendorName + "\\OrderHistory.txt");
//        System.out.println(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\vendors\\" + "VendorUser.txt");

        RunnerRevenueDao rrd = new RunnerRevenueDao();
        rrd.checkPastMonthRevenue(new User(5, "Alice Johnson", "123@123.com", "qweqweqwe".toCharArray(), "Runner"), 1);

    }
}
