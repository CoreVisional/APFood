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
    
    public List<Notification> getNotifications() {
        return notificationDao.getAllNotifications();
    }

    public Notification getNotificationById(int id) {
        return notificationDao.getNotificationById(id);
    }

    public void updateNotification(Notification notification) {
        notificationDao.update(notification);
    }
}
