package com.apu.apfood.db.dao;

import static com.apu.apfood.db.dao.APFoodDao.BASE_PATH;
import com.apu.apfood.db.models.User;
import com.apu.apfood.exceptions.CustomValidationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alex
 */
public class UserDao extends APFoodDao<User> {

    private static final String USER_FILEPATH = "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\Users.txt";
    private static final String HEADERS = "id| userId| name| email| password| role\n";
    private TransactionDao transactionDao = new TransactionDao();

    public UserDao() {
        super(USER_FILEPATH, HEADERS);
    }

    @Override
    protected String serialize(User user) {
        return String.valueOf(user.getId()) + "| " + user.getName() + "| " + user.getEmail() + "| " + new String(user.getPassword()) + "| " + user.getRole();
    }

    @Override
    public void update(User user) {

    }

    public String getCustomerName(String accountId) {
        String name = "";
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fileReader);
            br.readLine(); // Skip first row
            String row;

            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");
                if (rowArray[1].equals(accountId)) {
                    name = rowArray[2];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public String getCustomerId(String customerName) {
        String customerId = "";
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String row;

            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");

                if (rowArray[2].equals(customerName)) {
                    customerId = rowArray[1];
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerId;
    }

    public String getCustomerId(String orderId, String vendorName) {
        String customerId = "";
        try {
            FileReader fr = new FileReader(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\vendors\\" + vendorName + "\\Orders.txt");
            BufferedReader br = new BufferedReader(fr);
            String row;

            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");

                if (rowArray[1].equals(orderId)) {
                    customerId = rowArray[2];
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return customerId;
    }

    public void validateCredentials(String name, String password, String email, String role) throws CustomValidationException {

        try (FileReader fr = new FileReader(filePath); BufferedReader br = new BufferedReader(fr)) {

            String row;
            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");

                // Check duplication
                if (rowArray[2].equals(name)) {
                    throw new CustomValidationException("Name already exists");
                } else if (rowArray[3].equals(email)) {
                    throw new CustomValidationException("Email already exists");
                }
            }

            // Other validation checks
            if (name.length() < 3 || name.length() > 20 || !name.matches("^[a-zA-Z ]+$")) {
                throw new CustomValidationException("Invalid name format or length");
            }
            if (password.length() < 6 || password.length() > 20) {
                throw new CustomValidationException("Password should be between 6 and 20 characters");
            }
        } catch (IOException e) {
            throw new CustomValidationException("Couldn't read file", e);
        }
    }

    public boolean checkVendorExists(String inputVendorName) throws CustomValidationException {
        String vendorUserFilePath = BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\VendorUsers.txt";

        try (FileReader fr = new FileReader(vendorUserFilePath); BufferedReader br = new BufferedReader(fr)) {

            String row;
            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");

                // Check duplication
                if (rowArray[2].equals(inputVendorName)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new CustomValidationException("Couldn't read file", e);
        }
        return false;
    }

    public void addVendorUser(String customerName, String inputVendorName) {
        String customerId = getCustomerId(customerName);
        String vendorUserFilePath = BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\VendorUsers.txt";

        fileHelper.writeFile(vendorUserFilePath, new File(vendorUserFilePath), "id| userId| vendor\n", customerId + "| " + inputVendorName);
    }

    public Object[][] getCustomerCreditDetails() {

        // Declare 2D array
        Object[][] customerCreditDetails;

        // Create an empty list to store matching rows
        List<String[]> rows = new ArrayList<>();

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] rowArray = line.split("\\| ");
                // Retrieve orderid, vendor name, delivery location
                if (rowArray[5].equals("customer")) {
                    String userId = rowArray[1];
                    String customerName = rowArray[2];
                    String currentBalance = transactionDao.getTotalBalance(userId);
                    // Populate table row
                    String[] row = {userId, customerName, currentBalance};
                    rows.add(row);
                }

            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert the list of matching rows to a 2D array
        customerCreditDetails = new Object[rows.size()][8];
        for (int i = 0; i < rows.size(); i++) {
            customerCreditDetails[i] = rows.get(i);
        }

        return customerCreditDetails;
    }

}
