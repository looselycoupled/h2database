/*
 * Default edge schema/definiton for graph construction
 */

package org.h2.graph;

import java.util.List;
import java.util.ArrayList;

import org.h2.engine.Session;
import org.h2.table.Table;
import org.h2.table.Column;
import org.h2.result.Row;
import org.h2.index.Cursor;

import com.tinkerpop.blueprints.Direction;



/**
 * Default edge schema/definiton for graph construction.  An edge can span
 * multiple joins such as (from the school dataset):
 *
 *   Student -> Registration -> Class -> Room
 *
 */
public class EdgeSchema {

    private Session session;
    private String label;
    private Boolean directed;
    private VertexSchema srcVertexSchema;
    private VertexSchema dstVertexSchema;
    private List<JoinSchema> joins = new ArrayList<JoinSchema>();

    /**
     * Utility class to define a join relationship
     */
    private class JoinSchema {
        public Table sourceTable;
        public Column sourceColumn;
        public Table targetTable;
        public Column targetColumn;
        public List<String> targetTableAttributes;

        public JoinSchema(Table sourceTable, Column sourceColumn, Table targetTable, Column targetColumn, List<String> targetTableAttributes) {
            this.sourceTable = sourceTable;
            this.sourceColumn = sourceColumn;
            this.targetTable = targetTable;
            this.targetColumn = targetColumn;
            this.targetTableAttributes = targetTableAttributes;
        }
    }

    /**
     * Constructor accepts a reference to the underlying table and whether
     * the edge should be considered directed
     */
    public EdgeSchema(Session session, Boolean directed, String label) {
        this.session = session;
        this.directed = directed;
        this.label = label;
    }

    /**
     * Constructor accepts a reference to the underlying table and defaults
     * to the edge not being directed
     */
    public EdgeSchema(Session session, String label) {
        this.session = session;
        this.directed = false;
        this.label = label;
    }

    public void addVertexSchemas(VertexSchema srcVertexSchema, VertexSchema dstVertexSchema) {
        this.srcVertexSchema = srcVertexSchema;
        this.dstVertexSchema = dstVertexSchema;
    }

    /**
     * Returns the last table in the current set of joins
     */
    private Table getLastTable() {
        return joins.get(joins.size() - 1).targetTable;
    }

    /**
     * Public method to add consecutive joins that as a whole will define the
     * edge relationship.
     */
    public void addJoin(Table sourceTable, Column sourceColumn, Table targetTable,
        Column targetColumn, List<String> targetTableAttributes) {

        // make sure this join starts with the same table we currently end with
        if (joins.size() > 0) {
            if (sourceTable.getName() != getLastTable().getName()) {
                System.out.println("Error:SourceTable does not match last table joined.");
                System.exit(1);
            }
        }
        // add new joinschema object
        JoinSchema j = new JoinSchema(sourceTable, sourceColumn, targetTable, targetColumn, targetTableAttributes);
        joins.add(j);
    }

