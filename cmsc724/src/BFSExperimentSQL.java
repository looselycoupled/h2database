
import experiments.Experiment;
import load.PubsLoader;
import java.sql.*;
import org.h2.jdbcx.JdbcConnectionPool;
import java.util.Random;
import java.util.HashMap;

/**
 * Breadth first search experiment using SQL
 */
public class BFSExperimentSQL extends Experiment
{

    private HashMap<String, Integer> maxTableIdMap = new HashMap<String, Integer>();

    /**
     * Method to perform database, etc. setup
     */
    @Override
    public void setup() {
        try {
            loader = new PubsLoader();
            loader.load();

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
        proofOfConcept();
    }

    /**
     * Example method to perform some basic SQL queries
     */
    public void proofOfConcept() {
        System.out.println("RANDOM IDs from AUTHOR table\n=========");
        System.out.println(randomIdFromTable("author"));
        System.out.println(randomIdFromTable("author"));
        System.out.println(randomIdFromTable("author") + "\n");


        System.out.println("Looking for professor in AUTHOR table\n=========");
        Statement stmt = null;
        String query = "select * from author where name like '%Amol Deshpande%'";
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String aname = rs.getString("NAME");
                Integer aid = rs.getInt("ID");
                System.out.println(aname);
                System.out.println(aid);
            }
        } catch (SQLException e ) {
            System.out.println(e);
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
        Integer id = 0;
        Integer max = maxIdFromTable(table);

        Random generator = new Random();
        id = generator.nextInt(max - 1 + 1) + 1;
        return id;
    }


    /**
     *
     */
    public static void main(String[] args) {
        BFSExperimentSQL driver = new BFSExperimentSQL();
        driver.start();
    }

}
