package com.agentaid.a.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PeopleDAO {

	public static void main(String[] args) throws Exception {
		
		URL url = new URL("https://www.googleapis.com/plus/v1/people/me?key=AIzaSyChDeZxN9y_nnI0szcbQ2ln_4hat4o7YZk");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String line = reader.readLine();
    while (line!=null) {
    	System.out.println(line);
    	line = reader.readLine();
    }
	}
  
}
