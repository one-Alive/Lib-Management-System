import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Date;

class DatabaseManager {

    Connection connection;

    public DatabaseManager(Connection connection) {
        this.connection = connection;
    }

    public void executeSt(String st) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(st);
    }

    public ResultSet executeStatement(String st) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet res = stmt.executeQuery(st);
        return res;

    }
}

public class App {

    static int optionSelected;

    static BufferedReader inpR() {
        return new BufferedReader(new InputStreamReader(System.in));
    }

    static void addBook(DatabaseManager db) throws SQLException, IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\n-------*------- Book Entry -------*-------");
        System.out.print("Enter Book Name : ");
        String bName = input.readLine();
        System.out.print("Enter Book Price : ");
        int bPrice = Integer.parseInt(input.readLine());
        System.out.println("\nSelect Book Genre :");
        String[] genre = { "Computer Science", "Electronics", "Electrical", "Civil", "Mechanical", "Architecture",
                "Horror", "Comics", "Unknown" };
        for (int i = 0; i < genre.length; i++) {
            System.out.println(String.format("| %d.%s", i + 1, genre[i]));
        }
        System.out.println(String.format("| %d.%s", genre.length + 1, "Go To Main Menu"));
        System.out.print(">");
        int index = Integer.parseInt(input.readLine());
        String bGenre;
        if (index <= genre.length && index != 0) {
            bGenre = genre[index - 1];
            String sqlQuery = String.format(
                    "INSERT INTO Books VALUES(null, '%s', '%s', %d, 'No')",
                    bName, bGenre, bPrice);
            db.executeSt(sqlQuery);
            System.out.println("\nsaving record...done");
        } else {
            System.out.println("Cancelling operation...done");
            System.out.println("Sending You Back To Main Menu...");
        }

    }

    static void issueBook(DatabaseManager db) throws SQLException, IOException {
        System.out.println("\n-------*------- ISSUE BOOK -------*-------");
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter Book ID  : ");
        int bookId = Integer.parseInt(input.readLine());
        ResultSet data = db.executeStatement(String.format("SELECT * FROM BOOKS WHERE ID=%d AND ISSUED='No'", bookId));
        if (data.next() != false) {
            System.out.println(data.getInt(1));
            String bName = data.getString("name");
            System.out.println("\nBook Details : ");
            System.out.println(">> Book ID : " + bookId);
            System.out.println(">> Book Name : " + bName);
            System.out.println("The Book Is Available To Issue...Do Yo Want To Issue (Y/N) ? >");
            String is = input.readLine();
            if (is.toLowerCase().contains("y")) {
                Date dNow = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
                System.out.print("Book Issued To : ");
                String name = input.readLine();
                System.out.print("Student's Id : ");
                int stuId = Integer.parseInt(input.readLine());
                System.out.println("Book Issued!...Saving Record");
                String sqlQuery = String.format(
                        "INSERT INTO IssuedBooks VALUES(%d, '%s', '%d', '%s', '%s', NULL)", bookId, name, stuId, bName,
                        ft.format(dNow));
                db.executeSt(sqlQuery);
                sqlQuery = String.format("UPDATE Books SET ISSUED='Yes' WHERE ID=%d", bookId);
                db.executeSt(sqlQuery);
                System.out.println("DONE!");
            }
        } else {
            System.out.println("Book not found!.");
        }

    }

    static void deleteBook(DatabaseManager db) throws IOException, SQLException {
        BufferedReader input = inpR();
        System.out.print("Enter Book ID  : ");
        int bookId = Integer.parseInt(input.readLine());
        ResultSet data = db.executeStatement(String.format("SELECT * FROM BOOKS WHERE ID=%d", bookId));
        if (data.next() != false) {
            String bName = data.getString("name");
            System.out.println("\nBook Details : ");
            System.out.println("Book ID : " + bookId);
            System.out.println("Book Name : " + bName);
            System.out.println("Are you sure to delete the book from record (y/n): ");
            String is = input.readLine();
            if (is.toLowerCase().contains("y")) {
                String sqlQuery = String.format("DELETE FROM Books WHERE ID = %d", bookId);
                db.executeSt(sqlQuery);
            }
        } else {
            System.out.println("Book not found !");
        }

    }

    static void searchBooks(DatabaseManager db) throws IOException, SQLException {
        System.out.println("\n-------*------- Book Search -------*-------");
        System.out.println("Search Book By :");
        System.out.println("1. Book Id\n2. Book Name\n3. Genre");
        BufferedReader input = inpR();
        System.out.print("Enter Choice  : ");
        int option = Integer.parseInt(input.readLine());
        if (option <= 3) {
            ResultSet data;
            switch (option) {
                case 1:
                    System.out.print("Enter Book ID  : ");
                    int bookId = Integer.parseInt(input.readLine());
                    data = db.executeStatement(String.format("SELECT * FROM BOOKS WHERE ID=%d", bookId));
                    if (data.next() != false) {
                        System.out.println("===================================");
                        System.out.println("\nBook Found !.");
                        String bName = data.getString("name");
                        System.out.println("\nBook Details : ");
                        System.out.println("Book ID : " + bookId);
                        System.out.println("Book Name : " + bName);
                        System.out.println("ISSUED : " + data.getString("ISSUED"));
                        System.out.println("===================================");
                    } else {
                        System.out.println("No book found !");
                    }
                    break;

                case 2:
                    System.out.print("Enter Book Name or Title  : ");
                    String bName = input.readLine();
                    data = db.executeStatement(String.format("SELECT * FROM BOOKS WHERE NAME LIKE '%%%s%%'", bName));
                    int count = 0;
                    System.out.println("===================================");
                    while (data.next()) {

                        bName = data.getString("name");
                        System.out.println("\nBook No. " + count + 1);
                        System.out.println("Book ID : " + data.getString("ID"));
                        System.out.println("Book Name : " + bName);
                        System.out.println("ISSUED : " + data.getString("ISSUED"));
                        count++;
                    }
                    System.out.println("Total records found : " + count);
                    System.out.println("===================================");
                    break;

                case 3:
                    String[] genre = { "Computer Science", "Electronics", "Electrical", "Civil", "Mechanical",
                            "Architecture",
                            "Horror", "Comics", "Unknown" };
                    for (int i = 0; i < genre.length; i++) {
                        System.out.println(String.format("%d.%s", i + 1, genre[i]));
                    }
                    System.out.println(String.format("%d.%s", genre.length + 1, "Go To Main Menu"));
                    int index = Integer.parseInt(input.readLine());
                    String bGenre;
                    if (index <= genre.length && index != 0) {
                        bGenre = genre[index - 1];
                        data = db.executeStatement(
                                String.format("SELECT * FROM BOOKS WHERE GENRE LIKE '%%%s%%'", bGenre));
                        count = 0;
                        System.out.println("===================================");
                        while (data.next()) {

                            bName = data.getString("name");
                            System.out.println("\nBook No. " + count + 1);
                            System.out.println("Book ID : " + data.getString("ID"));
                            System.out.println("Book Name : " + bName);
                            System.out.println("ISSUED : " + data.getString("ISSUED"));

                            count++;
                        }
                        System.out.println("\nTotal records found : " + count);
                        System.out.println("===================================");
                    } else {
                        System.out.println("\nInvalid Choice ! ");
                    }
                    break;
            }
            System.out.print("Press enter to continue");
            input.readLine();
            System.out.println("\n");
        } else {
            System.out.println("Operation failed...invalid option!");
        }
    }

    static void returnBook(DatabaseManager db) {
        try {
            BufferedReader input = inpR();
            System.out.print("Enter Book ID  : ");
            int bookId = Integer.parseInt(input.readLine());
            String sqlQuery = String.format("UPDATE Books SET ISSUED='No' WHERE ID=%d", bookId);
            db.executeSt(sqlQuery);
            sqlQuery = String.format("DELETE FROM ISSUEDBOOKS WHERE ID=%d", bookId);
            db.executeSt(sqlQuery);
            System.out.println("\nSaving records...Done");
        } catch (Exception e) {
            System.out.println("Operation failed");
        }

    }

    static void viewBook(DatabaseManager db) throws IOException, SQLException {
        System.out.println("\n-------*------- View Book -------*-------");
        System.out.println("View Book By :");
        System.out.println("1. Issued Books\n2. All Books\n3. Available Books");
        BufferedReader input = inpR();
        System.out.print("Enter Choice  : ");
        int option = Integer.parseInt(input.readLine());
        String bID;
        String bName;
        int count = 0;
        if (option <= 3) {
            ResultSet data;
            switch (option) {
                case 1:
                    data = db.executeStatement("SELECT * FROM ISSUEDBOOKS ");
                    System.out.println("\n===================================");
                    while (data.next()) {
                        bID = data.getString("ID");
                        bName = data.getString("BName");
                        String iDate = data.getString("ISSUE_DATE");
                        String to = data.getString("ISSUED_TO");
                        String sid = data.getString("STUDENT_ID");
                        System.out.println("\nBook Details : ");
                        System.out.println("Book ID : " + bID);
                        System.out.println("Book Name : " + bName);
                        System.out.println("Issued To : " + to);
                        System.out.println("Student ID : " + sid);
                        System.out.println("Date of issue : " + iDate);
                        count++;
                    }
                    System.out.println("===================================");
                    System.out.println("Total records : " + (count));
                    break;
                case 2:
                    data = db.executeStatement("SELECT * FROM BOOKS ");
                    System.out.println("\n===================================");
                    count = 0;
                    while (data.next()) {
                        bID = data.getString("ID");
                        bName = data.getString("Name");
                        String bGenre = data.getString("GENRE");
                        String bPrice = data.getString("PRICE");
                        String issued = data.getString("ISSUED");
                        System.out.println("\nBook Details : ");
                        System.out.println("Book ID : " + bID);
                        System.out.println("Book Name : " + bName);
                        System.out.println("Genre : " + bGenre);
                        System.out.println("Price : " + bPrice);
                        System.out.println("Issued? : " + issued);
                        count++;
                    }
                    System.out.println("===================================");
                    System.out.println("Total records : " + (count));
                    break;
                case 3:
                    count = 0;
                    data = db.executeStatement("SELECT * FROM BOOKS WHERE ISSUED='No'");
                    System.out.println("\n===================================");
                    while (data.next()) {
                        bID = data.getString("ID");
                        bName = data.getString("Name");
                        String bGenre = data.getString("GENRE");
                        String bPrice = data.getString("PRICE");
                        String issued = data.getString("ISSUED");
                        System.out.println("\nBook Details : ");
                        System.out.println("Book ID : " + bID);
                        System.out.println("Book Name : " + bName);
                        System.out.println("Genre : " + bGenre);
                        System.out.println("Price : " + bPrice);
                        System.out.println("Issued? : " + issued);
                        count++;
                    }
                    System.out.println("===================================");
                    System.out.println("Total records : " + (count));
                    break;
            }
        }
        System.out.print("Press enter to continue");
        input.readLine();
        System.out.println("\n");
    }

    static Connection getCursor() throws SQLException {
        System.out.println("\nLoading...");
        System.out.println("Checking Database....");
        System.out.println("Establishing Connection...");
        return DriverManager.getConnection("jdbc:sqlite:database.db");
    }

    static void in(DatabaseManager sys) throws SQLException, IOException {
        System.out.println("-------------------------------------");
        System.out.println(" Lib Management System");
        System.out.println("|_____________________|");
        System.out.println("| 1. Add Book         |");
        System.out.println("| 2. Delete Book      |");
        System.out.println("| 3. Search Book      |");
        System.out.println("| 4. Issue Book       |");
        System.out.println("| 5. View Book List   |");
        System.out.println("| 6. Return Book      |");
        System.out.println("| Exit? Press ctrl+c  |");
        System.out.println("|_____________________|");
        System.out.print("Enter Your Choice -> ");
        Scanner scanner = new Scanner(System.in);
        optionSelected = scanner.nextInt();
        switch (optionSelected) {
            case 1:
                addBook(sys);
                break;
            case 2:
                deleteBook(sys);
                break;
            case 3:
                searchBooks(sys);
                break;
            case 4:
                issueBook(sys);
                break;
            case 5:
                viewBook(sys);
                break;
            case 6:
                returnBook(sys);
                break;
        }
    }

    public static void main(String[] args) throws Exception {
        Connection cursor = getCursor();
        DatabaseManager sys = new DatabaseManager(cursor);
        sys.executeSt(
                "CREATE TABLE IF NOT EXISTS Books(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, GENRE TEXT, PRICE TEXT, ISSUED TEXT);");
        sys.executeSt(
                "CREATE TABLE IF NOT EXISTS IssuedBooks(ID INTEGER, ISSUED_TO TEXT, STUDENT_ID TEXT , BNAME TEXT, ISSUE_DATE TEXT);");
        while (true) {
            in(sys);
        }
    }
}
