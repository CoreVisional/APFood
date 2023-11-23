
package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author Bryan
 */
public class TransactionDao extends APFoodDao<User> {

    private static final String USER_FILEPATH = "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\Transactions.txt";
    private static final String HEADERS = "id| userId| amount| date| time| remark\n";

    public TransactionDao() {
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

    public void writeTransaction(String userId, String amount, String remark) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        fileHelper.writeFile(filePath, new File(filePath), HEADERS, userId + "| " + amount + "| " + currentDate + "| " + currentTime + "| " + remark);
    }

    public String getTotalBalance(String inputUserId) {
        double totalAmount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Read and ignore the header line

            while ((line = br.readLine()) != null) {
                String[] rowArray = line.split("\\| ");
                String userId = rowArray[1];
                if (userId.equals(inputUserId)) {
                    double amount = Double.parseDouble(rowArray[2]);
                    totalAmount += amount;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(totalAmount);
    }

    public double calculateAmountSaved(String userId) {
        double totalDiscountAmount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip the header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\| ");
                String currentUserId = data[1];
                String remark = data[5];

                if (currentUserId.equals(userId) && remark.equals("Discount")) {
                    double amount = Double.parseDouble(data[2]);
                    totalDiscountAmount += amount;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return totalDiscountAmount;
    }

    public static void main(String[] args) {
        TransactionDao td = new TransactionDao();
        System.out.println(td.calculateAmountSaved("2"));
    }
}
