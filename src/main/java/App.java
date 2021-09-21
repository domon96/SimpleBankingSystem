import java.util.*;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    private static final Map<String, Account> accounts = new HashMap<>();
    private static Account activeAccount = null;
    private static final String BIN = "400000";

    private static class Account {
        private final String accountNumber;
        private final String pin;
        private int balance;

        public Account(String accountNumber, String pin) {
            this.accountNumber = accountNumber;
            this.pin = pin;
            this.balance = 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Account account = (Account) o;
            return Objects.equals(accountNumber, account.accountNumber) && Objects.equals(pin, account.pin);
        }

        @Override
        public int hashCode() {
            return Objects.hash(accountNumber, pin);
        }

        @Override
        public String toString() {
            return "Account{" +
                    "accountNumber='" + accountNumber + '\'' +
                    ", pin='" + pin + '\'' +
                    ", balance=" + balance +
                    '}';
        }
    }

    public static void main(String[] args) {
        while (true) {
            showMenu();
            switch (scanner.nextLine()) {
                case "0" -> {
                    System.out.println("\nBye!");
                    return;
                }
                case "1" -> {
                    if (activeAccount == null) createAccount();
                    else showBalance();
                }
                case "2" -> {
                    if (activeAccount == null) logIn();
                    else logOut();
                }
                default -> System.out.println("\nWrong choice");
            }

        }
    }

    private static void showMenu() {
        if (activeAccount == null) {
            System.out.println("\n1. Create an account");
            System.out.println("2. Log into account");
        } else {
            System.out.println("\n1. Show balance");
            System.out.println("2. Log out");
        }
        System.out.println("0. Exit");
    }

    private static void createAccount() {
        StringBuilder accountNumber;
        do {
            accountNumber = new StringBuilder(16);
            accountNumber.append(BIN);
            // generating 9-digit Account Identifier
            accountNumber.append(randomNumber(9));
            // generating 1-digit checksum
            accountNumber.append(randomDigit());
        } while (accounts.containsKey(accountNumber.toString()));
        System.out.println("\nYour card has been created");
        System.out.println("Your card number:");
        System.out.println(accountNumber);
        String pinNumber = randomNumber(4);
        System.out.println("Your card PIN:");
        System.out.println(pinNumber);
        // saving new account
        accounts.put(accountNumber.toString(), new Account(accountNumber.toString(), pinNumber));
    }

    private static void logIn() {
        System.out.println("\nEnter your card number:");
        String cardNumber = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();
        for (Map.Entry<String, Account> entry : accounts.entrySet()) {
            if (entry.getKey().equals(cardNumber) && entry.getValue().equals(new Account(cardNumber, pin))) {
                System.out.println("\nYou have successfully logged in!");
                activeAccount = entry.getValue();
                return;
            }
        }
        System.out.println("\nWrong card number or PIN!");
    }

    private static void logOut() {
        activeAccount = null;
        System.out.println("\nYou have successfully logged out!");
    }

    private static void showBalance() {
        System.out.println("\nBalance: " + activeAccount.balance);
    }

    private static String randomNumber(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(randomDigit());
        }
        return builder.toString();
    }

    private static int randomDigit() {
        return random.nextInt(10);
    }
}
