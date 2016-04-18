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
        VertexSchema vClassSchema = new VertexSchema(dbSession, tClasses, "class");
        VertexSchema vRoomSchema = new VertexSchema(dbSession, tRooms, "room");

        // a simple edge denoting registrations of a student
        EdgeSchema eRegistrationSchema = new EdgeSchema(dbSession, "registration");
        eRegistrationSchema.addJoin(
            tStudents, tStudents.getColumn("ID"),
            tRegistrations, tRegistrations.getColumn("STUDENT_ID")
        );

        // an edge with multiple joins representing rooms a user has had
        // class in
        EdgeSchema eHadClassInRoomSchema = new EdgeSchema(dbSession, "hadClassInRoom");
        eHadClassInRoomSchema.addJoin(
            tStudents, tStudents.getColumn("ID"),
            tRegistrations, tRegistrations.getColumn("STUDENT_ID")
        );
        eHadClassInRoomSchema.addJoin(
            tRegistrations, tRegistrations.getColumn("CLASS_ID"),
            tClasses, tClasses.getColumn("ID")
        );
        eHadClassInRoomSchema.addJoin(
            tClasses, tClasses.getColumn("ROOM_ID"),
            tRooms, tRooms.getColumn("ID")
        );
        vStudentSchema.outgoingEdges.put(eHadClassInRoomSchema.getLabel(), eHadClassInRoomSchema);


        // store all these schemas in the graphSchema object
        graphSchema.vertexSchemas.put(vStudentSchema.getLabel(), vStudentSchema);
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
            testGetAllVertices();
            testGetVerticesByAttribute();
            testGetVertex();
            testEdgeWithMulitpleJoins();
        } catch (Exception e) {
            System.out.println("FAIL: " + e.toString());
        }
    }

    public void testGetVertex() throws Exception {
        // not yet implemented
    }

    public void testGetVerticesByAttribute() throws Exception {
        // not yet implemented
    }

    /**
     * Tests the use of an edgeschema which traverses multiple joins.  Start
     * with the Allen/student Vertex and get Room vertices that can be Found
     * through the "hadClassInRoom" edge.
     *
     * This is just exploratory code and doesnt necessarily represent the final
     * way of doing things.
     */
    public void testEdgeWithMulitpleJoins() throws Exception {
        System.out.println("\nTEST: Test Multiple Join\n======================");

        // get the Allen vertex
        Vertex allen = null;
        VertexSchema vsStudent = graphSchema.vertexSchemas.get("student");
        // TODO: create vertexSchemas.getCursor()?
        Cursor cursor = vsStudent.sourceTable.getScanIndex(dbSession).find(dbSession, null, null);
        while (cursor.next()) {
            Row row = cursor.get();
            if (row.getValue(1).getString().equals("Allen Leis")) {
                allen = new Vertex(row, vsStudent.sourceTable.getColumns());
                break;
            }
        }

        EdgeSchema esHadClassInRoom = graphSchema.edgeSchemas.get("hadClassInRoom");
        // System.out.println(esHadClassInRoom.getColumnPosition(vsStudent.sourceTable, vsStudent.sourceTable.getColumn("ID")));
        // System.out.println(esHadClassInRoom.getColumnPosition(vsStudent.sourceTable, "id"));
        Vertex room = esHadClassInRoom.getTargetVertex(allen);
        System.out.println("Source: " + allen.getAttributes().get("NAME"));
        System.out.println("Target: " + room.getAttributes().toString());


    }

    public void testGetAllVertices() throws Exception {
        System.out.println("\nTEST: Get all vertices\n======================");
        List<Vertex> vertices = new ArrayList<Vertex>();
        for (VertexSchema schema: graphSchema.vertexSchemas.values()) {
            Cursor cursor = schema.sourceTable.getScanIndex(dbSession).find(dbSession, null, null);
            while (cursor.next()) {
                vertices.add(new Vertex(cursor.get(), schema.sourceTable.getColumns()));
            }
        }
        for (Vertex v: vertices) {
            System.out.println(v.getAttributes().toString());
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
