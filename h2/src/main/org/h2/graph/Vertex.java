/*
 * CMSC724 Graph API package
 */
package org.h2.graph;

import java.util.HashMap;
import java.util.Iterator;
import org.h2.result.Row;
import org.h2.table.Column;
import org.h2.value.Value;

/**
 * Default vertex implementation.  It basically wraps a Row object.
 */
public class Vertex {
    private Row row;
    private Column[] columns;
    private HashMap<String, Value> attributesMap = new HashMap<String, Value>();

    public Vertex(Row row, Column[] columns) {
        this.row = row;
        this.columns = columns;

        int counter = 0;
        for (Column c: columns) {
            attributesMap.put(c.getName(), row.getValue(counter));
            counter = counter + 1;
        }

    }

    public Value[] getValues() {
        return row.getValueList();
    }

    public HashMap<String, Value> getAttributes() {
        return attributesMap;
    }

    public String toString() {
        return "<Vertex> ID: " + row.getKey();
    }

    public String toJSON() throws Exception {
        throw new Exception("Not Implemented");
    }


}
