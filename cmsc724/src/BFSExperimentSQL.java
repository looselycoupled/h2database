/**
 * Breadth first search experiment using SQL
 *
 * This problem may be solveable in one go if H2 supported recursive queries properly
 *     http://www.vertabelo.com/blog/technical-articles/sql-recursive-queries
 *     http://www.h2database.com/html/advanced.html#recursive_queries
 */

import experiments.Experiment;
import load.PubsLoader;
import java.sql.*;
import org.h2.jdbcx.JdbcConnectionPool;
import java.util.Random;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

/**
 * Breadth first search experiment using SQL
 */
public class BFSExperimentSQL extends Experiment
{
    public Integer numOfRuns = 1;
    private HashMap<String, Integer> maxTableIdMap = new HashMap<String, Integer>();

    /**
     * Method to perform database, etc. setup
     */
    @Override
    public void setup() {
        try {
            // load database
            loader = new PubsLoader();
            loader.load();

            // store a connection to this database
            JdbcConnectionPool ds = JdbcConnectionPool.create("jdbc:h2:mem:" + loader.dbName + ";DB_CLOSE_DELAY=-1", loader.username, loader.password);
            conn = ds.getConnection();

        } catch (SQLException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    /**
     * Method to actually conduct the experiment
     */
    @Override
    public void conduct() {

        // perform requested number of runs
        for (int i = 0; i < numOfRuns; i++) {

            long startTime = System.nanoTime();
            Integer startID = randomIdFromTable("author");
            System.out.println(String.format("\n\n=====\nStarting new run with ID: %d\n=====", startID));

            if (false) {
                // perform recursive search
                List<Integer> parentIDs = new ArrayList<Integer>();
                List<Integer> ignoredIDs = new ArrayList<Integer>();
                parentIDs.add(startID);
                ignoredIDs.add(startID);

                getChildren(parentIDs, ignoredIDs);
            } else {
                // perform non-recursive search
                nonRecursiveBFS(startID);
            }

            // report execution time
            long endTime = System.nanoTime();
            double duration = (endTime - startTime) * 0.000000001;
            System.out.println(String.format("\nExecution time for this iteration: %.2f seconds\n", duration));
        }

    }


    /**
     * Convenience method to join list of ints into a string
     */
    private String joinInts(List<Integer> list) {
        return list.stream().map(Object::toString).collect(Collectors.joining(", "));
    }

    /**
     * Recursively explore connected graph by performing a breadth first search
     */
    public List<Integer> getChildren(List<Integer> parentIDs, List<Integer> ignoredIDs) {
        List<Integer> childIDs = new ArrayList<Integer>();
        Statement stmt = null;

        String query = "select distinct author.id"
            + " from author join authorpub on author.id = authorpub.aid "
            + " where authorpub.pid in (select pid from authorpub where aid in (%s)) "
            + " and author.id not in (%s) ";
        query = String.format(query, joinInts(parentIDs), joinInts(ignoredIDs));
        // System.out.println(query + "\n\n");

        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                childIDs.add(rs.getInt("ID"));
                ignoredIDs.add(rs.getInt("ID"));
            }
        } catch (SQLException e ) {
            System.out.println(e);
        }

        // System.out.println(childIDs.size());
        System.out.println("\nCurrent IDs: " + Integer.toString(childIDs.size()));
        System.out.println("Known IDs: " + Integer.toString(ignoredIDs.size()));


        if (!childIDs.isEmpty()) {
            getChildren(childIDs, ignoredIDs);
        }
        return childIDs;
    }

    public List<Integer> nonRecursiveGetChildren(List<Integer> parentIDs, List<Integer> ignoredIDs) {
        List<Integer> childIDs = new ArrayList<Integer>();
        Statement stmt = null;

        String query = "select distinct author.id"
            + " from author join authorpub on author.id = authorpub.aid "
            + " where authorpub.pid in (select pid from authorpub where aid in (%s)) "
            + " and author.id not in (%s) ";
        query = String.format(query, joinInts(parentIDs), joinInts(ignoredIDs));

        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                childIDs.add(rs.getInt("ID"));
            }
        } catch (SQLException e ) {
            System.out.println(e);
        }
        return childIDs;
    }

    public void nonRecursiveBFS(Integer startId) {
        List<Integer> knownIDs = new ArrayList<Integer>();
        knownIDs.add(startId);

        List<Integer> currentIDs = new ArrayList<Integer>();
        currentIDs.add(startId);

        while (!currentIDs.isEmpty()) {
            System.out.println("\nCurrent IDs: " + Integer.toString(currentIDs.size()));
            System.out.println("Known IDs: " + Integer.toString(knownIDs.size()));
            currentIDs = nonRecursiveGetChildren(currentIDs, knownIDs);
            knownIDs.addAll(currentIDs);
        }

    }

    /**
     * Returns largest ID from given table and caches value in the maxTableIdMap
     * property
     */
    public Integer maxIdFromTable(String table) {
        Integer maxid = 0;

        if (maxTableIdMap.containsKey(table)) {
            maxid = maxTableIdMap.get(table);

        } else {

            Statement stmt = null;
            String query = String.format("select max(ID) as id from %s", table);
            try {
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                maxid = rs.getInt("ID");
            } catch (SQLException e ) {
                System.out.println(e);
            }
            maxTableIdMap.put(table, maxid);
        }
        return maxid;
    }

    /**
     * Returns a random ID from the given table.  This method assumes the ID is
     * an auto-incrementing integer and uses the max(id) from the table as the
     * upper bound.
     */
    public Integer randomIdFromTable(String table) {
        Random generator = new Random();
        Integer max = maxIdFromTable(table);
        return generator.nextInt(max - 1 + 1) + 1;
    }


    /**
     *
     */
    public static void main(String[] args) {
        BFSExperimentSQL driver = new BFSExperimentSQL();
        driver.start();
    }

}
