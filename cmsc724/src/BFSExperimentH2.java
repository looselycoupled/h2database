
import experiments.Experiment;
import load.PubsLoader;
import java.sql.*;
import org.h2.jdbcx.JdbcConnectionPool;

import org.h2.engine.Engine;
import org.h2.engine.Database;
import org.h2.engine.Session;
import org.h2.index.Cursor;
import org.h2.result.Row;
import org.h2.graph.*;
import org.h2.table.Column;
import org.h2.table.Table;
import com.tinkerpop.blueprints.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

/**
 * Breadth first search experiment using SQL
 */
public class BFSExperimentH2 extends Experiment
{

    private String dbName;
    private Session dbSession;
    private GraphSchema graphSchema = new GraphSchema("coauthor");
    private Integer numIterations = 100;
    private double totalTime = 0.0;
    private double loadTime;

    /**
     * Method to perform database, etc. setup
     */
    @Override
    public void setup() {
        try {
            loader = new PubsLoader();
            loader.load();
            dbName = "mem:pubs";
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
    public void conduct() throws SQLException {
        long startTime;
        long endTime;
        double duration;
        Random rand = new Random();
        startTime = System.nanoTime();
        GraphSchema gs = makeGraphSchema();
        Graph g = makeGraph(gs);
        //testStuff(g);
        // Vertex v = g.getVertices().get(15);
        // testBFS(g, v);
        endTime = System.nanoTime();
        loadTime = (endTime - startTime) * 0.000000001;
        System.out.println(String.format("\n==\nGraph creation phase completed in %.2f seconds\n==\n", loadTime));

        // get random rootnodes for the graph
        for (int i = 0; i < numIterations; i++){

            int index = rand.nextInt(g.getVertices().size());
            Vertex v = g.getVertices().get(index);// get Amol
            startTime = System.nanoTime();
            Integer nodesFound = testBFS(g, v);
            endTime = System.nanoTime();
            duration = (endTime - startTime) * 0.000000001;
            System.out.println(String.format("\n==\nBFS completed in %.2f seconds\n==\n", duration));
            totalTime += duration;

            addResult(v.row.getValue(0).getInt(), duration, nodesFound);
        }
        //System.out.println(String.format("\n==\n50 runs of BFS completed in %.2f seconds\n==\n", totalTime));

        // pause to take heap dump
        // pause();
    }

    public void testStuff(Graph g) {
        Vertex v = g.getVertices().get(15);
        for (Edge e: v.getEdges(Direction.OUT)){
            Vertex dstV = e.getDstVertex();
            System.out.println("source vertex is ");
            v.print();
            System.out.println("edge type is " + e.getLabel());
            System.out.println("destination vertex is ");
            dstV.print();
        }
        for (Vertex dstV: v.getVertices(Direction.OUT)){
            System.out.println("source vertex is ");
            v.print();
            // System.out.println("edge type is " + e.getLabel());
            System.out.println("destination vertex is ");
            dstV.print();
        }
    }

    /**
     * Grabs table/column references and creates appropriate graph schema objects
     */
    public Graph makeGraph(GraphSchema graphSchema) throws SQLException {
        List<Vertex> vertices = graphSchema.vertexSchemas.get("author").findAll();
        return new Graph(vertices);
    }

    public GraphSchema makeGraphSchema() {
        Database db = Engine.getInstance().getDatabases().get(dbName);
        dbSession = db.getSystemSession();

        Table tAuthors = db.getTableOrViewByName("AUTHOR").get(0);
        Table tAuthorPublications = db.getTableOrViewByName("AUTHORPUB").get(0);

        VertexSchema vAuthorSchema = new VertexSchema(dbSession, tAuthors, "author");

        // create a "classmates" edge
        EdgeSchema eCoauthorSchema = new EdgeSchema(dbSession, "coauthored");
        eCoauthorSchema.addJoin(
            tAuthors, tAuthors.getColumn("ID"),
            tAuthorPublications, tAuthorPublications.getColumn("AID"), new ArrayList<String>()
        );
        eCoauthorSchema.addJoin(
            tAuthorPublications, tAuthorPublications.getColumn("PID"),
            tAuthorPublications, tAuthorPublications.getColumn("PID"), new ArrayList<String>()
        );
        eCoauthorSchema.addJoin(
            tAuthorPublications, tAuthorPublications.getColumn("AID"),
            tAuthors, tAuthors.getColumn("ID"), new ArrayList<String>()
        );

        eCoauthorSchema.addVertexSchemas(vAuthorSchema, vAuthorSchema);
        vAuthorSchema.addEdgeSchema(eCoauthorSchema);

        GraphSchema graphSchema = new GraphSchema("coauthors");

        graphSchema.vertexSchemas.put(vAuthorSchema.getLabel(), vAuthorSchema);
        graphSchema.edgeSchemas.put(eCoauthorSchema.getLabel(), eCoauthorSchema);

        return graphSchema;
    }

    // public Graph makeGraph() {
    //     EdgeSchema eSchema = graphSchema.edgeSchemas.get("coauthored");
    //     List<Edge> edges = eSchema.connectVertices(vertices, vertices);
    //     return new Graph(vertices, edges);
    // }


    public Integer testBFS(Graph g, Vertex rootNode) {
        // run BFS
        Map<Vertex, Integer> distances = new HashMap<Vertex, Integer>();
        Queue queue = new LinkedList();
        queue.add(rootNode);
        distances.put(rootNode, 0);
        System.out.println("root node is ");
        rootNode.print();
        while(!queue.isEmpty()) {
            Vertex node = (Vertex)queue.remove();
            for (Vertex v: node.getVertices(Direction.OUT)){
                // v.print();
                if (!distances.containsKey(v)){
                    Integer current_distance = distances.get(node);
                    distances.put(v, current_distance+1);
                    queue.add(v);
                }
            }
        }
        // show results
        // for (Map.Entry<Vertex, Integer> entry : distances.entrySet()){
        //     Vertex v = entry.getKey();
        //     Integer d = entry.getValue();
        //     if (d == 1){
        //         rootNode.print();
        //         System.out.println("co-author with ");
        //         v.print();
        //     } else if (d > 1){
        //         rootNode.print();
        //         System.out.println("is one co-author hop from");
        //         v.print();
        //     }
        // }

        System.out.println(String.format("Total vertices found: %d", distances.size() - 1));

        // return total vertices found (minus original vertex)
        return distances.size() - 1;
    }

    // public void report() {
    //     // System.out.println(String.format("\n==\nGraph creation phase completed in %.2f seconds\n==\n", loadTime));
    //     System.out.println(String.format("\n==\nBFS took an average time of %.2f time per iteration\n==\n", totalTime/numIterations));
    // }

    /**
     *
     */
    public static void main(String[] args) {
        BFSExperimentH2 driver = new BFSExperimentH2();
        driver.start();
    }

}
