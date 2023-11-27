package com.apu.apfood.db.seeders;

import com.apu.apfood.helpers.FileHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Alex
 */
public class UserSeeder {
    private static final String BASE_PATH = System.getProperty("user.dir");
    private static final String USER_FILEPATH = BASE_PATH + "/src/main/java/com/apu/apfood/db/datafiles/Users.txt";
    private static final String HEADERS = "id| userId| name| email| password| role\n";
    private static final FileHelper fileHelper = new FileHelper();

    private static final String[][] USERS = {
        {"1", "SYSTEM", "system@system.com", "password", "admin"},
    };

    public static void seed() {
        if (!isFileExistsAndNotEmpty()) {
            createUsers();
            return;
        }

        for (String[] user : USERS) {
            if (!isUserPresent(user[1])) {
                createUser(user);
            }
        }
    }

    private static boolean isFileExistsAndNotEmpty() {
        File userFile = new File(USER_FILEPATH);
        return userFile.exists() && userFile.length() > 0;
    }

    private static boolean isUserPresent(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILEPATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return false;
    }

    private static void createUser(String[] user) {
        String data = user[0] + "| " + user[1] + "| " + user[2] + "| " + user[3] + "\n";
        fileHelper.writeFile(USER_FILEPATH, new File(USER_FILEPATH), HEADERS,true, data);
    }

    private static void createUsers() {
        for (String[] user : USERS) {
            createUser(user);
        }
    }
}
