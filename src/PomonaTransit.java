import java.util.*;
import java.sql.*;
import java.text.ParseException;

public class PomonaTransit {
    public static void main(String[] args) throws ParseException {
        Connection conn;
        Statement stmt;
        Scanner scan = new Scanner(System.in);
        String input = "";
        try {
            String jdbcUrl = "jdbc:postgresql://localhost:5432/Lab4";
            String username = "postgres";
            String password = "123";

            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
            stmt = conn.createStatement();

            while (true) {
                System.out.println();
                displayMenu();
                System.out.print("Command: ");
                input = scan.nextLine();
                switch (input.trim()) {
                    case "1":
                        displaySchedule(stmt);
                        break;
                    case "2":
                        deleteTripOffering(stmt);
                        break;
                    case "3":
                        addTripOffering(stmt);
                        break;
                    case "4":
                        changeDriver(stmt);
                        break;
                    case "5":
                        changeBus(stmt);
                        break;
                    case "6":
                        displayTripStops(stmt);
                        break;
                    case "7":
                        displayWeekly(stmt);
                        break;
                    case "8":
                        addDriver(stmt);
                        break;
                    case "9":
                        addBus(stmt);
                        break;
                    case "10":
                        deleteBus(stmt);
                        break;
                    case "11":
                        insertTripData(stmt);
                        break;
                    case "-1":
                        System.exit(0);
                        break;
                    default:
                        displayMenu();
                        break;
                }
            }
        } catch (SQLException msg) {
            msg.printStackTrace();
        } catch (ClassNotFoundException msg) {
            msg.printStackTrace();
        }
        scan.close();
    }

    private static void displayMenu() {
        System.out.println("1:  Display a Schedule");
        System.out.println("2:  Delete a Trip Offering");
        System.out.println("3:  Add a Trip Offering");
        System.out.println("4:  Change a Driver");
        System.out.println("5:  tChange a Bus");
        System.out.println("6:  Display Trip Stops");
        System.out.println("7:  Display Weekly Schedule for Driver");
        System.out.println("8:  Add a Driver");
        System.out.println("9:  Add a Bus");
        System.out.println("10: Delete a Bus");
        System.out.println("11: Insert Actual Trip Info");

        System.out.println("-1: Exit program");
    }

    // 1. Display the schedule of all trips for a given StartLocationName and
    // Destination Name, and Date
    public static void displaySchedule(Statement stmt) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Start Location Name: ");
        String start = sc.nextLine().trim();
        System.out.print("Destination Name: ");
        String destination = sc.nextLine().trim();
        System.out.print("Date: ");
        String date = sc.nextLine().trim();

