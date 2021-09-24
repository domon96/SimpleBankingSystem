package service;

import model.Account;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Optional;

public class CardService {

    private static final String URL = "jdbc:sqlite:src/main/resources/banking_system.db";
    private SQLiteDataSource dataSource;

    public CardService() {
        init();
        createTable();
    }

    private void init() {
        dataSource = new SQLiteDataSource();
        dataSource.setUrl(URL);
    }

    private void createTable() {
        try (final Connection con = dataSource.getConnection()) {
            try (final Statement statement = con.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "number TEXT," +
                        "pin TEXT," +
                        "balance INT DEFAULT 0)");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void insertAccount(String accountNumber, String pinNumber) {
        try (final Connection con = dataSource.getConnection()) {
            final String insertQuery = "INSERT INTO card (number, pin) VALUES (?, ?)";

            try (final PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, accountNumber);
                preparedStatement.setString(2, pinNumber);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Optional<Account> findAccount(String accountNumber) {
        try (final Connection con = dataSource.getConnection()) {
            final String findQuery = "SELECT * FROM card WHERE number = ?";
            try (final PreparedStatement preparedStatement = con.prepareStatement(findQuery)) {
                preparedStatement.setString(1, accountNumber);

                final ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return Optional.of(new Account(
                            resultSet.getInt("id"),
                            resultSet.getString("number"),
                            resultSet.getString("pin"),
                            resultSet.getInt("balance")
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    public void addIncome(int income, int id) {
        try (final Connection con = dataSource.getConnection()) {
            final String updateQuery = "UPDATE card SET balance = balance + ? WHERE id = ?";

            try (final PreparedStatement preparedStatement = con.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, income);
                preparedStatement.setInt(2, id);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteAccount(int id) {
        try (final Connection con = dataSource.getConnection()) {
            final String deleteQuery = "DELETE FROM card WHERE id = ?";

            try (final PreparedStatement preparedStatement = con.prepareStatement(deleteQuery)) {
                preparedStatement.setInt(1, id);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void doTransfer(int amount, String accountFrom, String accountTo) {
        try (final Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);

            final String withdrawSQL = "UPDATE card SET balance = balance - ? WHERE number = ?";
            final String depositSQL = "UPDATE card SET balance = balance + ? WHERE number = ?";

            try (final PreparedStatement withdraw = con.prepareStatement(withdrawSQL);
                 final PreparedStatement deposit = con.prepareStatement(depositSQL)) {

                withdraw.setInt(1, amount);
                withdraw.setString(2, accountFrom);
                withdraw.executeUpdate();

                deposit.setInt(1, amount);
                deposit.setString(2, accountTo);
                deposit.executeUpdate();

                con.commit();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
