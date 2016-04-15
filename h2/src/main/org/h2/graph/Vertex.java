/*
 * CMSC724 Graph API package
 */
package org.h2.graph;

import java.util.Iterator;
import org.h2.result.Row;
import org.h2.value.Value;
import java.util.HashMap;


/**
 * Default vertex implementation.  It basically wraps a Row object.
 */
public class Vertex {
    private Row row;

    public Vertex(Row row) {
        this.row = row;
    }

    public Value[] getValues() {
        return row.getValueList();
    }

    public HashMap<String, Value> getAttributes() throws Exception {
        throw new Exception("Not Implemented");
    }

    public String toString() {
        return "<Vertex> ID: " + row.getKey();
    }

    public String toJSON() throws Exception {
        throw new Exception("Not Implemented");
    }


}
