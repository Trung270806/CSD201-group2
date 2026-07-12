import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateAccountsCSV {
    public static void main(String[] args) {
        String csvFile = "data/accounts_10k.csv";
        int totalRows = 10000;
        Random rand = new Random();

        // Ensure parent directory exists
        File file = new File(csvFile);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            // Write CSV Header
            writer.write("AccountNumber,Balance");
            writer.newLine();

            // Generate 10,000 accounts
            for (int i = 0; i < totalRows; i++) {
                String accNum = String.format("ACC%06d", 100000 + i);
                
                // Random balance between 10,000.00 and 1,000,000.00 VND
                double balance = 10000.0 + rand.nextDouble() * 990000.0;
                
                // Format decimal to 2 decimal places
                writer.write(String.format(java.util.Locale.US, "%s,%.2f", accNum, balance));
                writer.newLine();
            }
            System.out.println("====================================================");
            System.out.println("SUCCESS: Generated 10,000 accounts successfully!");
            System.out.println("File saved to: " + file.getAbsolutePath());
            System.out.println("====================================================");
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }
}
