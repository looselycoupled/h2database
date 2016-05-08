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


    //initilializes db and session
	public DSLParser(Database d){
		db = d;
	}
	      

	// stores the DSL queries in queries
	public void loadDSL(String filename) throws IOException {

		try
		{
			BufferedReader bf = new BufferedReader(new FileReader(filename));
			String line = bf.readLine();
			while (line != null)
			{
				if(! "".equals(line.trim()) && (line.startsWith("Nodes")||line.startsWith("Edges")||line.startsWith("Graph"))){
					queries.add(line);
				}
				line = bf.readLine();
			}
			bf.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
	  }
	}



	public String returnatts(String sTable, int pos) throws Exception {
		
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
	
	    return atts.get(pos);
	}


	public int getJoinIndex(String[] attlist1, String[] attlist2){

		// find join index wrp table1

		int pos=-1;

		for(int i=0;i<attlist1.length;i++){
			for(int j=0;j<attlist2.length;j++){

				if(attlist1[i].trim().equals(attlist2[j].trim()) && !attlist1[i].trim().equals("_") ){
					pos = i;
					break;
				}
				
			}
			if(pos>-1){
				break;
			}
		}
		
		return pos;

	}

	public String getJoinColumn(String table1, String table2) throws Exception {
		
		String table1name = table1.split("\\(")[0].trim();

		String table2name = table2.split("\\(")[0].trim();

		String[] attlist1 = table1.split("\\(")[1].split("\\)")[0].split(",");

		String[] attlist2 = table2.split("\\(")[1].split("\\)")[0].split(",");
		
		int matchIndex = getJoinIndex(attlist1, attlist2);

		return returnatts(table1name, matchIndex);

	}


	public String getNodeAttrs(String sTable, String proplist, String attlist) throws Exception{


		if(proplist.trim().equals("")){
			return "";
		}

		String[] propvals = proplist.split(",");

		String[] attvals = attlist.split(",");

		int[] posIndex = new int[propvals.length];

		String output = "";

		for(int i=0;i<propvals.length;i++){
			for(int j=0; j<attvals.length;j++){
				if(propvals[i].trim().equals(attvals[j].trim())){
					
					if(i<propvals.length-1){
						output = output + returnatts(sTable, j) + "," ;
					}
					else{
						output = output + returnatts(sTable, j);
					}
					

					break;
				}
			}
		}

		return output;
	}


	public ArrayList<String> parseNodeQueries() throws Exception{

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

				String proplist = "";

				if(nodelabel.split(":").length>1){

					proplist = nodelabel.split(":")[1].trim();
				}

				nodelabel = nodelabel.split(":")[0].trim();

				//Extracting sourceTable for Nodes
				parts = tail.split("\\(");
				
				String sTable = parts[0].trim();

				String attlist = parts[1].split("\\)")[0].trim();

				String res = sTable+"+"+nodelabel;

				//add the node attributes to 
				res = res + "!" + getNodeAttrs(sTable, proplist,attlist);

				vinfo.add(res);
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

				String props = "";

				if(edgelabel.split("-").length>1){
					props = edgelabel.split("-")[1].trim();
				}

				edgelabel = edgelabel.split("-")[0].trim();

				//Extracting sourceTables for Edges
				
				String outquery = edgelabel+":";

				tail = tail.replaceAll("\\)\\s*,","):");

				String[] tables = tail.split(":");
					
				for(int i=0;i<tables.length-1;i++){
					
					String table1 = tables[i].trim();
					String table2 = tables[i+1].trim();
					
					String joinCol = getJoinColumn(table1,table2);

					String table1name = table1.split("\\(")[0].trim();

					String table2name = table2.split("\\(")[0].trim();

					outquery += (table1name+"+"+joinCol+"+"+table2name+",");	

				}

				//add the edge attributes 

				if(props.trim().equals("")){
					
					outquery = outquery + "!" ;
				
				}else{
					
					//Add it as Table1+attr,Table2+attr

					outquery = outquery + "!" ;

					//returnatts(sTable,pos)

					for(String att:props.split(",")){

						//for each att pick the Table and the actual attribute

						//Store it as Table1+att

						String attid = att.trim();

						for(String t: tables){

							String table1name = t.split("\\(")[0].trim();

							String[] tattlist = t.split("\\(")[1].split("\\)")[0].split(",");

							int pos = 0;
		
							int k = 0;

							for(String tatt: tattlist){

								if(tatt.trim().equals(att.trim())){

									outquery += (table1name + "+"+returnatts(table1name,k)+",");

									pos = 1;

									break;

								}

								k++;

							}

							if(pos == 1){
								break;
							}

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
