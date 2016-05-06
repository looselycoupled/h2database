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
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

/**
 * Breadth first search experiment using SQL
 */
public class RandomVertexExperimentSQL extends Experiment
{
    public Integer numOfRuns = 1000;
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

        String query = "select * from author where ID = %s";

        for (Integer randomID: randomIDList("author")) {
            try {
                Statement stmt = null;
                stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery(String.format(query, randomID));
                rs.next();
                System.out.println(String.format("Random Record: %d", rs.getInt("ID")));
            } catch (Exception e) {
                System.out.println(e);
            }
        }


    }


    /**
     * Convenience method to give random list of IDs
     */
    private List<Integer> randomIDList(String table) {
        Integer max = maxIdFromTable(table);
        List<Integer> randomIDs = new ArrayList<Integer>();

        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());

        for (int i=0; i<numOfRuns; i++) {
            randomIDs.add(rand.nextInt(max) + 1);
        }
        return randomIDs;
    }


    /**
     * Convenience method to join list of ints into a string
     */
    private String joinInts(List<Integer> list) {
        return list.stream().map(Object::toString).collect(Collectors.joining(", "));
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
        RandomVertexExperimentSQL driver = new RandomVertexExperimentSQL();
        driver.start();
    }

}
