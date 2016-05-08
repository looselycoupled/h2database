/*
 * Default vertex schema/definiton for graph construction
 */

package org.h2.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import org.h2.graph.EdgeSchema;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.engine.Session;
import org.h2.result.Row;
import org.h2.index.Cursor;
import org.h2.value.DataType;
import org.h2.value.Value;

import java.util.UUID;
import java.util.Set;


/**
 * Default vertex schema/definiton for graph construction
 */
public class VertexSchema {

    private Session session;
    private String label; // the "type" of the vertex
    public Table sourceTable;
    public Map<String, Integer> attributeMapping;
    // should have a list of outgoing edge schemas
    // and a list of incoming edge schemas
    public EdgeSchema edgeSchema;
    private ArrayList<Vertex> vertices;

    /**
     * Constructor accepts a reference to the underlying table
     * TODO - accept specific columns
     */
    public VertexSchema(Session session, Table sourceTable, String label) {
        this.session = session;
        this.sourceTable = sourceTable;
        this.label = label;
        // need a mapping from property name to index
        attributeMapping = new HashMap<String, Integer>();
        Integer counter = 0;
        for (Column c: sourceTable.getColumns()){
            attributeMapping.put(c.getName(), counter);
            counter += 1;
        }
    }

    public String getLabel() {
        return label;
    }

    public Set<String> getPropertyKeys() {
        return attributeMapping.keySet();
    }

    public void addEdgeSchema(EdgeSchema es){
        edgeSchema = es;
    }

    /**
     * Returns a list of all vertices in the underlying relation
     */
    public List<Vertex> findAll() {
        if (vertices == null){
            vertices = new ArrayList<Vertex>();
            Cursor cursor = sourceTable.getScanIndex(session).find(session, null, null);
            while (cursor.next()) {
                vertices.add(new Vertex(UUID.randomUUID(), cursor.get(), this));
            }
        }
        return vertices;
    }

    /**
     * Returns a list of all vertices in the underlying relation that match
     * the given column name and value
     */
    // public List<Vertex> findByAttribute(String name, Object value) {
    //     List<Vertex> result = new ArrayList<Vertex>();
    //     Row row;
    //     Cursor c = sourceTable.getScanIndex(session).find(session, null, null);
    //     int columnPosition = getColumnPosition(sourceTable, name);

    //     int type = DataType.getTypeFromClass(value.getClass());
    //     Value v = DataType.convertToValue(session, value, type);

    //     while (c.next()) {
    //         row = c.get();
    //         if (row.getValue(columnPosition).equals(v)) {
    //             result.add(new Vertex(row, sourceTable.getColumns()));
    //         }
    //     }

    //     return result;
    // }

    // public int getColumnPosition(Table table, String name) {
    //     return getColumnPosition(table, table.getColumn(name.toUpperCase()));
    // }

    // public int getColumnPosition(Table table, Column column) {
    //     int counter = 0;
    //     for (Column c: table.getColumns()) {
    //         if (c == column) {
    //             return counter;
    //         }
    //         counter += 1;
    //     }
    //     return -1;
    // }

}
