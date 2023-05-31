import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SWINGdemo {

    private final JEditorPane log;
    private final JButton show;
    private final JButton report;
    private final JComboBox<String> clients;

    public SWINGdemo() {
        log = new JEditorPane("text/html", "");
        log.setPreferredSize(new Dimension(800, 600));
        show = new JButton("Show");
        report = new JButton("Report");
        clients = new JComboBox<>();

        readCustomerData("C:\\Users\\kanur\\OneDrive\\Documents\\NetBeansProjects\\GUIdemo\\src\\test.dat");

        for (int i = 0; i < Bank.getNumberOfCustomers(); i++) {
            clients.addItem(Bank.getCustomer(i).getLastName() +" "+ Bank.getCustomer(i).getFirstName());
        }
    }

    private void readCustomerData(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                if (data.length >= 3) {
                    String firstName = data[0].trim();
                    String lastName = data[1].trim();
                    Bank.addCustomer(firstName, lastName);
                    int numAccounts = Integer.parseInt(data[2].trim());
                    for (int i = 0; i < numAccounts; i++) {
                        line = reader.readLine();
                        String[] accountData = line.split("\t");
                        if (accountData.length >= 3) {
                            String accountType = accountData[0].trim();
                            double balance = Double.parseDouble(accountData[1].trim());
                            double interestRate = Double.parseDouble(accountData[2].trim());
                            if (accountType.equals("S")) {
                                SavingsAccount savingsAccount = new SavingsAccount(balance, interestRate);
                                Bank.getCustomer(Bank.getNumberOfCustomers() - 1).addAccount(savingsAccount);
                            } else if (accountType.equals("C")) {
                                double overdraftAmount = Double.parseDouble(accountData[2].trim());
                                CheckingAccount checkingAccount = new CheckingAccount(balance, overdraftAmount);
                                Bank.getCustomer(Bank.getNumberOfCustomers() - 1).addAccount(checkingAccount);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading customers from file: " + e.getMessage());
        }
    }

    private void launchFrame() {
        JFrame frame = new JFrame("MyBank clients");
        frame.setLayout(new BorderLayout());
        JPanel cpane = new JPanel();
        cpane.setLayout(new GridLayout(1, 3));

        cpane.add(clients);
        cpane.add(show);
        cpane.add(report);
        frame.add(cpane, BorderLayout.NORTH);
        frame.add(log, BorderLayout.CENTER);

        show.addActionListener((ActionEvent e) -> {
            Customer current = Bank.getCustomer(clients.getSelectedIndex());
            StringBuilder custInfo = new StringBuilder("<br>&nbsp;<b><span style=\"font-size:2em;\">")
                    .append(current.getLastName())
                    .append(" ")
                    .append(current.getFirstName())
                    .append("</span><br><hr>")
                    .append("&nbsp;<b>Accounts:</b><br>");
            
            for (int i = 0; i < current.getNumberOfAccounts(); i++) {
                String accType = current.getAccount(i) instanceof CheckingAccount ? "Checking" : "Savings";
                custInfo.append("&nbsp;&nbsp;&nbsp;<b>Acc Type:</b> ")
                        .append(accType)
                        .append("<br>")
                        .append("&nbsp;&nbsp;&nbsp;<b>Balance:</b> <span style=\"color:red;\">$")
                        .append(current.getAccount(i).getBalance())
                        .append("</span><br>");
                
            }
            
            log.setText(custInfo.toString());
        });

        report.addActionListener((ActionEvent e) -> {
            StringBuilder reportInfo = new StringBuilder("<br>&nbsp;<b><span style=\"font-size:2em;\">Customer Report</span><br><hr>");
            
            for (int i = 0; i < Bank.getNumberOfCustomers(); i++) {
                Customer customer = Bank.getCustomer(i);
                reportInfo.append("&nbsp;<b>Customer:</b> ")
                        .append(customer.getLastName())
                        .append(" ")
                        .append(customer.getFirstName())
                        .append("<br>");
                
                for (int j = 0; j < customer.getNumberOfAccounts(); j++) {
                    reportInfo.append("&nbsp;&nbsp;&nbsp;<b>Account Type:</b> ")
                            .append(customer.getAccount(j) instanceof CheckingAccount ? "Checking" : "Savings")
                            .append("<br>")
                            .append("&nbsp;&nbsp;&nbsp;<b>Balance:</b> <span style=\"color:red;\">$")
                            .append(customer.getAccount(j).getBalance())
                            .append("</span><br>");
                }
                
                reportInfo.append("<br>");
            }
            
            log.setText(reportInfo.toString());
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SWINGdemo demo = new SWINGdemo();
        demo.launchFrame();
    }
}
