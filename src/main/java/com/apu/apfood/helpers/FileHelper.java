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
        
        if (!file.exists()) {
            return 1;
        }
        
        int id = 1;

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\| ");
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
    
    public void writeFile(String filename, File file, String headers, boolean hasNewLine, String... varargs) {

        int id = generateID(filename, file);

        // Create file if it does not exist with StandardOpenOption.CREATE.
        // Leaving the StandardOpenOption.CREATE option with only StandardOpenOption.APPEND will result in file not found exception.
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            if (file.length() == 0) {
                writer.write(headers);
                if (hasNewLine) {
                    writer.newLine();
                }
            }

            for (String line : varargs) {
                writer.write(id + "| " + line);
                if (hasNewLine) {
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public void writeFile(String filename, File file, String headers, String... varargs) {
        writeFile(filename, file, headers, false, varargs);
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
    
    public void updateFile(String filePath, String headers, List<String> lines) {
        
        try (BufferedWriter writer = Files.newBufferedWriter(new File(filePath).toPath(), StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(headers);
            for (String line : lines) {
                writer.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
