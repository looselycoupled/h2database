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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Breadth first search experiment using SQL
 */
public class BFSExperimentSQL2 extends Experiment
{
    public Integer numOfRuns = 5;
    public Random generator = new Random();
    private HashMap<String, Integer> maxTableIdMap = new HashMap<String, Integer>();

    private class Author {
        public Integer id;
        public String name;

        public Author(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

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

            search(startID);

            // report execution time
            timingReport("this iteration", startTime);
        }

    }


    public void search(Integer startId) {
        Map<Integer, Integer> distances = new HashMap<Integer, Integer>();
        Queue queue = new LinkedList();
        Integer totalFound = 0;

        queue.add(startId);
        distances.put(startId, 0);

        while(!queue.isEmpty()) {
            Integer id = (Integer)queue.remove();
            for (Author a: getChildren(id)){

                if (!distances.containsKey(a.id)){
                    Integer current_distance = distances.get(startId);
                    distances.put(a.id, current_distance + 1);
                    queue.add(a.id);
                    totalFound += 1;
                }

            }
        }

        System.out.println(String.format("Total nodes found: %d", totalFound));


    }

    public List<Author> getChildren(Integer parentID) {
        List<Author> children = new ArrayList<Author>();
        Statement stmt = null;

        String query = "select distinct author.*"
            + " from author join authorpub on author.id = authorpub.aid"
            + " where authorpub.pid in (select pid from authorpub where aid = %d)"
            + " and author.id <> %d";

        query = String.format(query, parentID, parentID);

        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                children.add(new Author(rs.getInt("ID"), rs.getString("NAME")));
            }
        } catch (SQLException e ) {
            System.out.println(e);
        }
        return children;
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
        Integer max = maxIdFromTable(table);
        return generator.nextInt(max) + 1;
    }


    /**
     *
     */
    public static void main(String[] args) {
        BFSExperimentSQL2 driver = new BFSExperimentSQL2();
        driver.start();
    }

}
