import com.bank.service.BankService;
import com.bank.service.DataGenerator;
import com.bank.ui.ConsoleMenu;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Prepare storage directory paths
        String currentDir = System.getProperty("user.dir");
        String dataDir = currentDir + File.separator + "data";
        
        // Ensure data directory exists
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String accountsPath = dataDir + File.separator + "accounts.csv";
        String transactionsPath = dataDir + File.separator + "transactions.csv";

        File accFile = new File(accountsPath);
        File txFile = new File(transactionsPath);

        if (!accFile.exists() || !txFile.exists()) {
            // Instantly generate 10,000 simulation records at startup
            System.out.println("====================================================");
            System.out.println("Instantly generating 10,000 accounts & transactions...");
            System.out.println("Please wait a moment...");
            boolean success = DataGenerator.generateData(10000, accountsPath, transactionsPath);
            if (success) {
                System.out.println("10,000 mock records generated successfully!");
            } else {
                System.err.println("Warning: Failed to generate mock records at startup.");
            }
            System.out.println("====================================================\n");
        } else {
            System.out.println("[+] Found existing ledger files. Skipping initial data generation to preserve updates.\n");
        }

        // Initialize Services & CLI Menu
        BankService bankService = new BankService();
        ConsoleMenu menu = new ConsoleMenu(bankService, accountsPath, transactionsPath);
        menu.start();
    }
}
