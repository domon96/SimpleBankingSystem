import java.util.Random;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    private static Account activeAccount = null;
    private static final String BIN = "400000";

    public static void main(String[] args) {
        DatabaseOperations databaseOperations = new DatabaseOperations();

        while (true) {
            showMenu();
            switch (scanner.nextLine()) {
                case "0" -> {
                    System.out.println("\nBye!");
                    return;
                }
                case "1" -> {
                    if (activeAccount == null) createAccount(databaseOperations);
                    else showBalance();
                }
                case "2" -> {
                    if (activeAccount == null) logIn(databaseOperations);
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

    private static void createAccount(DatabaseOperations databaseOperations) {
        StringBuilder accountNumber;
        do {
            accountNumber = new StringBuilder(16);
            accountNumber.append(BIN);
            // generating 9-digit Account Identifier
            accountNumber.append(randomNumber(9));
            // generating 1-digit checksum
            accountNumber.append(generateChecksum(accountNumber));
        } while (databaseOperations.isAccountInDatabase(accountNumber.toString()));
        System.out.println("\nYour card has been created");
        System.out.println("Your card number:");
        System.out.println(accountNumber);
        String pinNumber = randomNumber(4);
        System.out.println("Your card PIN:");
        System.out.println(pinNumber);
        // saving new account to database
        databaseOperations.insertAccount(accountNumber.toString(), pinNumber);
    }

    private static void logIn(DatabaseOperations databaseOperations) {
        System.out.println("\nEnter your card number:");
        String cardNumber = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();
        if (databaseOperations.findAccount(cardNumber, pin)) {
            System.out.println("\nYou have successfully logged in!");
            activeAccount = new Account(cardNumber, pin);
        }
        System.out.println("\nWrong card number or PIN!");
    }

    private static void logOut() {
        activeAccount = null;
        System.out.println("\nYou have successfully logged out!");
    }

    private static void showBalance() {
        System.out.println("\nBalance: " + activeAccount.getBalance());
    }

    private static int generateChecksum(StringBuilder accountNumber) {
        // Luhn Algorithm
        int sum = 0;
        for (int i = 0; i < accountNumber.length(); i++) {
            int digit = Character.getNumericValue(accountNumber.charAt(i));
            if (i % 2 == 0) {
                digit *= 2;
            }
            if (digit >= 10) {
                digit -= 9;
            }
            sum += digit;
        }
        int reminder = sum % 10;
        return reminder == 0 ? 0 : 10 - reminder;
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
