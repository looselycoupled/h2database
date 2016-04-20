/*
 * Exploratory code to test graph construction classes.  Some of this code will
 * be reworked and added to the org.h2.graph.Graph class when it's ready.
 */

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
import java.sql.SQLException;
import load.SchoolLoader;


/**
 * Exploratory code to test graph construction classes
 */
public class TestGraphDefinition {

    private String dbName;
    private Session dbSession;
    private GraphSchema graphSchema = new GraphSchema("school");

    /**
     * Creates database and loads up the school dataset
     */
    public void loadDatabase() throws SQLException {
        SchoolLoader loader = new SchoolLoader();
        loader.load();
        dbName = "mem:school";
    }

    /**
     * Grabs table/column references and creates appropriate graph schema objects
     */
    public void loadSchemas() throws Exception {
        Database db = Engine.getInstance().getDatabases().get(dbName);
        dbSession = db.getSystemSession();

        Table tStudents = db.getTableOrViewByName("STUDENTS").get(0);
        Table tClasses = db.getTableOrViewByName("CLASSES").get(0);
        Table tRegistrations = db.getTableOrViewByName("REGISTRATIONS").get(0);
        Table tRooms = db.getTableOrViewByName("ROOMS").get(0);

        VertexSchema vStudentSchema = new VertexSchema(dbSession, tStudents, "student");
        VertexSchema vRegistrationSchema = new VertexSchema(dbSession, tRegistrations, "registration");
        VertexSchema vClassSchema = new VertexSchema(dbSession, tClasses, "class");
        VertexSchema vRoomSchema = new VertexSchema(dbSession, tRooms, "room");

        // a simple edge denoting registrations of a student
        // an edge only exists if there exists a node on either side of it
        // edge schema gives us one or more join that we must traverse
        EdgeSchema eRegistrationSchema = new EdgeSchema(dbSession, "registration");
        eRegistrationSchema.addJoin(
            tStudents, tStudents.getColumn("ID"), 0,
            tRegistrations, tRegistrations.getColumn("STUDENT_ID"), 1
        );

        // an edge with multiple joins representing rooms a user has had
        // class in
        EdgeSchema eHadClassInRoomSchema = new EdgeSchema(dbSession, "hadClassInRoom");
        eHadClassInRoomSchema.addJoin(
            tStudents, tStudents.getColumn("ID"), 0,
            tRegistrations, tRegistrations.getColumn("STUDENT_ID"), 1
        );
        eHadClassInRoomSchema.addJoin(
            tRegistrations, tRegistrations.getColumn("CLASS_ID"), 0,
            tClasses, tClasses.getColumn("ID"), 0
        );
        eHadClassInRoomSchema.addJoin(
            tClasses, tClasses.getColumn("ROOM_ID"), 5,
            tRooms, tRooms.getColumn("ID"), 0
        );
        // vStudentSchema.outgoingEdges.put(eHadClassInRoomSchema.getLabel(), eHadClassInRoomSchema);


        // store all these schemas in the graphSchema object
        graphSchema.vertexSchemas.put(vStudentSchema.getLabel(), vStudentSchema);
        graphSchema.vertexSchemas.put(vRegistrationSchema.getLabel(), vRegistrationSchema);
        graphSchema.vertexSchemas.put(vClassSchema.getLabel(), vClassSchema);
        graphSchema.vertexSchemas.put(vRoomSchema.getLabel(), vRoomSchema);
        graphSchema.edgeSchemas.put(eRegistrationSchema.getLabel(), eRegistrationSchema);
        graphSchema.edgeSchemas.put(eHadClassInRoomSchema.getLabel(), eHadClassInRoomSchema);

    }


    /**
     * A series of tests
     */
    public void runTests() {

        try {
            testCreateSingleJoinEdge();
            // testGetAllVertices();
            // testGetVerticesByAttribute();
            // testEdgeWithMulitpleJoins();
        } catch (Exception e) {
            System.out.println("FAIL: " + e.toString());
        }
        testCreateMultipleJoinEdge();
        
    }


