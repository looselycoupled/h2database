/*
 * CMSC724 Graph API package
 */
package org.h2.graph;

import java.util.Iterator;
import org.h2.result.Row;
import org.h2.value.Value;
import java.util.HashMap;

/**
 * Default edge implementation.  It currently assumes that edges are represented
 * as rows in a join table.
 */
public class Edge {
    private String label;
    private Row row;

    public Edge(Row row, String label) {
        this.row = row;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Vertex getVertex(String direction) {
        if (direction.toUpperCase() == "IN" || direction.toUpperCase() == "OUT" || direction.toUpperCase() == "BOTH") {
            // return the Vertex lazily;
            return null;
        } else {
            throw new IllegalArgumentException("direction must be IN or OUT or BOTH");
        }
    }

    public Value[] getValues() {
        return row.getValueList();
    }

    public HashMap<String, Value> getAttributes() throws Exception {
        throw new Exception("Not Implemented");
    }

    public String toString() {
      return "<Edge> ID: " + row.getKey();
    }

    public String toJSON() throws Exception {
        throw new Exception("Not Implemented");
    }


}
