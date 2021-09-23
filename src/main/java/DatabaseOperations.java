import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseOperations {

    private static final String URL = "jdbc:sqlite:src/main/resources/banking_system.db";
    private SQLiteDataSource dataSource;

    public DatabaseOperations() {
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
            try (final Statement statement = con.createStatement()) {
                statement.executeUpdate(
                        String.format("INSERT INTO card (number, pin) VALUES ('%s', '%s')", accountNumber, pinNumber));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isAccountInDatabase(String accountNumber) {
        try (final Connection con = dataSource.getConnection()) {
            try (final Statement statement = con.createStatement()) {
                final ResultSet resultSet = statement.executeQuery(
                        String.format("SELECT * FROM card WHERE number = '%s'", accountNumber));
                return resultSet.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean findAccount(String accountNumber, String pinNumber) {
        try (final Connection con = dataSource.getConnection()) {
            try (final Statement statement = con.createStatement()) {
                final ResultSet resultSet = statement.executeQuery(
                        String.format("SELECT * FROM card WHERE number = '%s' AND pin = '%s'", accountNumber, pinNumber));
                return resultSet.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
