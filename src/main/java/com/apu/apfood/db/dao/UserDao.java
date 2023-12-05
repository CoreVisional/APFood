package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.User;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author Alex
 */
public class UserDao extends APFoodDao<User> {

    private static final String BASE_PATH = System.getProperty("user.dir");
    private static final String USER_FILEPATH = "/src/main/java/com/apu/apfood/db/datafiles/Users.txt";
    private static final String HEADERS = "id| name| email| password| role\n";

    public UserDao() {
        super(USER_FILEPATH, HEADERS);
    }
    
    public User getUserById(int id) {
        return getById(id);
    }

    @Override
    protected String serialize(User user) {
        return user.getName() + "| " + user.getEmail() + "| " + new String(user.getPassword()) + user.getRole() + "\n";
    }

    @Override
    protected User deserialize(String[] data) {
        int id = Integer.parseInt(data[0].trim());
        String name = data[2].trim();
        String email = data[3].trim();
        char[] password = data[4].trim().toCharArray();
        String role = data[5].trim();

        return new User(id, name, email, password, role);
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
}
