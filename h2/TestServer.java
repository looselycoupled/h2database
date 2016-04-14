/* TestServer.java
 */


import org.h2.tools.Server;
import org.h2.engine.Engine;
import org.h2.engine.Database;
import java.util.HashMap;


public class TestServer
{
	public static void main(String[] args) throws Exception {
      // start the TCP Server

	  // Server server = new Server();
      Server server = Server.createTcpServer();

      server.start();

      Engine engine = Engine.getInstance();

      HashMap<String, Database> db = engine.getDatabase();

      for (String key : db.keySet()) {
      	System.out.println("key is " + key);
      }
      System.out.println("done printing database keys");

      // stop the TCP Server
      server.stop();



	}
}
