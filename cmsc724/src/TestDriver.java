/* TestServer.java
 */


import org.h2.engine.Engine;
import org.h2.engine.Database;
import org.h2.engine.ConnectionInfo;
import org.h2.engine.Database;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.table.Table;
import org.h2.tools.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


public class TestDriver
{

  private Engine engine = Engine.getInstance();

  private HashMap<String, Database> DATABASES = engine.getDatabases();

  private Server server;


  public Database createDatabase() {
    ConnectionInfo ci = new ConnectionInfo("mem:");
    Database db = new Database(ci, null);
    return db;
  }

  public void loadDatabase(Database db) {

  }

  public void setup() throws SQLException {
    Database db = createDatabase();
    loadDatabase(db);

  }

  public void setupViaJDBC() throws SQLException {
    JdbcConnectionPool ds = JdbcConnectionPool.create("jdbc:h2:mem:school;DB_CLOSE_DELAY=-1", "user", "password");
    Connection conn = ds.getConnection();

    conn.createStatement().executeUpdate("CREATE TABLE students (id INT PRIMARY KEY, name VARCHAR(255)) "
      + "AS SELECT * FROM CSVREAD('data/students.csv');");

    conn.close();
  }

  private Database getDatabase(String name) {
    return DATABASES.get(name);
  }

  public void work(){
    System.out.println("Databases\n==========");
    for (String key : DATABASES.keySet()) {
      System.out.println("key is " + key);
    }

    System.out.println("\nFetching Database\n==========");
    Database db = getDatabase("mem:school");
    System.out.println(db.toString());


    // System.out.println("\nFetching Table 'data'\n==========");
    // ArrayList<Table> tables = db.getTableOrViewByName("data");
    // System.out.println(tables.get(0).toString());

  }

  public void start() throws SQLException {
    setupViaJDBC();
    work();
  }

	public static void main(String[] args) throws SQLException {
    TestDriver driver = new TestDriver();
    driver.start();
	}
}
