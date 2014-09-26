/*
 * This code identifies resources that are stuck in processing in the DISCO workflow. A stuck resource 
 * is defined as a resource with a processing status in DISCO for more than 2 days in comparison to the 
 * current date. The list of stuck resources is emailed to the nif-curators mailing list.  
 */

package com.neuinfo.resourceworkflow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

	private static HashMap<String, List<String>> resources;
	static ArrayList<String> resourcesToReview = new ArrayList<String>();
	
	public static void main(String[] args) {
		// Query DISCO user database
		try {
			resources = QueryDisco.queryDiscoUserDb();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Get list of "stuck" resources by iterating through db query results 
		System.out.println("\n<-- Fetching Keys and corresponding Multiple Values -->");
		for (Map.Entry<String, List<String>> entry : resources.entrySet()) {
			String key = entry.getKey();
			List<String> values = entry.getValue();
			System.out.print("KEY: " + key);
			System.out.println(" VALUES: " + values+"\nSTATUS-DATE: "+values.get(1));

			// Identify stuck resources based on status_date
			String statusDate = values.get(1);
			boolean resourceIsStuck = ResourceDateValidator.checkDate(statusDate);
			System.out.println("RESOURCE STATUS: "+key+"\t"+statusDate+"\t"+resourceIsStuck+"\n");
			
			//If resourceIsStuck = true, add to list
			if (resourceIsStuck) {
				String discoUrlPrefix = "http://disco.neuinfo.org/webportal/dataPipelineViewStatus.do?id=";
					resourcesToReview.add(discoUrlPrefix+key);
			}
		}
		
		//Email list of stuck resources to curators
		System.out.println("Stuck resources to email: "+resourcesToReview);
		try {
			SendEmail.emailCurators(resourcesToReview);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
