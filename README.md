# Banking System

A console-based banking management system written in Java. The application allows users to create savings accounts, deposit and withdraw money, check balances, view account details, print transaction passbooks, and visualize deposits and withdrawals.

## Features

- Create savings accounts with an automatically generated six-digit account number
- Create accounts with or without an opening balance through overloaded methods
- Deposit money with amount validation
- Withdraw money with:
  - Insufficient-balance checks
  - A daily withdrawal limit of Rs. 20,000
  - Automatic daily-limit reset on a new day
- Check the current account balance
- Display detailed account information
- Maintain transaction history for each account
- Print passbook entries for a selected date range
- Display simple text-based deposit and withdrawal visualizations
- Handle invalid numeric input, invalid dates, and banking errors

## Technologies and Concepts

- **Language:** Java
- **Standard libraries:** `java.util`, `java.time`, and `java.time.format`
- **Object-oriented programming concepts:**
  - Interfaces through `TransactionOperations`
  - Abstract classes through `BankAccount`
  - Inheritance through `SavingsAccount`
  - Method overriding for account operations
  - Method overloading in `createAccount`
  - Custom checked exceptions through `BankingException`
  - Encapsulation using private fields and public/protected methods

## Project Structure

```text
.
├── BankingSystem.java   # Account classes, transactions, menu, and application entry point
└── README.md            # Project documentation
```

## Requirements

- Java Development Kit (JDK) 8 or later
- A terminal or command prompt

## Run the Application

Clone the repository and move into the project directory:

```bash
git clone https://github.com/Dhruvashingote07/Banking-System.git
cd Banking-System
```

Compile and run the program:

```bash
javac BankingSystem.java
java BankingSystem
```

## Menu Options

When the application starts, it displays the following menu:

1. Create Account
2. Deposit Money
3. Withdraw Money
4. Check Balance
5. Display Account Information
6. Print Passbook by Date Range
7. View Data Visualization
8. Exit

### Date Format

For passbook searches, enter dates in the following format:

```text
yyyy-MM-dd
```

Example:

```text
2026-10-27
```

## Account and Transaction Rules

- Account numbers are generated sequentially, beginning with `000001`.
- Account names cannot be blank.
- Opening balances cannot be negative.
- Deposit and withdrawal amounts must be finite values greater than zero.
- Withdrawals cannot exceed the available account balance.
- The daily withdrawal limit is Rs. 20,000 per account.
- Each account can store up to 500 transaction records in memory.
- The application supports up to 100 accounts during one execution.
- All data is stored in memory and is lost when the application exits.

## Example Workflow

```text
1. Select 1 to create an account.
2. Enter the account holder's name and opening balance.
3. Note the generated account number.
4. Use the account number for deposits, withdrawals, balance checks, and passbook searches.
5. Select 8 to exit the application.
```

## Limitations

- The system does not use a database or persistent file storage.
- Only savings accounts are currently implemented.
- The program is designed for a single user through a command-line interface.
- Account and transaction storage are fixed-size in-memory arrays.

## License

No license has been specified for this project.
