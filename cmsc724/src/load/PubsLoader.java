/**
 * Loads publication data into a new database
 */

package load;

import java.sql.SQLException;
import load.Loader;



/**
 * Concrete class for loading Publication dataset into a new in-memory database
 */
public class PubsLoader extends Loader {

    /**
     * Constructor to set the expected db name
     */
    public PubsLoader() {
        dbName = "pubs";
    }

    /**
     * Provides implementation for actual SQL inserts
     */
    @Override
    public void work() throws SQLException {
        conn.createStatement().executeUpdate("CREATE TABLE Author (a_id INT PRIMARY KEY, name VARCHAR(255)) "
          + "AS SELECT * FROM CSVREAD('data/authors.csv');");

        conn.createStatement().executeUpdate("CREATE TABLE Publication (p_id INT PRIMARY KEY, title VARCHAR(255)) "
          + "AS SELECT * FROM CSVREAD('data/publications.csv');");

        conn.createStatement().executeUpdate("CREATE TABLE AuthorPub (a_id INT, p_id INT) "
          + "AS SELECT * FROM CSVREAD('data/authorpub.csv');");
    }

}
