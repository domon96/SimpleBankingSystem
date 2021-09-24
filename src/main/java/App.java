import model.Account;
import service.CardService;

import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    private static Account activeAccount = null;
    private static final CardService cardService = new CardService();
    private static final String BIN = "400000";

    public static void main(String[] args) {

        while (true) {
            if (activeAccount == null) {
                showStartMenu();
                switch (scanner.nextLine()) {
                    case "0" -> {
                        System.out.println("\nBye!");
                        return;
                    }
                    case "1" -> createAccount();
                    case "2" -> logIn();
                    default -> System.out.println("\nWrong choice");
                }
            } else {
                showUsersMenu();
                switch (scanner.nextLine()) {
                    case "0" -> {
                        System.out.println("\nBye!");
                        return;
                    }
                    case "1" -> showBalance();
                    case "2" -> addIncome();
                    case "3" -> doTransfer();
                    case "4" -> closeAccount();
                    case "5" -> logOut();
                    default -> System.out.println("\nWrong choice");
                }
            }
        }
    }

    private static void showStartMenu() {
        System.out.println("\n1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

    private static void showUsersMenu() {
        System.out.println("\n1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }

    private static void createAccount() {
        StringBuilder accountNumber;
        do {
            accountNumber = new StringBuilder(16);
            accountNumber.append(BIN);
            // generating 9-digit model.Account Identifier
            accountNumber.append(randomNumber(9));
            // generating 1-digit checksum
            accountNumber.append(generateChecksum(accountNumber));
        } while (cardService.findAccount(accountNumber.toString()).isPresent());
        System.out.println("\nYour card has been created");
        System.out.println("Your card number:");
        System.out.println(accountNumber);
        final String pinNumber = randomNumber(4);
        System.out.println("Your card PIN:");
        System.out.println(pinNumber);
        cardService.insertAccount(accountNumber.toString(), pinNumber);
    }

    private static void logIn() {
        System.out.println("\nEnter your card number:");
        String cardNumber = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();
        final Optional<Account> optionalAccount = cardService.findAccount(cardNumber);
        if (optionalAccount.isPresent() && optionalAccount.get().getPin().equals(pin)) {
            System.out.println("\nYou have successfully logged in!");
            activeAccount = optionalAccount.get();
        } else {
            System.out.println("\nWrong card number or PIN!");
        }
    }

    private static void logOut() {
        activeAccount = null;
        System.out.println("\nYou have successfully logged out!");
    }

    private static void showBalance() {
        System.out.println("\nBalance: " + activeAccount.getBalance());
    }

    private static void addIncome() {
        System.out.println("\nEnter income:");
        final int income = scanner.nextInt();
        scanner.nextLine(); // clearing terminal
        cardService.addIncome(income, activeAccount.getId());
        activeAccount.setBalance(activeAccount.getBalance() + income);
        System.out.println("Income was added!");
    }

    private static void doTransfer() {
        System.out.println("\nEnter card number to transfer money to:");
        String card = scanner.nextLine();
        if (calculateLuhnAlgorithmSum(card) % 10 != 0) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        } else if (cardService.findAccount(card).isEmpty()) {
            System.out.println("Such a card does not exist.");
        } else if (activeAccount.getAccountNumber().equals(card)) {
            System.out.println("You can't transfer money to the same account!");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            final int amount = scanner.nextInt();
            scanner.nextLine(); // clearing terminal
            if (activeAccount.getBalance() < amount) {
                System.out.println("Not enough money!");
            } else {
                cardService.doTransfer(amount, activeAccount.getAccountNumber(), card);
                activeAccount.setBalance(activeAccount.getBalance() - amount);
                System.out.println("Success!");
            }
        }
    }

    private static void closeAccount() {
        cardService.deleteAccount(activeAccount.getId());
        activeAccount = null;
        System.out.println("\nThe account has been closed!");
    }

    private static int generateChecksum(StringBuilder accountNumber) {
        final int sum = calculateLuhnAlgorithmSum(accountNumber.toString());
        final int reminder = sum % 10;
        return reminder == 0 ? 0 : 10 - reminder;
    }

    private static int calculateLuhnAlgorithmSum(String number) {
        int sum = 0;
        for (int i = 0; i < number.length(); i++) {
            int digit = Character.getNumericValue(number.charAt(i));
            if (i % 2 == 0) {
                digit *= 2;
            }
            if (digit >= 10) {
                digit -= 9;
            }
            sum += digit;
        }
        return sum;
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
