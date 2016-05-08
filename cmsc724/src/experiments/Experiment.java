/**
 * Abstract class to load data into a new database
 */
package experiments;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import load.Loader;

/**
 * Abstract class for loading data into a new in-memory database
 */
public abstract class Experiment {

    public Loader loader;
    public Connection conn;
    public List<ResultRecord> results = new ArrayList<ResultRecord>();

    public class ResultRecord {
        public Integer id;
        public double elapsedTime;
        public Integer nodesFound;

        public ResultRecord(Integer id, double elapsedTime, Integer nodesFound) {
            this.id = id;
            this.elapsedTime = elapsedTime;
            this.nodesFound = nodesFound;
        }
    }

    /**
     *
     */
    public void pause() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("System paused. Hit enter to continue");
        scanner.next();
    }

    /**
     *
     */
    public void addResult(Integer id, double elapsedTime, Integer nodesFound) {
        results.add(new ResultRecord(id, elapsedTime, nodesFound));
    }

    /**
     * Overideable method to do any experiment reporting
     */
    public void report() {
        System.out.println("\n=====\nREPORT\n=====");
        for (ResultRecord rr: results) {
            System.out.println(String.format("%d,%.2f,%d", rr.id, rr.elapsedTime, rr.nodesFound));
        }
    };

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

        if (results.size() > 0) {
            report();
        }
    }


    /**
     * Convenience method to print elapsed time and current timestamp
     */
    protected double timingReport(String label, long startTime) {
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) * 0.000000001;
        System.out.println(String.format("\n=====\nElapsed time for %s : %.2f seconds", label, duration));

        Date date = new Date();
        System.out.println("Current time is: " + date.toString() + "\n=====");
        return duration;
    }

    /**
     * Abstract method to do any experiment setup (ex: load database)
     */
    public abstract void setup() throws SQLException;

    /**
     * Abstract method to actually load the data (usually from CSVs)
     */
    public abstract void conduct() throws SQLException;


}
