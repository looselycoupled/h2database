/**
 * Abstract class to load data into a new database
 */
package experiments;

import java.sql.SQLException;
import java.sql.Connection;
import load.Loader;

/**
 * Abstract class for loading data into a new in-memory database
 */
public abstract class Experiment {

    public Loader loader;
    public Connection conn;

    /**
     *
     */
    public void start() {
        try {
            long startTime = System.nanoTime();
            setup();
            long endTime = System.nanoTime();
            double duration = (endTime - startTime) * 0.000000001;
            System.out.println(String.format("\n==\nSetup phase completed in %.2f seconds\n==\n", duration));

            startTime = System.nanoTime();
            conduct();
            endTime = System.nanoTime();
            duration = (endTime - startTime) * 0.000000001;
            System.out.println(String.format("\n==\nExperimental phase completed in %.2f seconds\n==\n", duration));
        } catch (SQLException e) {

        }

        report();
    }

    /**
     * Abstract method to do any experiment setup (ex: load database)
     */
    public abstract void setup() throws SQLException;

    /**
     * Abstract method to actually load the data (usually from CSVs)
     */
    public abstract void conduct() throws SQLException;

    /**
     * Overideable method to do any experiment reporting
     */
    public void report() {};


}
