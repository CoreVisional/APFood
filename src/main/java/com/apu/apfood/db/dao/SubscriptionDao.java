package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Bryan
 */
public class SubscriptionDao extends APFoodDao<User> {

    private static final String USER_FILEPATH = "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\Subscriptions.txt";
    private static final String HEADERS = "id| userId| subscriptionStartDate| subscriptionEndDate\n";
    private final TransactionDao transactionDao = new TransactionDao();

    public SubscriptionDao() {
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

    public void addSubscription(String userId) {

        LocalDate subscriptionStartDate = LocalDate.now();
        LocalDate subscriptionEndDate = subscriptionStartDate.plusMonths(1);
        String subscriptionDeductionAmount = "-4";

        fileHelper.writeFile(filePath, new File(filePath), HEADERS, true, userId + "| " + subscriptionStartDate + "| " + subscriptionEndDate);
        transactionDao.writeTransaction(userId, subscriptionDeductionAmount, "Subscription");

    }

    public boolean checkUserSubscription(String userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip the header line

            while ((line = br.readLine()) != null) {
                String[] subscription = line.split("\\| ");
                if (subscription[1].equals(userId)) {
                    LocalDate startDate = LocalDate.parse(subscription[2], formatter);
                    LocalDate endDate = LocalDate.parse(subscription[3], formatter);

                    // Checking if today's date is within the subscription period
                    return !today.isBefore(startDate) && !today.isAfter(endDate);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If user ID not found or no active subscription found
        return false;
    }

    public static void main(String[] args) {
        SubscriptionDao sd = new SubscriptionDao();
        sd.addSubscription("3");

    }
}
