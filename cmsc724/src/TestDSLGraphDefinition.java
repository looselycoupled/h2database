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


import org.h2.graph.*;
import org.h2.graph.dslparser.*;

import java.sql.SQLException;
import load.SchoolLoader;

import java.util.*;


/**
 * Exploratory code to test graph construction classes and
 */
public class TestDSLGraphDefinition {


	private String dbName;
    private Session dbSession;
    private GraphSchema graphSchema;

    /**
     * Creates database and loads up the school dataset
     */
    public void loadDatabase() throws SQLException {
        SchoolLoader loader = new SchoolLoader();
        loader.load();
        dbName = "mem:school";
    }


    public void loadSchemas() throws Exception{

        Database db = Engine.getInstance().getDatabases().get(dbName);
        dbSession = db.getSystemSession();

        DSLParser p = new DSLParser(db,dbSession);
        p.loadDSL("./data/dsl/school.txt");
        
        
        //creating vertex schema
        
        ArrayList<String> vinfo = p.parseNodeQueries();
        ArrayList<VertexSchema> vSchemaList = new ArrayList<VertexSchema>();

      
        for(String s:vinfo){

            String[] parts = s.split("\\+");
            String tablename = parts[0].trim();
            String label = parts[1].trim().split("!")[0];

            /*
            //Node attributes are here use them             
            if(parts[1].trim().split("!").length>1){
                String[] attributes = parts[1].trim().split("!")[1].split(",");
                   for(String att:attributes){
                        System.out.print(att+" , ");
                    }
            }
            */

            //Constructing the node schema 
            
            Table t = db.getTableOrViewByName(tablename).get(0);
            VertexSchema vSchema = new VertexSchema(dbSession, t, label);
            
            vSchemaList.add(vSchema);
            
            
        }

        
        //creating edge schema

        ArrayList<String> einfo = p.parseEdgeQueries();
        ArrayList<EdgeSchema> eSchemaList = new ArrayList<EdgeSchema>();

        
        for(String s:einfo){

            System.out.println(s);

            String edgelabel = s.split(":")[0].trim();
            

            EdgeSchema eSchema = new EdgeSchema(dbSession, edgelabel);

            String[] joinqueries = s.split(":")[1].trim().split("!")[0].trim().split(",");

            String attrs = "";

            if(s.split(":")[1].trim().split("!").length>1){

                attrs = s.split(":")[1].trim().split("!")[1].trim();
            }
    
            for(String q:joinqueries){

                String[] parts = q.split("\\+");

                String joincol = parts[1].trim();
                String tablename1 = parts[0].trim();
                String tablename2 = parts[2].trim();

                Table t1 = db.getTableOrViewByName(tablename1).get(0);
                Table t2 = db.getTableOrViewByName(tablename2).get(0);

                //System.out.println(tablename1+" "+joincol+" "+tablename2);

                eSchema.addJoin(
                    t1, t1.getColumn(joincol),
                    t2, t2.getColumn(joincol)
                );
            }


            /*
            //Add edge attributes to 

            //Retrieved as tablename, attribute

            if(attrs.split(",").length>1){
                for(String val:attrs.split(",")){
                    String table = val.split("\\+")[0].trim();
                    String att = val.split("\\+")[1].trim();
                    System.out.println(table+ " "+ att);
                }
            }
            */


            eSchemaList.add(eSchema);
            
        }

        
        // store all these schemas in the graphSchema object

        String graphq = p.parseGraphQueries();

        String[] parts = graphq.split(":");
        String nodepart = parts[1].trim();
        String edgepart = parts[2].trim();

        
        String glabel = parts[0].trim();
        graphSchema = new GraphSchema(glabel);


        //adding vertexSchema to graphschema
        for(String s: nodepart.split("\\+")){

            String label = s.trim();
            int index=0, i=0;

            // find the correct vSchema to add to graphSchema

            while(i<vSchemaList.size()){
                if(label.equals(vSchemaList.get(i).getLabel().trim())){
                    index = i;
                    break;
                }else{
                    i++;
                }    
            }

            graphSchema.vertexSchemas.put(label, vSchemaList.get(index));
        }

  
        //adding edgeSchema to graphschema
        for(String s: edgepart.split("\\+")){

            String label = s.trim();
            int index=0, i=0;

            // find the correct vSchema to add to graphSchema
            while(i<eSchemaList.size()){
                if(label.equals(eSchemaList.get(i).getLabel().trim())){
                    index = i;
                    break;
                }else{
                    i++;
                }
            }

            graphSchema.edgeSchemas.put(label, eSchemaList.get(index));
        } 
        

    }


    /**
     * A series of tests
    */
    public void runTests() {

        try {
            testCreateSingleJoinEdge();
            testCreateMultipleJoinEdge();
            //testGetAllVertices();
            // testGetVerticesByAttribute();
            // testEdgeWithMulitpleJoins();
        } catch (Exception e) {
            System.out.println("FAIL: " + e.toString());
        }
    }

    /*
     public void testGetVerticesByAttribute() throws Exception {
         System.out.println("\nTEST: testGetVerticesByAttribute\n======================");
         VertexSchema vsStudent = graphSchema.vertexSchemas.get("student");

         List<Vertex> vertices = vsStudent.findByAttribute("name", "Allen Leis");
         System.out.println(vertices.get(0).getAttributes().toString());

         vertices = vsStudent.findByAttribute("id", 2);
         System.out.println(vertices.get(0).getAttributes().toString());
    }
    */

    /**
     * Tests the use of an edgeschema which traverses multiple joins.  Start
     * with the Allen/student Vertex and get Room vertices that can be Found
     * through the "hadClassInRoom" edge.
     *
     * This is just exploratory code and doesnt necessarily represent the final
     * way of doing things.
     */
    /*
     public void testEdgeWithMulitpleJoins() throws Exception {
         System.out.println("\nTEST: Test Multiple Join\n======================");

         // get the Allen vertex
         VertexSchema vsStudent = graphSchema.vertexSchemas.get("student");
         Vertex allen = vsStudent.findByAttribute("name", "Allen Leis").get(0);

         EdgeSchema esHadClassInRoom = graphSchema.edgeSchemas.get("hadClassInRoom");
         Vertex room = esHadClassInRoom.getTargetVertex(allen);
         System.out.println("Source: " + allen.getAttributes().get("NAME"));
         System.out.println("Target: " + room.getAttributes().toString());
    }
    */

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


    public void start() throws Exception {

    	loadDatabase();
    	loadSchemas();
        runTests();

    }

    public static void main(String[] args) throws Exception {
        TestDSLGraphDefinition driver = new TestDSLGraphDefinition();
        driver.start();
    }
}
