/* TestServer.java
 */


import org.h2.tools.Server;



public class TestServer
{
	public static void main(String[] args) throws Exception {
      // start the TCP Server


      Server server = Server.createTcpServer("-tcpPort", "9123", "-tcpAllowOthers");


      server.start();


      // stop the TCP Server
      server.stop();

	}
}
