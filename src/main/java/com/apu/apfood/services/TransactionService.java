package com.apu.apfood.services;

import com.apu.apfood.db.dao.TransactionDao;
import com.apu.apfood.db.models.Transaction;
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
}
