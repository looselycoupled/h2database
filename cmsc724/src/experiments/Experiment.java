/**
 * Abstract class to load data into a new database
 */
package experiments;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.Date;

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
        Date date = new Date();
        System.out.println("=====\nSYSTEM START\nCurrent time is: " + date.toString() + "\n=====");

        try {
            long startTime = System.nanoTime();
            setup();
            timingReport("SETUP phase", startTime);

            startTime = System.nanoTime();
            conduct();
            timingReport("EXPERIMENTAL phase", startTime);
        } catch (SQLException e) {
            System.out.println(e);
            System.exit(1);
        }

        report();
    }


    /**
     * Convenience method to print elapsed time and current timestamp
     */
    protected void timingReport(String label, long startTime) {
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) * 0.000000001;
        System.out.println(String.format("\n=====\nElapsed time for %s : %.2f seconds", label, duration));

        Date date = new Date();
        System.out.println("Current time is: " + date.toString() + "\n=====");
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
