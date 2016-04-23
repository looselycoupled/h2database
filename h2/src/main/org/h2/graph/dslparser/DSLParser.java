package org.h2.graph.dslparser;

import org.h2.engine.Engine;
import org.h2.engine.Database;
import org.h2.engine.Session;
import org.h2.index.Cursor;
import org.h2.result.Row;
import org.h2.table.Column;
import org.h2.table.Table;


import java.io.*;
import java.util.*;


public class DSLParser {

	private ArrayList<String> queries = new ArrayList<String>();
	private Database db;
    private Session dbSession;


    //initilializes db and session
	public DSLParser(Database d, Session s){
		db = d;
		dbSession = s; 
	}

	// stores the DSL queries in queries
	public void loadDSL(String filename) throws IOException {

		try
		{
			BufferedReader bf = new BufferedReader(new FileReader(filename));
			String line = bf.readLine();
			while (line != null)
			{
				if(! "".equals(line.trim())){
					queries.add(line);
				}
				line = bf.readLine();
			}
			bf.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
	  }
	}

	public ArrayList<String> intersection( List<String> list1, List<String> list2) {    
	    
	    ArrayList<String> result = new ArrayList<String>(list1);
	    result.retainAll(list2);
	    return result;
	}

	public ArrayList<String> returnatts(String sTable) throws Exception {
		
		ArrayList<String> atts = new ArrayList<String>();

		try{
			Table t = db.getTableOrViewByName(sTable).get(0);
			Column[] columns = t.getColumns();
		    if (columns.length > 0) {
		      for (Column c: columns) {
		        atts.add(c.toString());
		      }
		    }

		}catch(Exception e){
			System.out.println("Invalid Table Name"+e.toString());
		}
	
	    return atts;
	}

	public String getJoinColumn(String table1, String table2) throws Exception {

		ArrayList<String> attlist1 = returnatts(table1);
		ArrayList<String> attlist2 = returnatts(table2);
		ArrayList<String> joincols = intersection(attlist1,attlist2);

		if(joincols.size() == 0){
			return "";
		}else{
			return joincols.get(0);
		}

	}


	public ArrayList<String> parseNodeQueries(){

		ArrayList<String> vinfo = new ArrayList<String>();

		for(String query:queries){	
			if(query.startsWith("Nodes")){

				String[] parts = query.split(":-");
				String head = parts[0].trim();
				String tail = parts[1].trim();

				// Extracting label for output reln
				String val = head.split("Nodes")[1].trim();
				parts = val.split("\\(");
				String nodelabel = parts[1].split("\\)")[0].trim();

				//Extracting sourceTable for Nodes
				parts = tail.split("\\(");
				String sTable = parts[0].trim();

				vinfo.add(sTable+"+"+nodelabel);
			}
		}
		return vinfo;
	}

	public ArrayList<String> parseEdgeQueries() throws Exception {

		ArrayList<String> einfo = new ArrayList<String>();

		for(String query:queries){	
			if(query.startsWith("Edges")){

				String[] parts = query.split(":-");
				String head = parts[0].trim();
				String tail = parts[1].trim();

				// Extracting label for output reln
				String val = head.split("Edges")[1].trim();
				parts = val.split("\\(");
				String edgelabel = parts[1].split("\\)")[0].trim();

				//Extracting sourceTables for Edges
				
				String[] tables = tail.split(",");
				
				String outquery = edgelabel+":";
				
				for(int i=0;i<tables.length;i++){
					for(int j=i+1;j<tables.length;j++){

						String table1 = tables[i].trim();
						String table2 = tables[j].trim();
						
						String joinCol = getJoinColumn(table1,table2);

						if(! "".equals(joinCol)){
							outquery += (table1+"+"+joinCol+"+"+table2+",");
						}
						
					}
				}

				einfo.add(outquery);
			}
		}
		
		return einfo;
	}


	//Assumes only 1 graph query per DSL file

	public String parseGraphQueries() throws Exception {

		String res = "";

		for(String query:queries){	
			if(query.startsWith("Graph")){

				String[] parts = query.split(":-");
				String head = parts[0].trim();
				String tail = parts[1].trim();

				String graph_name = head.split("Graph")[1].split("\\(")[1].split("\\)")[0].trim();
					
				String node_att = tail.split("Nodes")[1].split("\\)")[0].split("\\(")[1].trim();
				node_att = node_att.replace(',','+');

				String edge_att = tail.split("Edges")[1].split("\\)")[0].split("\\(")[1].trim();
				edge_att = edge_att.replace(',','+');

				res = res + graph_name+":"+node_att+":"+edge_att;

			}
		}
		return res;
	}


}
