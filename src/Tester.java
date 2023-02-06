import java.sql.SQLException;

public class Tester {
    public static void main(String[] args) {
        DBHandler dbHandler = null;
        try {
            dbHandler = DBHandler.getInstance();
            System.out.println("Connection to DB established");
        } catch (SQLException ex) {
            System.out.println("SQL Exception");
            ex.printStackTrace();
        }
        try {
            assert dbHandler != null;
            if (dbHandler.addUser("user2", "74859302747485930274748593027433"))
                System.out.println("user1 added to DB");
        } catch (SQLException ex) {
            System.out.println("SQL Exception");
            ex.printStackTrace();
        }
        try {
            dbHandler.connectionClose();
            System.out.println("Connection to DB closed");
        } catch (SQLException ex) {
            System.out.println("SQL Exception");
            ex.printStackTrace();
        }
    }
}
