package com.apu.apfood.services;

import com.apu.apfood.db.dao.TransactionDao;
import com.apu.apfood.db.models.Transaction;
import java.text.DecimalFormat;
import java.util.List;

/**
 *
 * @author Alex
 */
public class TransactionService {
    
    private final TransactionDao transactionDao;
    
    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }
    
    public List<Transaction> getTransactions() {
        return transactionDao.getAllTransactions();
    }
    
    public String getTotalBalance(String inputUserId) {
        double totalAmount = 0;
        List<Transaction> allTransactions = transactionDao.getAllTransactions();

        for(Transaction transaction : allTransactions) {
            if (String.valueOf(transaction.getUserId()).equals(inputUserId)) {
                totalAmount += transaction.getAmount();
            }
        }

        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(totalAmount);
    }
}
