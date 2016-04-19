/*
 * Default vertex schema/definiton for graph construction
 */

package org.h2.graph;

import java.util.HashMap;
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


/**
 * Default vertex schema/definiton for graph construction
 */
public class VertexSchema {

    private Session session;
    private String label;
    public Table sourceTable;

    public HashMap<String, EdgeSchema> incomingEdges = new HashMap<String, EdgeSchema>();
    public HashMap<String, EdgeSchema> outgoingEdges = new HashMap<String, EdgeSchema>();

    /**
     * Constructor accepts a reference to the underlying table
     */
    public VertexSchema(Session session, Table sourceTable, String label) {
        this.session = session;
        this.sourceTable = sourceTable;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Returns a list of all vertices in the underlying relation
     */
    public List<Vertex> findAll() {
        List<Vertex> vertices = new ArrayList<Vertex>();
        Cursor cursor = sourceTable.getScanIndex(session).find(session, null, null);
        while (cursor.next()) {
            vertices.add(new Vertex(cursor.get(), sourceTable.getColumns()));
        }
        return vertices;
    }

    /**
     * Returns a list of all vertices in the underlying relation that match
     * the given column name and value
     */
    public List<Vertex> findByAttribute(String name, Object value) {
        List<Vertex> result = new ArrayList<Vertex>();
        Row row;
        Cursor c = sourceTable.getScanIndex(session).find(session, null, null);
        int columnPosition = getColumnPosition(sourceTable, name);

        int type = DataType.getTypeFromClass(value.getClass());
        Value v = DataType.convertToValue(session, value, type);

        while (c.next()) {
            row = c.get();
            if (row.getValue(columnPosition).equals(v)) {
                result.add(new Vertex(row, sourceTable.getColumns()));
            }
        }

        return result;
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

}
