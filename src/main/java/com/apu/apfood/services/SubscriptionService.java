package com.apu.apfood.services;

import com.apu.apfood.db.dao.SubscriptionDao;
import com.apu.apfood.db.dao.TransactionDao;
import com.apu.apfood.db.models.Subscription;
import java.time.LocalDate;
import java.util.Comparator;

/**
 *
 * @author Alex
 */
public class SubscriptionService {
    
    private final SubscriptionDao subscriptionDao;
    private final TransactionDao transactionDao;
    
    public SubscriptionService(SubscriptionDao subscriptionDao, TransactionDao transactionDao) {
        this.subscriptionDao = subscriptionDao;
        this.transactionDao = transactionDao;
    }
    
    public void addSubscription(int userId) {
        if (!isUserSubscribed(userId)) {
            LocalDate subscriptionStartDate = LocalDate.now();
            LocalDate subscriptionEndDate = subscriptionStartDate.plusMonths(1);

            Subscription subscription = new Subscription(userId, subscriptionStartDate, subscriptionEndDate);
            subscriptionDao.add(subscription);

            transactionDao.writeTransaction(String.valueOf(userId), "-4", "Subscription");
        }
    }
    
    public Subscription getLatestActiveSubscription(int userId) {
        LocalDate today = LocalDate.now();

        // Return the latest active subscription for the given user ID
        return subscriptionDao.getAllSubscriptions().stream()
                              .filter(subscription -> 
                                  subscription.getUserId() == userId &&
                                  !today.isBefore(subscription.getSubscriptionStartDate()) && // Ensure today is not before the start date
                                  !today.isAfter(subscription.getSubscriptionEndDate())) // Ensure today is not after the end date
                              .max(Comparator.comparing(Subscription::getSubscriptionStartDate)) // Find the subscription with the latest start date
                              .orElse(null);
    }
    
    public boolean isUserSubscribed(int userId) {
        LocalDate today = LocalDate.now();
        return subscriptionDao.getAllSubscriptions().stream()
                              .anyMatch(subscription -> subscription.getUserId() == userId &&
                                                        !today.isBefore(subscription.getSubscriptionStartDate()) &&
                                                        !today.isAfter(subscription.getSubscriptionEndDate()));
    }

    public boolean hasUserEverSubscribed(int userId) {
        return subscriptionDao.getAllSubscriptions().stream()
                              .anyMatch(subscription -> subscription.getUserId() == userId);
    }
}