    public List<Edge> connectVertex(Vertex srcV){
        List<Edge> edges = new ArrayList<Edge>();
        List<Vertex> vertices = dstVertexSchema.findAll();
        List<ArrayList<Row>> prevRowsList;
        List<ArrayList<Row>> currRowsList = new ArrayList<ArrayList<Row>>();
        ArrayList<Row> srcRows = new ArrayList<Row>();
        srcRows.add(srcV.row);
        currRowsList.add(srcRows);
        int counter = 0;// let's us figure out when we should join with a vertex
        for (JoinSchema j: joins) {
            // candidate edges
            //   - a candidate edge is a list of rows that so far extend over the join
            //   - ex. initially the only candidate edge is the row r1 of the source vertex
            //   - ex. then, we join r1 with two rows r2, r3 from table t2
            //   - now, we have two candidate edges [r1,r2] and [r1,r3]
            // prevRowsList is a list of the candidate edges found for the previous join schema
            prevRowsList = currRowsList;
            // currRowsList will be the list of the candidate edges found for this join schema
            currRowsList = new ArrayList<ArrayList<Row>>();
            // loop through all the "candidate" edges
            for (List<Row> rows: prevRowsList) {
                // get the value that we are trying to join
                // TODO - assumes the join value is an int
                int src_value = rows.get(rows.size()-1).getValue(getColumnPosition(j.sourceTable, j.sourceColumn)).getInt();
                // if last join, loop through the dstVertices
                if (counter == (joins.size()-1)){
                    for (Vertex dstV: vertices) {
                        // TODO - assumes the join value is an int
                        int dst_value = dstV.getProperty(j.targetColumn.getName()).getInt();
                        if (dst_value == src_value){
                            // check to make sure this is not a self-referential edge
                            if (srcV.getId() != dstV.getId()) {
                                Edge edge = new Edge(srcV, dstV, rows, this);
                                edges.add(edge);
                                srcV.addEdge(edge, Direction.OUT);
                                dstV.addEdge(edge, Direction.IN);
                            }
                        }
                    }
                } else { // otherwise, scan the target table - TODO use index
                    Cursor c = j.targetTable.getScanIndex(session).find(session, null, null);
                    while (c.next()) {
                        Row cursorRow = c.get();
                        // TODO - assumes the join value is an int
                        int dst_value = cursorRow.getValue(getColumnPosition(j.targetTable, j.targetColumn)).getInt();
                        if (src_value == dst_value) {
                            // we found our join row - found a candidate edge for next round
                            ArrayList<Row> extRows = new ArrayList<Row>();
                            extRows.addAll(rows);
                            extRows.add(cursorRow);
                            currRowsList.add(extRows);
                        }
                    }
                }
            }
            counter += 1;
        }
        return edges;
    }

    // Given two lists of vertices, find all the edges between them
    public List<Edge> connectVertices(List<Vertex> srcVertices){
        List<Edge> edges = new ArrayList<Edge>();
        // find ALL possible edges for each vertex
        for (Vertex srcV: srcVertices){
            edges.addAll(connectVertex(srcV));
        }
        return edges;
    }


    /**
     * Public method to find the matching vertex at the end of an edge relationship.
     *
     * NOTE THAT THIS ASSUMES THE VERTEX HAS ONLY ONE EDGE OF THIS TYPE.
     */
    public Vertex getTargetVertex(Vertex sourceVertex) {
        return null;
        // Boolean init = true;
        // int targetId = sourceVertex.getPropertyKeys().get("ID").getInt();
        // Row row = null;

        // // loop through joined tables
        // for (JoinSchema j: joins) {
        //     if (init) {
        //         init = false;
        //     } else {
        //         targetId = row.getValue(getColumnPosition(j.sourceTable, j.sourceColumn)).getInt();
        //     }

        //     // loop through rows of joined tables looking for our target row
        //     Cursor c = j.targetTable.getScanIndex(session).find(session, null, null);
        //     while (c.next()) {
        //         Row cursorRow = c.get();
        //         if (cursorRow.getValue(getColumnPosition(j.targetTable, j.targetColumn)).getInt() == targetId) {
        //             // we found our join row
        //             row = cursorRow;
        //             break;
        //         }
        //     }
        // }

        // Table t = joins.get(joins.size() - 1).targetTable;
        // Vertex endVertex = new Vertex(row, t.getColumns());
        // return endVertex;
    }

    public int getColumnPosition(Table table, String name) {
        return getColumnPosition(table, table.getColumn(name.toUpperCase()));
    }

    public int getColumnPosition(Table table, Column column) {
        int counter = 0;
        for (Column c: table.getColumns()) {
            if (c == column) {
                return counter;
            }
            counter += 1;
        }
        return -1;
    }

    public Boolean isDirected() {
        return directed;
    }

    public String getLabel() {
        return label;
    }




}
