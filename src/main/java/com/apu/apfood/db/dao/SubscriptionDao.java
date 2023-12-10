package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.Subscription;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author Alex
 */
public class SubscriptionDao extends APFoodDao<Subscription> {

    private static final String SUBSCRIPTIONS_FILEPATH = "/src/main/java/com/apu/apfood/db/datafiles/Subscriptions.txt";
    private static final String HEADERS = "id| userId| subscriptionStartDate| subscriptionEndDate\n";

    public SubscriptionDao() {
        super(SUBSCRIPTIONS_FILEPATH, HEADERS);
    }

    public List<Subscription> getAllSubscriptions() {

        List<String[]> rawData = super.getAll();

        return rawData.stream()
                       .map(this::deserialize)
                       .collect(Collectors.toList());
    }

    @Override
    protected String serialize(Subscription subscription) {
        return subscription.getUserId() + "| " +
               subscription.getSubscriptionStartDate() + "| " +
               subscription.getSubscriptionEndDate() + "| " + "\n";
    }

    @Override
    protected Subscription deserialize(String[] data) {
        int id = Integer.parseInt(data[0].trim());
        int userId = Integer.parseInt(data[1].trim());
        LocalDate subscriptionStartDate = LocalDate.parse(data[2].trim());
        LocalDate subscriptionEndDate = LocalDate.parse(data[3].trim());

        return new Subscription(id, userId, subscriptionStartDate, subscriptionEndDate);
    }

    @Override
    public void update(Subscription subscription) {

    }
}