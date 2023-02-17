import org.sqlite.JDBC;
import java.sql.*;
public final class DBHandler {
    private static final String CON_STR = "jdbc:sqlite:database.db"; // Константа, в которой хранится адрес подключения
    private static DBHandler instance = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private final Connection connection; // Объект, в котором будет храниться соединение с БД
    public static DBHandler getInstance() throws SQLException {  // Используем шаблон одиночка
        if (instance == null)
            instance = new DBHandler();
        return instance;
    }
    private DBHandler() throws SQLException {
        DriverManager.registerDriver(new JDBC());  // Регистрируем драйвер, с которым будем работать (Sqlite)
        this.connection = DriverManager.getConnection(CON_STR); // Выполняем подключение к базе данных
    }
    public void connectionClose () throws SQLException {
        this.connection.close();
    }
    public boolean registerClient (String userName, String passwordHash) throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT COUNT(*) AS recordCount FROM users WHERE userName = '" + userName + "'");
        if (resultSet.getInt("recordCount") == 0) {
            statement.executeUpdate("INSERT INTO users VALUES (null,'" + userName + "','" + passwordHash + "')");
            return true;
        } else return false;
    }
    public boolean checkAuth (String userName, String passwordHash) throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT COUNT(*) AS recordCount FROM users WHERE userName = '" + userName + "' AND password = '" + passwordHash + "'");
        return resultSet.getInt("recordCount") == 1;
    }
    public boolean passwordChange (String userName, String passwordHash) throws SQLException {
        statement = connection.createStatement();
        return statement.executeUpdate("UPDATE users SET password = '" + passwordHash + "' WHERE userName = '" + userName + "'") == 1;
    }
    public boolean moneyTransfer (String userName, String toUserName, int count) throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT balance FROM users WHERE userName = '" + userName + "'");
        if (resultSet.getInt("balance") >= count) {
            int result1, result2;
            result1 = statement.executeUpdate("UPDATE users SET balance = balance - " + count + " WHERE userName = '" + userName + "'");
            result2 = statement.executeUpdate("UPDATE users SET balance = balance + " + count + " WHERE userName = '" + toUserName + "'");
            return (result1 + result2) == 2;
        } return false;
    }
    public int balanceCheck (String userName) throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT balance FROM users WHERE userName = '" + userName + "'");
        return resultSet.getInt("balance");
    }
}
