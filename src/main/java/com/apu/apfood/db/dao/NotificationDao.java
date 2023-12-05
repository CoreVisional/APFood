package com.apu.apfood.db.dao;

import com.apu.apfood.db.enums.NotificationStatus;
import com.apu.apfood.db.enums.NotificationType;
import com.apu.apfood.db.models.Notification;
import com.apu.apfood.db.models.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Alex
 */
public class NotificationDao extends APFoodDao<Notification> {

    private static final String NOTIFICATION_FILEPATH = "/src/main/java/com/apu/apfood/db/datafiles/Notifications.txt";
    private static final String HEADERS = "id| userId| content| status| type\n";

    public NotificationDao() {
        super(NOTIFICATION_FILEPATH, HEADERS);
    }

    public List<Notification> getAllNotifications() {
        List<String[]> rawData = super.getAll();
        return rawData.stream()
                .map(this::deserialize)
                .collect(Collectors.toList());
    }

    public Notification getNotificationById(int id) {
        return getById(id);
    }

    @Override
    protected String serialize(Notification notification) {
        return notification.getUserId() + "| "
                + notification.getContent() + "| "
                + notification.getNotificationStatus().toString() + "| "
                + notification.getNotificationType().toString() + "\n";
    }

    @Override
    protected Notification deserialize(String[] data) {
        int id = Integer.parseInt(data[0].trim());
        int userId = Integer.parseInt(data[1].trim());
        String content = data[2].trim();
        NotificationStatus notificationStatus = NotificationStatus.valueOf(data[3].trim().toUpperCase());
        NotificationType notificationType = NotificationType.valueOf(data[4].trim().toUpperCase());

        return new Notification(id, userId, content, notificationStatus, notificationType);
    }

    @Override
    public void update(Notification notification) {

        List<Notification> notifications = getAllNotifications();
        List<String> serializedNotifications = new ArrayList<>();

        for (Notification existingNotification : notifications) {
            StringBuilder serializedNotificationBuilder = new StringBuilder();
            serializedNotificationBuilder.append(existingNotification.getId()).append("| ");

            if (existingNotification.getId() == notification.getId()) {
                // If the notification matches the one to update, serialize the updated notification
                serializedNotificationBuilder.append(serialize(notification));
            } else {
                // Otherwise, serialize the existing notification
                serializedNotificationBuilder.append(serialize(existingNotification));
            }

            serializedNotifications.add(serializedNotificationBuilder.toString());
        }

        fileHelper.updateFile(filePath, HEADERS, serializedNotifications);
    }

    public List<Notification> getNotificationList() {
        List<String[]> rawData = super.getAll();
        List<Notification> notifications = rawData.stream()
                .map(this::deserialize)
                .collect(Collectors.toList());
        return notifications;
    }

    public void writeNotification(String userId, String content, String status, String type) {
        String notification = userId + "| " + content + "| " + status + "| " + type;
        this.fileHelper.writeFile(filePath, new File(filePath), HEADERS, true, notification);
    }

    public void updateNotificationStatus(int id, NotificationStatus notificationStatus) {
        List<Notification> notifications = getNotificationList();
        try {
            // Open the file with WRITE mode, which truncates the file to size 0
            Files.newBufferedWriter(Path.of(filePath), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        String[] lines = new String[notifications.size()];
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);
            if (notification.getId() == id) {
                notification.setNotificationStatus(notificationStatus);
            }
            String serializedData = serialize(notification);
            fileHelper.writeFile(filePath, new File(filePath), HEADERS, serializedData);
        }
    }
}
