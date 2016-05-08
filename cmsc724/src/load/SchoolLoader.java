/**
 * Loads school dataset into a new database
 */
package load;

import java.sql.SQLException;
import load.Loader;



/**
 * Concrete class for loading Publication dataset into a new in-memory database
 */
public class SchoolLoader extends Loader {

    /**
     * Constructor to set the expected db name
     */
    public SchoolLoader() {
        dbName = "school";
    }

    /**
     * Provides implementation for actual SQL inserts
     */
    @Override
    public void work() throws SQLException {
      conn.createStatement().executeUpdate("CREATE TABLE classes (class_id INT PRIMARY KEY, "
        + "code VARCHAR(20), title VARCHAR(255), semester VARCHAR(20), "
        + "academic_year INT, room_id INT) "
        + "AS SELECT * FROM CSVREAD('data/classes.csv');");

      conn.createStatement().executeUpdate("CREATE TABLE students (student_id INT PRIMARY KEY, name VARCHAR(255)) "
        + "AS SELECT * FROM CSVREAD('data/students.csv');");

      conn.createStatement().executeUpdate("CREATE TABLE registrations (class_id INT, student_id INT, grade VARCHAR(2)) "
        + "AS SELECT * FROM CSVREAD('data/registrations.csv');");

    conn.createStatement().executeUpdate("CREATE TABLE rooms (room_id INT PRIMARY KEY, building VARCHAR(255), "
      + "room_number VARCHAR(20)) AS SELECT * FROM CSVREAD('data/rooms.csv');");

    }

}