        try {
            ResultSet result = stmt
                    .executeQuery("SELECT A.ScheduledStartTime, A.ScheduledArrivalTime, A.DriverName, A.BusID " +
                            "FROM TripOffering A, Trip B " +
                            "WHERE B.StartLocationName LIKE '" + start + "' AND " +
                            "B.DestinationName LIKE '" + destination + "' AND " +
                            "A.Date = '" + date + "' AND " +
                            "B.TripNumber = A.TripNumber " +
                            "Order by ScheduledStartTime ");

            ResultSetMetaData data = result.getMetaData();
            int colCount = data.getColumnCount();

            System.out.println("****************************************************************");

            while (result.next()) {
                for (int i = 1; i <= colCount; i++) {
                    System.out.println(String.format("%-20s: %s", data.getColumnName(i), result.getString(i)));
                }
            }

            result.close();
            System.out.println("****************************************************************");
        } catch (SQLException msg) {
            System.out.println("Problem with transaction!\nCheck input and make sure item exists.");
        }
    }

    // 2a. Delete a trip offering specified by Trip#, Date, and ScheduledStartTime
    public static void deleteTripOffering(Statement stmt) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Start Trip Number: ");
        String tripNum = sc.nextLine().trim();
        System.out.print("Date: ");
        String date = sc.nextLine().trim();
        System.out.print("Scheduled Start Time: ");
        String startTime = sc.nextLine().trim();

        try {
            if (stmt.executeUpdate("DELETE TripOffering " +
                    "WHERE TripNumber = '" + tripNum + "' AND " +
                    "Date = '" + date + "' AND " +
                    "ScheduledStartTime = '" + startTime + "'") != 0) {
                System.out.println("Deleted Trip Offering with Trip Number: " + tripNum + " on " + date
                        + " starting at " + startTime);
            } else {
                System.out.println(
                        "Trip Offering with Trip Number: " + tripNum + " on " + date + " starting at " + startTime
                                + " does not exist.");
            }

        } catch (SQLException msg) {
            System.out.println("Problem with transaction!\n*Check input and make sure item exists.");
        }
    }

    // 2b. Add a set of trip offerings assuming the values of all attributes are
    // given
    public static void addTripOffering(Statement stmt) throws SQLException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("Enter Trip Number: ");
            String tripNum = sc.nextLine().trim();
            System.out.print("Date: ");
            String date = sc.nextLine().trim();
            System.out.print("Scheduled Start Time: ");
            String startTime = sc.nextLine().trim();
            System.out.print("Scheduled Arrival Time: ");
            String arrivalTime = sc.nextLine().trim();
            System.out.print("Driver Name: ");
            String driver = sc.nextLine().trim();
            System.out.print("Bus ID: ");
            String bus = sc.nextLine().trim();

            try {
                stmt.execute(
                        "INSERT INTO TripOffering VALUES ('" + tripNum + "', '" + date + "', '" + startTime + "', '"
                                + arrivalTime + "', '" + driver + "', '" + bus + "')");
                System.out.print("Added new Trip Offering\n");
            } catch (SQLException msg) {
                System.out.println("Problem with transaction!\n* Check input and make sure item has no conflict.");
            }

            System.out.print("Add another Trip Offering? (y/n): ");
            String input = sc.nextLine();
            if (input.trim().charAt(0) == 'y') {
            } else {
                break;
            }
        }
    }

    // 2c. Change the driver for a given Trip offering
    public static void changeDriver(Statement stmt) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("New Driver Name: ");
        String driver = sc.nextLine().trim();
        System.out.print("Start Trip Number: ");
        String tripNum = sc.nextLine().trim();
        System.out.print("Date: ");
        String date = sc.nextLine().trim();
        System.out.print("Scheduled Start Time: ");
        String startTime = sc.nextLine().trim();

        try {
            if (stmt.executeUpdate("UPDATE TripOffering " +
                    "SET DriverName = '" + driver + "' " +
                    "WHERE TripNumber = '" + tripNum + "' AND " +
                    "Date = '" + date + "' AND " +
                    "ScheduledStartTime = '" + startTime + "'") != 0) {
                System.out.println("Updated Driver");
            } else {
                System.out.println("Problem with transaction!\n* Check input and make sure item has no conflict.");
            }
        } catch (SQLException msg) {
            System.out.println("Problem with transaction!\n* Check input and make sure item has no conflict.");
            msg.printStackTrace();
        }
    }

    // 2d. Change the bus for a given Trip offering
    public static void changeBus(Statement stmt) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("New Bus Number: ");
        String bus = sc.nextLine().trim();
        System.out.print("Start Trip Number: ");
        String tripNum = sc.nextLine().trim();
        System.out.print("Date: ");
        String date = sc.nextLine().trim();
        System.out.print("Scheduled Start Time: ");
        String startTime = sc.nextLine().trim();

        try {
            if (stmt.executeUpdate("UPDATE TripOffering " +
                    "SET BusID = '" + bus + "' " +
                    "WHERE TripNumber = '" + tripNum + "' AND " +
                    "Date = '" + date + "' AND " +
                    "ScheduledStartTime = '" + startTime + "'") != 0) {
                System.out.println("Successfully updated Bus");
            } else {
                System.out.println("Problem with transaction!\n* Check input and make sure item has no conflict.");
            }

        } catch (SQLException msg) {
            System.out.println("Problem with transaction!\n* Check input and make sure item has no conflict.");
            msg.printStackTrace();
        }
    }

    // 3. Display the stops of a given trip
    public static void displayTripStops(Statement stmt) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Trip Number: ");
        String tripNum = sc.nextLine().trim();
        System.out.println("****************************************************************");
        try {
            ResultSet result = stmt.executeQuery("SELECT * " +
                    "FROM TripStopInfo " +
                    "WHERE TripNumber = '" + tripNum + "' " +
                    "Order By SequenceNumber ");
            ResultSetMetaData data = result.getMetaData();
            int colCount = data.getColumnCount();
            for (int i = 1; i <= colCount; i++) {
                System.out.print(data.getColumnName(i) + "\t");
            }
            System.out.println();

            while (result.next()) {
                for (int i = 1; i <= colCount; i++)
                    System.out.print(result.getString(i) + "\t\t");
                System.out.println();
            }
            result.close();
            System.out.println("****************************************************************");
        } catch (SQLException msg) {
            System.out.println("Problem with transaction!\n* Check input and make sure item has no conflict.");
            msg.printStackTrace();
        }
    }

    // 4. Display the weekly schedule of a given driver and date.
    public static void displayWeekly(Statement stmt) throws ParseException, SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Driver name: ");
        String driver = sc.nextLine().trim();
        System.out.print("Date: ");
        String dateStr = sc.nextLine().trim();
    }

    // 5. Add a driver
    public static void addDriver(Statement stmt) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Driver name: ");
        String driver = sc.nextLine().trim();
        System.out.print("Phone number: ");
        String phone = sc.nextLine().trim();

        try {
            stmt.execute("INSERT INTO Driver VALUES ('" + driver + "', '" + phone + "')");
            System.out.println("Added new Driver");
        } catch (SQLException msg) {
            System.out.println("Problem with transaction!\n* Check input and make sure item has no conflict.");
            msg.printStackTrace();
        }
    }

    // 6. Add a bus
    public static void addBus(Statement stmt) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Bus ID: ");
        String bus = sc.nextLine().trim();
        System.out.print("Bus model: ");
        String model = sc.nextLine().trim();
        System.out.print("Bus year: ");
        String year = sc.nextLine().trim();

        // insert into bus
        try {
            stmt.execute("INSERT INTO Bus VALUES ('" + bus + "', '" + model + "', '" + year + "')");
            System.out.println("Added new Bus");
        } catch (SQLException msg) {
            System.out.println("****************************************************************");
        }
    }

    // 7. Delete a bus
    public static void deleteBus(Statement stmt) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Bus ID: ");
        String bus = sc.nextLine().trim();

        try {
            if (stmt.executeUpdate("DELETE FROM Bus " +
                    "WHERE BusId = '" + bus + "'") != 0) {
                System.out.println("Deleted bus");
            } else {
                System.out.println("Problem with transaction!\n* Check input and make sure item has no conflict.");
            }
        } catch (SQLException msg) {
            System.out.println("Problem with transaction!\n* Check input and make sure item has no conflict.");
            msg.printStackTrace();
        }
    }

    public static void insertTripData(Statement stmt) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Trip Number: ");
        String tripNum = sc.nextLine().trim();
        System.out.print("Date: ");
        String date = sc.nextLine().trim();
        System.out.print("Scheduled Start Time: ");
        String startTime = sc.nextLine().trim();
        System.out.print("Stop Number: ");
        String stop = sc.nextLine().trim();
        System.out.print("Scheduled Arrival Time: ");
        String arrivalTime = sc.nextLine().trim();
        System.out.print("Actual Start Time: ");
        String actualStart = sc.nextLine().trim();
        System.out.print("Actual Arrival Time: ");
        String actualArrival = sc.nextLine().trim();
        System.out.print("Passengers in: ");
        String passengerIn = sc.nextLine().trim();
        System.out.print("Passengers out: ");
        String passengerOut = sc.nextLine().trim();

        try {
            stmt.execute("INSERT INTO ActualTripStopInfo VALUES ('" + tripNum + "', '" + date + "', '" + startTime
                    + "', '" + stop + "', '" + arrivalTime
                    + "', '" + actualStart + "', '" + actualArrival + "', '" + passengerIn + "', '" + passengerOut
                    + "')");
        } catch (SQLException msg) {
            System.out.println("Problem with transaction!\n* Check input and make sure item has no conflict.");
            msg.printStackTrace();
        }
        System.out.println("Inserted Actual Trip Info.");
    }
}