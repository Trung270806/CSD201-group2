import com.bank.service.BankService;
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

        // Initialize Services & CLI Menu
        BankService bankService = new BankService();
        ConsoleMenu menu = new ConsoleMenu(bankService, accountsPath, transactionsPath);
        menu.start();
    }
}
