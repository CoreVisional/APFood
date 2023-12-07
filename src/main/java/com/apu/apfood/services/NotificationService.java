package com.apu.apfood.services;

import com.apu.apfood.db.dao.NotificationDao;
import com.apu.apfood.db.models.Notification;
import java.util.List;

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
        notification.setOrderId(extractOrderIdFromContent(content));
        notification.setVendorName(extractVendorNameFromContent(content));
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
}
