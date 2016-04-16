/* TestServer.java
 */


import org.h2.engine.Engine;
import org.h2.engine.Database;
import org.h2.engine.ConnectionInfo;
import org.h2.engine.Database;
import org.h2.engine.Session;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.tools.Server;
import org.h2.result.Row;
import org.h2.index.Cursor;
import org.h2.index.Index;
import org.h2.value.Value;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import load.PubsLoader;
import load.SchoolLoader;


/**
 * Exploratory code for understanding the H2 codebase
 */
public class TestDriver
{

  private Engine engine = Engine.getInstance();
  private HashMap<String, Database> DATABASES = engine.getDatabases();


  private Database getDatabase(String name) {
    return DATABASES.get(name);
  }


  private void printTableData(Table t, Session session, Cursor cursor) {
    System.out.println("\nRows in " + t.getName() + " table\n======================");

    // print columns in table
    Column[] columns = t.getColumns();
    if (columns.length > 0) {
      for (Column c: columns) {
        System.out.print(c.toString() + "\t");
      }
      System.out.println();
    }

    // print rows
    while (cursor.next()) {
        Row row = cursor.get();
        Value[] values = row.getValueList();
        for (Value v: values) {
          System.out.print(v.toString() + "\t");
        }
        System.out.println();
    }

  }

  public void work() throws SQLException {

    System.out.println("Databases\n==========");
    for (String key : DATABASES.keySet()) {
      System.out.println(key);
    }

    System.out.println("\nFetching First Database Found\n==========");
    Database db = DATABASES.get(DATABASES.keySet().iterator().next());
    System.out.println(db.toString());
    System.out.println("isMultiVersion: " + db.isMultiVersion());

    ArrayList<Table> tables = db.getAllTablesAndViews(false);
    System.out.println("\nTables Found: " + tables.size() + "\n===============");
    for (Table t: tables){
      System.out.println(t.getName());
    }

    // Table t = db.getTableOrViewByName("STUDENTS").get(0);
    Session session = db.getSystemSession();
    for (Table t: tables){
      Cursor cursor = t.getScanIndex(session).find(session, null, null);
      printTableData(t, session, cursor);
    }

  }

  public void start() throws SQLException {
    //   SchoolLoader loader = new SchoolLoader();
      PubsLoader loader = new PubsLoader();
      loader.load();
      work();
  }

  public static void main(String[] args) throws SQLException {
    TestDriver driver = new TestDriver();
    driver.start();
  }

}
