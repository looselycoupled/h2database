/**
 * Abstract class to load data into a new database
 */
package load;

import org.h2.jdbcx.JdbcConnectionPool;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * Abstract class for loading data into a new in-memory database
 */
public abstract class Loader {

    public String dbName;
    public String username = "user";
    public String password = "password";

    protected Connection conn;


    /**
     * Creates the in memory database using class instance variables for
     * database name, username, password
     */
    private void start() throws SQLException {
        JdbcConnectionPool ds = JdbcConnectionPool.create("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1", username, password);
        conn = ds.getConnection();
    }

    /**
     * Closes database connection object
     */
    private void shutdown() throws SQLException {
        conn.close();
    }

    /**
     * Abstract method to actually load the data (usually from CSVs)
     */
    public abstract void work() throws SQLException;

    /**
     * Public method to kick off the work of creating/loading database
     */
    public void load() throws SQLException {
        start();
        work();
        shutdown();
    }

}
