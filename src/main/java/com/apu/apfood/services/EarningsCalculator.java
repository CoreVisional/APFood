import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EarningsCalculator {

    public static void main(String[] args) {
        // Paths to the input files
        String orderHistoryFilePath = "OrderHistory.txt";
        String deliveryTaskFilePath = "DeliveryTask.txt";

        // Read and parse the data from both files
        List<String[]> orderHistoryData = readDataFromFile(orderHistoryFilePath);
        List<String[]> deliveryTaskData = readDataFromFile(deliveryTaskFilePath);

        // Calculate the earnings for runnerID U04 for the current month
        double totalEarnings = calculateEarnings(orderHistoryData, deliveryTaskData, "U04");

        System.out.println("Total earnings for runnerID U04 this month: $" + totalEarnings);
    }

    public static List<String[]> readDataFromFile(String filePath) {
        List<String[]> data = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\| ");
                data.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static double calculateEarnings(List<String[]> orderHistoryData, List<String[]> deliveryTaskData, String runnerID) {
        double totalEarnings = 0.0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy");

        for (String[] delivery : deliveryTaskData) {
            if (delivery[0].equals(runnerID) && delivery[2].equals("Completed")) {
                String orderID = delivery[3];
                for (String[] order : orderHistoryData) {
                    if (order[0].equals(orderID)) {
                        try {
                            Date orderDate = dateFormat.parse(order[4]);
                            Date currentDate = new Date();
                            if (orderDate.getMonth() == currentDate.getMonth()) {
                                totalEarnings += 3.0; // Assuming $3 for each completed delivery
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return totalEarnings;
    }
}