    // public void testGetVerticesByAttribute() throws Exception {
    //     System.out.println("\nTEST: testGetVerticesByAttribute\n======================");
    //     VertexSchema vsStudent = graphSchema.vertexSchemas.get("student");

    //     List<Vertex> vertices = vsStudent.findByAttribute("name", "Allen Leis");
    //     System.out.println(vertices.get(0).getAttributes().toString());

    //     vertices = vsStudent.findByAttribute("id", 2);
    //     System.out.println(vertices.get(0).getAttributes().toString());
    // }

    /**
     * Tests the use of an edgeschema which traverses multiple joins.  Start
     * with the Allen/student Vertex and get Room vertices that can be Found
     * through the "hadClassInRoom" edge.
     *
     * This is just exploratory code and doesnt necessarily represent the final
     * way of doing things.
     */
    // public void testEdgeWithMulitpleJoins() throws Exception {
    //     System.out.println("\nTEST: Test Multiple Join\n======================");

    //     // get the Allen vertex
    //     VertexSchema vsStudent = graphSchema.vertexSchemas.get("student");
    //     Vertex allen = vsStudent.findByAttribute("name", "Allen Leis").get(0);

    //     EdgeSchema esHadClassInRoom = graphSchema.edgeSchemas.get("hadClassInRoom");
    //     Vertex room = esHadClassInRoom.getTargetVertex(allen);
    //     System.out.println("Source: " + allen.getAttributes().get("NAME"));
    //     System.out.println("Target: " + room.getAttributes().toString());


    // }

    public void testCreateMultipleJoinEdge() {
        System.out.println("\nTEST: Creating a multiple join edge\n======================");
        // create all the vertices from the vertexSchemas and add them to the Graph
        List<Vertex> srcVertices = graphSchema.vertexSchemas.get("student").findAll();
        List<Vertex> dstVertices = graphSchema.vertexSchemas.get("room").findAll();
        EdgeSchema eSchema = graphSchema.edgeSchemas.get("hadClassInRoom");
        List<Edge> edges = eSchema.connectVertices(srcVertices, dstVertices);
        for (Vertex srcV: srcVertices) {
            for (Edge e: srcV.getEdges(Direction.OUT)){
                Vertex dstV = e.getDstVertex();
                System.out.println("source vertex is ");
                srcV.print();
                System.out.println("edge type is " + e.getLabel());
                System.out.println("destination vertex is ");
                dstV.print();
            }
        }
    }

    public void testCreateSingleJoinEdge() {
        System.out.println("\nTEST: Create a single join edge \n======================");
        // create all the vertices from the vertexSchemas and add them to the Graph
        List<Vertex> srcVertices = graphSchema.vertexSchemas.get("student").findAll();
        List<Vertex> dstVertices = graphSchema.vertexSchemas.get("registration").findAll();
        EdgeSchema eSchema = graphSchema.edgeSchemas.get("registration");
        List<Edge> edges = eSchema.connectVertices(srcVertices, dstVertices);
        for (Vertex srcV: srcVertices) {
            for (Edge e: srcV.getEdges(Direction.OUT)){
                Vertex dstV = e.getDstVertex();
                System.out.println("source vertex is ");
                srcV.print();
                System.out.println("edge type is " + e.getLabel());
                System.out.println("destination vertex is ");
                dstV.print();
            }
        }
    }

    public void testGetAllVertices() throws Exception {
        System.out.println("\nTEST: Get all vertices\n======================");
        Graph graph = new Graph();
        // create all the vertices from the vertexSchemas and add them to the Graph
        List<Vertex> vertices = new ArrayList<Vertex>();
        for (VertexSchema schema: graphSchema.vertexSchemas.values()) {
            graph.addVertices(schema.findAll());
        }
        // add the vertices to the Graph
        for (Vertex v: graph.getVertices()) {
            for (String key: v.getPropertyKeys()) {
                System.out.println(v.getProperty(key).toString());
            }
        }
    }


    /**
     * Kicks of the actual work to perform
     */
    public void start() throws Exception {
        loadDatabase();
        loadSchemas();
        runTests();
    }

    public static void main(String[] args) throws Exception {
        TestGraphDefinition driver = new TestGraphDefinition();
        driver.start();
    }
}