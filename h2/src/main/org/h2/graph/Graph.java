/*
 * CMSC724 Graph API package
 */
package org.h2.graph;

import org.h2.engine.Engine;
import org.h2.engine.Database;
import org.h2.engine.Session;
import org.h2.graph.dslparser.*;

import org.h2.index.Cursor;
import org.h2.result.Row;
import org.h2.table.Column;
import org.h2.table.Table;
import com.tinkerpop.blueprints.Direction;


import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.*;


public class Graph {

    private List<Vertex> vertices;
    private List<Edge> edges;

    public Graph() {
        vertices = new ArrayList<Vertex>();
    }

    public Graph(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public List<Vertex> getVertices() {
        //  Return an iterable to all the vertices in the graph.
        return vertices;
    }

    public Vertex addVertex(Object id) {
        return vertices.get(0);
    }

    public Vertex addVertex(Vertex vertex) {
        vertices.add(vertex);
        return vertex;
    }

    public void addVertices(List<Vertex> v){
        vertices.addAll(v);
    }

    public Edge getEdge(Object id) {
        // Return the edge referenced by the provided object identifier.
        return null;
    }

    public List<Edge> getEdges() {
        //  Return an iterable to all the edges in the graph.
        return edges;
    }

    public Iterable<Edge> getEdges(String key, Object value) {
        //  Return an iterable to all the edges in the graph that have a particular key/value property.
        return null;
    }

    public Vertex getVertex(Object id) {
        //  Return the vertex referenced by the provided object identifier.
        return null;
    }

    public Iterable<com.tinkerpop.blueprints.Vertex> getVertices(String key, Object value) {
        //  Return an iterable to all the vertices in the graph that have a particular key/value property.
        return null;
    }



    /* The following are suggested stubs for registering the graph definition */
    public void register(DSLParser p, String dbName) throws Exception{
        // public method to register a graph definition

        Database db = Engine.getInstance().getDatabases().get(dbName);
        Session dbSession = db.getSystemSession();


       //  //creating vertex schema
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
            String edgelabel = s.split(":")[0].trim();
            EdgeSchema eSchema = new EdgeSchema(dbSession, edgelabel);
            String[] joinqueries = s.split(":")[1].trim().split("!")[0].trim().split(",");
            String attrs = "";

            if(s.split(":")[1].trim().split("!").length>1){
                attrs = s.split(":")[1].trim().split("!")[1].trim();
            }


            //Retrieved as tablename, attribute
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            ArrayList<Table> tables = db.getAllTablesAndViews(false);
            for (Table t: tables){
                map.put(t.getName(), new ArrayList<String>());
            }

            if(attrs.split(",").length>1){
                for(String val:attrs.split(",")){
                    String table = val.split("\\+")[0].trim();
                    String att = val.split("\\+")[1].trim();
                    map.get(table).add(att);
                }
            }

            for(String q:joinqueries){
                String[] parts = q.split("\\+");
                String joincol = parts[1].trim();
                String tablename1 = parts[0].trim();
                String tablename2 = parts[2].trim();

                Table t1 = db.getTableOrViewByName(tablename1).get(0);
                Table t2 = db.getTableOrViewByName(tablename2).get(0);

                eSchema.addJoin(
                    t1, t1.getColumn(joincol),
                    t2, t2.getColumn(joincol),
                    map.get(t2)
                );
            }
            eSchemaList.add(eSchema);
        }


        // store all these schemas in the graphSchema object

        String graphq = p.parseGraphQueries();

        String[] parts = graphq.split(":");
        String nodepart = parts[1].trim();
        String edgepart = parts[2].trim();

        String glabel = parts[0].trim();
        GraphSchema graphSchema = new GraphSchema(glabel);

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

        return graphSchema;
        
    }

    public void deregister() {
        // public method to deregister a graph definition
    }

    public void shutdown() {
        deregister();
    }



    public Edge addEdge(Object id, Vertex outVertex, Vertex inVertex, String label){
        return null;
    }



    /**
     * Methods required by the interface that we don't want to implement for now
     */

    // public void removeEdge(Edge edge) {}

    // public GraphQuery query() {
    //     return null;
    // }

    // public void removeVertex(Vertex vertex) {}

    // public Features getFeatures(){
    //     return null;
    // }

}
