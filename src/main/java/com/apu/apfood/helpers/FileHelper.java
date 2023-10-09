package com.apu.apfood.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 *
 * @author Alex
 */
public class FileHelper {
        public int generateId(String filename, File file) {

        int id = 1;
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            reader.readLine(); // skip first line
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("| ");
                int currId = Integer.parseInt(fields[0].trim());
                if (currId >= id) {
                    id = currId + 1; // increment ID value
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return id;
    }
}
