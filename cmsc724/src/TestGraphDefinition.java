/*
 * Exploratory code to test graph construction classes.  Some of this code will
 * be reworked and added to the org.h2.graph.Graph class when it's ready.
 */

import org.h2.engine.Engine;
import org.h2.engine.Database;
import org.h2.engine.Session;
import org.h2.index.Cursor;
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
    private GraphSchema graphSchema = new GraphSchema();

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
    public void loadSchemas() {
        Database db = Engine.getInstance().getDatabases().get(dbName);
        dbSession = db.getSystemSession();
        Table tStudents = db.getTableOrViewByName("STUDENTS").get(0);
        Table tClasses = db.getTableOrViewByName("CLASSES").get(0);
        Table tRegistrations = db.getTableOrViewByName("REGISTRATIONS").get(0);
        Table tRooms = db.getTableOrViewByName("ROOMS").get(0);

        VertexSchema vStudentSchema = new VertexSchema(tStudents);
        VertexSchema vClassSchema = new VertexSchema(tClasses);
        VertexSchema vRoomSchema = new VertexSchema(tRooms);
        EdgeSchema vRegistrationSchema = new EdgeSchema(tRegistrations);

        graphSchema.vertexSchemas.add(vStudentSchema);
        graphSchema.vertexSchemas.add(vClassSchema);
        graphSchema.edgeSchemas.add(vRegistrationSchema);

        // TODO: test edge representing multiple joins (ex: student to room)
        //       after rewriting EdgeSchema
    }


    /**
     * A series of tests
     */
    public void runTests() {

        try {
            testGetAllVertices();
            testGetVerticesByAttribute();
            testGetVertex();
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
        }
    }

    public void testGetVertex() throws Exception {
        // not yet implemented
    }

    public void testGetVerticesByAttribute() throws Exception {
        // not yet implemented
    }

    public void testGetAllVertices() throws Exception {
        System.out.println("\nTEST: Get all vertices\n======================");
        List<Vertex> vertices = new ArrayList<Vertex>();
        for (VertexSchema schema: graphSchema.vertexSchemas) {
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
    public void start() throws SQLException {
        loadDatabase();
        loadSchemas();
        runTests();
    }

    public static void main(String[] args) throws SQLException {
        TestGraphDefinition driver = new TestGraphDefinition();
        driver.start();
    }
}
