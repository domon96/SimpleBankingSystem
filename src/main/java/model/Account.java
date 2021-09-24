package model;

import java.util.Objects;

public class Account {
    private final int id;
    private final String accountNumber;
    private final String pin;
    private int balance;

    public Account(int id, String accountNumber, String pin, int balance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPin() {
        return pin;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id && balance == account.balance && Objects.equals(accountNumber, account.accountNumber) && Objects.equals(pin, account.pin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountNumber, pin, balance);
    }

    @Override
    public String toString() {
        return "model.Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", pin='" + pin + '\'' +
                ", balance=" + balance +
                '}';
    }
}
