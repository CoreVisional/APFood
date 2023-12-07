package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.Transaction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Bryan
 */
public class TransactionDao extends APFoodDao<Transaction> {

    private static final String TRANSACTION_FILEPATH = "/src/main/java/com/apu/apfood/db/datafiles/Transactions.txt";
    private static final String HEADERS = "id| userId| amount| date| time| remark\n";

    public TransactionDao() {
        super(TRANSACTION_FILEPATH, HEADERS);
    }
    
    public List<Transaction> getAllTransactions() {
        List<String[]> rawData = super.getAll();
        return rawData.stream()
                      .map(this::deserialize)
                      .collect(Collectors.toList());
    }

    @Override
    protected String serialize(Transaction transaction) {
        return transaction.getUserId() + "| " +
               transaction.getAmount() + "| " +
               transaction.getTransactionOn() + "| " +
               transaction.getTransactionAt() + "| " +
               transaction.getRemarks() + "\n";
    }
    
    @Override
    protected Transaction deserialize(String[] data) {
        int id = Integer.parseInt(data[0].trim());
        int userId = Integer.parseInt(data[1].trim());
        double amount = Double.parseDouble(data[2].trim());
        LocalDate transactionDate = LocalDate.parse(data[3].trim());
        LocalTime transactionTime = LocalTime.parse(data[4].trim());
        String remarks = data[5].trim();
        
        return new Transaction(id, userId, amount, transactionDate, transactionTime, remarks);
    }

    @Override
    public void update(Transaction transaction) {

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
            e.printStackTrace(System.out);
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
            e.printStackTrace(System.out);
        }

        return totalDiscountAmount;
    }
}
