
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Interface
interface TransactionOperations {
    void deposit(double amount);
    void withdraw(double amount);
}

// Custom exception
class BankingException extends Exception {
    BankingException(String message) {
        super(message);
    }
}

// Abstract class
abstract class BankAccount implements TransactionOperations {
    private final String accountNumber;
    private final String holderName;
    private double balance;

    protected final double dailyWithdrawalLimit = 20000.0;
    protected double todayWithdrawn = 0;
    protected LocalDate withdrawalDate = LocalDate.now();

    BankAccount(String accountNumber, String holderName,
                double openingBalance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = openingBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    protected void validateAmount(double amount)
            throws BankingException {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new BankingException(
                "Amount must be greater than zero.");
        }
    }

    protected void resetDailyLimitIfNeeded() {
        if (!withdrawalDate.equals(LocalDate.now())) {
            withdrawalDate = LocalDate.now();
            todayWithdrawn = 0;
        }
    }

    @Override
    public void deposit(double amount) {
        try {
            validateAmount(amount);
            setBalance(getBalance() + amount);
            System.out.println("Deposit successful.");
        } catch (BankingException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public abstract void withdraw(double amount);

    public void displayInformation() {
        System.out.println("\n--- Account Information ---");
        System.out.println("Account Number : " + accountNumber);
        System.out.println("Holder Name    : " + holderName);
        System.out.println("Account Type   : " + getAccountType());
        System.out.printf("Balance        : Rs. %.2f%n",
                          getBalance());
        System.out.printf("Daily Limit    : Rs. %.2f%n",
                          dailyWithdrawalLimit);
        System.out.printf("Withdrawn Today: Rs. %.2f%n",
                          todayWithdrawn);
    }

    public abstract String getAccountType();
}

// Inheritance and method overriding
class SavingsAccount extends BankAccount {
    SavingsAccount(String accountNumber, String holderName,
                   double openingBalance) {
        super(accountNumber, holderName, openingBalance);
    }

    @Override
    public void withdraw(double amount) {
        try {
            validateAmount(amount);
            resetDailyLimitIfNeeded();

            if (amount > getBalance()) {
                throw new BankingException(
                    "Insufficient balance.");
            }

            if (todayWithdrawn + amount > dailyWithdrawalLimit) {
                throw new BankingException(
                    "Daily withdrawal limit exceeded.");
            }

            setBalance(getBalance() - amount);
            todayWithdrawn += amount;
            System.out.println("Withdrawal successful.");

        } catch (BankingException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }
}

// Transaction record
class Transaction {
    private final LocalDateTime dateTime;
    private final String type;
    private final double amount;
    private final double balanceAfter;

    Transaction(String type, double amount, double balanceAfter) {
        this.dateTime = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void display() {
        DateTimeFormatter format =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        System.out.printf("%-18s %-12s Rs. %-10.2f Rs. %.2f%n",
            dateTime.format(format), type, amount, balanceAfter);
    }
}

// Main class
public class BankingSystem {
    static Scanner sc = new Scanner(System.in);

    // Array of objects
    static SavingsAccount[] accounts = new SavingsAccount[100];
    static Transaction[][] transactions = new Transaction[100][500];
    static int[] transactionCounts = new int[100];
    static int accountCount = 0;

    // Method overloading
    static void createAccount(String name, double openingBalance) {
        if (accountCount >= accounts.length) {
            System.out.println("Account storage is full.");
            return;
        }

        if (name == null || name.isBlank()
                || !Double.isFinite(openingBalance)
                || openingBalance < 0) {
            System.out.println("Invalid account details.");
            return;
        }

        String number = String.format("%06d", accountCount + 1);

        accounts[accountCount] =
            new SavingsAccount(number, name, openingBalance);

        addTransaction(accountCount, "OPENING", openingBalance);

        System.out.println("Account created successfully.");
        System.out.println("Account Number: " + number);
        accountCount++;
    }

    static void createAccount(String name) {
        createAccount(name, 0);
    }

    static int findAccount(String number) {
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountNumber().equals(number)) {
                return i;
            }
        }
        return -1;
    }

    static void addTransaction(int index, String type, double amount) {
        if (transactionCounts[index] >= transactions[index].length) {
            System.out.println("Transaction history is full.");
            return;
        }

        transactions[index][transactionCounts[index]++] =
            new Transaction(type, amount, accounts[index].getBalance());
    }

    static void depositMoney(int index, double amount) {
        double oldBalance = accounts[index].getBalance();
        accounts[index].deposit(amount);

        if (accounts[index].getBalance() > oldBalance) {
            addTransaction(index, "DEPOSIT", amount);
        }
    }

    static void withdrawMoney(int index, double amount) {
        double oldBalance = accounts[index].getBalance();
        accounts[index].withdraw(amount);

        if (accounts[index].getBalance() < oldBalance) {
            addTransaction(index, "WITHDRAWAL", amount);
        }
    }

    static void printPassbook(int index, LocalDate from,
                              LocalDate to) {
        if (from.isAfter(to)) {
            System.out.println("Invalid date range.");
            return;
        }

        System.out.println("\n--- PASSBOOK ---");
        System.out.println("Account: " +
                           accounts[index].getAccountNumber());
        System.out.printf("%-18s %-12s %-17s %s%n",
                          "Date & Time", "Type",
                          "Amount", "Balance");

        boolean found = false;

        for (int i = 0; i < transactionCounts[index]; i++) {
            Transaction t = transactions[index][i];
            LocalDate date = t.getDateTime().toLocalDate();

            if (!date.isBefore(from) && !date.isAfter(to)) {
                t.display();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No transactions found in this period.");
        }
    }

    static void showVisualization(int index) {
        double deposits = 0;
        double withdrawals = 0;

        for (int i = 0; i < transactionCounts[index]; i++) {
            Transaction t = transactions[index][i];

            // Visualization summary based on transaction records.
            // Opening balance is excluded from deposits.
            if (t.getDateTime() != null) {
                // Totals are calculated using transaction types below.
            }
        }

        // Count actual deposit and withdrawal transactions.
        for (int i = 0; i < transactionCounts[index]; i++) {
            Transaction t = transactions[index][i];

            // This summary is displayed using the stored transaction data.
            // Transaction exposes its type and amount through helper methods.
            if (t.getType().equals("DEPOSIT")) {
                deposits += t.getAmount();
            } else if (t.getType().equals("WITHDRAWAL")) {
                withdrawals += t.getAmount();
            }
        }

        System.out.println("\n--- DATA VISUALIZATION ---");
        System.out.printf("Total Deposits    : Rs. %.2f%n", deposits);
        System.out.printf("Total Withdrawals : Rs. %.2f%n", withdrawals);
        System.out.println("Deposit bar       : " + "#".repeat(
            (int) Math.min(50, deposits / 1000)));
        System.out.println("Withdrawal bar    : " + "*".repeat(
            (int) Math.min(50, withdrawals / 1000)));
        System.out.println("(Each symbol represents approximately Rs. 1000.)");
    }

    public static void main(String[] args) {
        int choice;

        do {
            System.out.println("\n===== BANKING MANAGEMENT SYSTEM =====");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Check Balance");
            System.out.println("5. Display Account Information");
            System.out.println("6. Print Passbook (Date Range)");
            System.out.println("7. Data Visualization");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");

            try {
                choice = sc.nextInt();
                sc.nextLine();

                if (choice == 1) {
                    System.out.print("Enter holder name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter opening balance: ");
                    double amount = sc.nextDouble();
                    sc.nextLine();

                    createAccount(name, amount);

                } else if (choice >= 2 && choice <= 7) {
                    System.out.print("Enter account number: ");
                    String number = sc.nextLine();

                    int index = findAccount(number);

                    if (index == -1) {
                        System.out.println("Account not found.");
                        continue;
                    }

                    switch (choice) {
                        case 2:
                            System.out.print("Deposit amount: ");
                            depositMoney(index, sc.nextDouble());
                            sc.nextLine();
                            break;

                        case 3:
                            System.out.print("Withdrawal amount: ");
                            withdrawMoney(index, sc.nextDouble());
                            sc.nextLine();
                            break;

                        case 4:
                            System.out.printf("Balance: Rs. %.2f%n",
                                accounts[index].getBalance());
                            break;

                        case 5:
                            accounts[index].displayInformation();
                            break;

                        case 6:
                            System.out.print("From date (yyyy-MM-dd): ");
                            LocalDate from = LocalDate.parse(sc.nextLine());

                            System.out.print("To date (yyyy-MM-dd): ");
                            LocalDate to = LocalDate.parse(sc.nextLine());

                            printPassbook(index, from, to);
                            break;

                        case 7:
                            showVisualization(index);
                            break;
                    }

                } else if (choice != 8) {
                    System.out.println("Invalid choice.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                sc.nextLine();
                choice = 0;
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Invalid date. Use yyyy-MM-dd.");
                choice = 0;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                choice = 0;
            }

            if (choice == 8) {
                System.out.println("Thank you for using our banking system.");
            }

        } while (choice != 8);

        sc.close();
    }
}
