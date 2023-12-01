package com.apu.apfood.db.dao;

import static com.apu.apfood.db.dao.APFoodDao.BASE_PATH;
import com.apu.apfood.db.models.User;
import com.apu.apfood.exceptions.CustomValidationException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        return String.valueOf(user.getId()) + "| " + user.getName() + "| " + user.getEmail() + "| " + new String(user.getPassword()) + "| " + user.getRole() + "\n";
    }

    @Override
    protected User deserialize(String[] data) {
        return null;
    }

    @Override
    public void update(User user) {

    }

    public String getUserName(String userId) {
        String name = "";
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fileReader);
            br.readLine(); // Skip first row
            String row;

            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");
                if (rowArray[1].equals(userId)) {
                    name = rowArray[2];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public String getUserId(String customerName) {
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

    public String getUserId(String orderId, String vendorName) {
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

    public Object[][] getUsers(boolean manageUsers) {
        // Declare 2D array
        Object[][] allUsers;

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
                String id = values[1];
                String name = values[2];
                String email = values[3];
                String password = values[4];
                String role = values[5];

                // Populate table row
                if (manageUsers && !role.equals("admin")) {
                    String[] row = {id, name, email, password, role};
                    rows.add(row);
                } else if (!manageUsers) {
                    String[] row = {id, name, email, role};
                    rows.add(row);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert the list of matching rows to a 2D array
        allUsers = new Object[rows.size()][4];
        for (int i = 0; i < rows.size(); i++) {
            allUsers[i] = rows.get(i);
        }
        return allUsers;
    }

    public Object[][] getUsers() {
        return getUsers(false);
    }

    public Object[][] getAllVendor() {
        // Create a set to store unique vendor names
        Set<String> uniqueVendors = new HashSet<>();
        String vendorUsersFilePath = BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\VendorUsers.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(vendorUsersFilePath))) {
            br.readLine(); // Skip header
            String line;

            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\|");

                if (values.length > 2) {
                    // Extract the vendor name from index 2 and add it to the set
                    uniqueVendors.add(values[2].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }

        // Convert the set of unique vendors to a 2D array
        Object[][] allVendor = new Object[uniqueVendors.size()][1];
        int i = 0;
        for (String vendor : uniqueVendors) {
            allVendor[i++][0] = vendor;
        }

        return allVendor;
    }

    public void removeUserById(String inputId) {
        List<String> updatedLines = new ArrayList<>();

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);

            // Read the header row and add it to the updated lines
            updatedLines.add(br.readLine());

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\| ");

                String id = values[1];

                // Exclude the line if it matches the ID to delete
                if (id.equals(inputId)) {
                    continue;
                }

                updatedLines.add(line);
            }
            br.close();

            FileWriter fw = new FileWriter(filePath, false);
            BufferedWriter bw = new BufferedWriter(fw);

            // Write the updated lines (excluding the line with the deleted ID) to the file
            for (String updatedLine : updatedLines) {
                bw.write(updatedLine);
                bw.newLine();
            }

            // Close the BufferedWriter to save the changes
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void modifyUser(String inputUserId, String inputUserName, String inputUserEmail, String userInputPassword) {
        List<String> updatedLines = new ArrayList<>();

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);

            // Read the header row and add it to the updated lines
            updatedLines.add(br.readLine());

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\| ");

                String id = values[0];
                String userId = values[1];
                String userName = values[2];
                String userEmail = values[3];
                String userPassword = values[4];
                String userRole = values[5];

                // Exclude the line if it matches the ID to delete
                if (userId.equals(inputUserId)) {
                    userName = inputUserName;
                    userEmail = inputUserEmail;
                    userPassword = userInputPassword;
                }

                updatedLines.add(id + "| " + userId + "| " + userName + "| " + userEmail + "| " + userPassword + "| " + userRole);
            }
            br.close();

            FileWriter fw = new FileWriter(filePath, false);
            BufferedWriter bw = new BufferedWriter(fw);

            // Write the updated lines (excluding the line with the deleted ID) to the file
            for (String updatedLine : updatedLines) {
                bw.write(updatedLine);
                bw.newLine();
            }

            // Close the BufferedWriter to save the changes
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

            if (!email.matches(emailPattern)) {
                throw new CustomValidationException("Invalid email format");
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

    public void createNewVendorFolder(String VendorName) {
        String newVendorTextFilePath = BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\vendors";

        // Create a File object representing the folder
        File folder = new File(newVendorTextFilePath + "\\" + VendorName);
        if (!folder.exists()) {
            boolean folderCreated = folder.mkdirs();
            if (folderCreated) {
                System.out.println("Folder created successfully");
            } else {
                System.out.println("Failed to create folder");
                return;
            }
        } else {
            System.out.println("Folder already exists");
        }
    }

    public void addVendorUser(String customerName, String inputVendorName) {
        String customerId = UserDao.this.getUserId(customerName);
        String vendorUserFilePath = BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\VendorUsers.txt";

        fileHelper.writeFile(vendorUserFilePath, new File(vendorUserFilePath), "id| userId| vendor\n", true, customerId + "| " + inputVendorName);
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
