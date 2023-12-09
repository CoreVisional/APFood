package com.apu.apfood.services;

import com.apu.apfood.db.dao.NotificationDao;
import com.apu.apfood.db.models.Notification;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Alex
 */
public class NotificationService {

    private final NotificationDao notificationDao;

    public NotificationService(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    public void addNotification(Notification notification) {
        notificationDao.add(notification);
    }

    public List<Notification> getNotifications() {
        List<Notification> notifications = notificationDao.getAllNotifications();
        notifications.forEach(this::extractDetailsFromContent);
        return notifications;
    }

    public Notification getNotificationById(int id) {
        Notification notification = notificationDao.getNotificationById(id);
        extractDetailsFromContent(notification);
        return notification;
    }

    public void updateNotification(Notification notification) {
        notificationDao.update(notification);
    }

    private void extractDetailsFromContent(Notification notification) {
        String content = notification.getContent();
        if (content.toLowerCase().contains("credit top up")) {
            notification.setTransactionId(extractTransactionIdFromContent(content));
            notification.setExtractedUserId(extractUserIdFromContent(content));
        } else {
            notification.setOrderId(extractOrderIdFromContent(content));
            notification.setVendorName(extractVendorNameFromContent(content));
        }
    }

    private String extractOrderIdFromContent(String content) {
        String orderPrefix = "[order id: ";
        int start = content.indexOf(orderPrefix);
        if (start == -1) {
            return "";
        }
        start += orderPrefix.length();
        int end = content.indexOf(",", start);
        if (end == -1) {
            return "";
        }
        return content.substring(start, end);
    }

    private String extractVendorNameFromContent(String content) {
        String vendorPrefix = "vendor name: ";
        int start = content.indexOf(vendorPrefix);
        if (start == -1) {
            return "";
        }
        start += vendorPrefix.length();
        int end = content.indexOf("]", start);
        if (end == -1) {
            return "";
        }
        return content.substring(start, end).trim();
    }

    public static String extractUserId(String input) {
        String userId = null;
        Pattern pattern = Pattern.compile("user id: (\\d+)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            userId = matcher.group(1);
        }

        return userId;
    }

    public static String extractAmount(String input) {
        String amount = null;
        Pattern pattern = Pattern.compile("amount: RM ([\\d.]+)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            amount = matcher.group(1);
        }

        return amount;

    }

    private int extractTransactionIdFromContent(String content) {
        String transactionIdPrefix = "transaction id: ";
        int start = content.indexOf(transactionIdPrefix);
        if (start == -1) {
            return 0;
        }
        start += transactionIdPrefix.length();
        int end = content.indexOf("]", start);
        if (end == -1) {
            return 0;
        }

        try {
            return Integer.parseInt(content.substring(start, end).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int extractUserIdFromContent(String content) {
        String userIdPrefix = "user id: ";
        int start = content.indexOf(userIdPrefix);
        if (start == -1) {
            return 0;
        }
        start += userIdPrefix.length();
        int end = content.indexOf(",", start);
        if (end == -1) {
            return 0;
        }

        try {
            return Integer.parseInt(content.substring(start, end).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
