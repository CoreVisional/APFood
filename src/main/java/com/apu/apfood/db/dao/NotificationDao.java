package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.User;
import java.io.File;

/**
 *
 * @author Alex
 */
public class NotificationDao extends APFoodDao<User> {

    private static final String USER_FILEPATH = "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\Notifications.txt";
    private static final String HEADERS = "id| userId| content| status| type\n";

    public NotificationDao() {
        super(USER_FILEPATH, HEADERS);
    }

    @Override
    protected String serialize(User user) {
        return user.getName() + "| " + user.getEmail() + "| " + new String(user.getPassword()) + user.getRole() + "\n";
    }

    @Override
    public void update(User user) {
    }

    public void writeNotification(String userId, String content, String status, String type) {
        String notification = userId + "| " + content + "| " + status + "| " + type;
        this.fileHelper.writeFile(filePath, new File(filePath), HEADERS, true, notification);
    }

}
