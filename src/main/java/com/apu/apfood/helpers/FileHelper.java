package com.apu.apfood.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alex
 */
public class FileHelper {
    public int generateID(String filename, File file) {

        int id = 1;
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("| ");
                int currId = Integer.parseInt(fields[0].trim());
                if (currId >= id) {
                    id = currId + 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return id;
    }
        
    public void writeFile(String filename, File file, String headers, String... varargs) {
        
        int id = generateID(filename, file);
        
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            if (file.length() == 0) {
                writer.write(headers);
            }
            for (String line : varargs) {
                writer.write(id + "| " + line);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
    
    public List<String[]> readFile(String filename) {
        List<String[]> dataList = new ArrayList();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\| ");
                dataList.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        
        return dataList;
    }
}
