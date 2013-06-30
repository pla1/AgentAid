package com.agentaid.a.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class GoogleApiClientDAO {
	private static final DatabaseUtils databaseUtils = new DatabaseUtils();
	private static final Logger logger = Logger.getAnonymousLogger();

	public static void main(String[] args) {
		GoogleApiClientDAO googleApiClientDAO = new GoogleApiClientDAO();
		System.out.println(googleApiClientDAO.get().toString());
	}

	public GoogleApiClient get() {
		GoogleApiClient googleApiClient = new GoogleApiClient();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = databaseUtils.getConnection();
			ps = connection.prepareStatement("select * from googleapiclient");
			rs = ps.executeQuery();
			if (rs.next()) {
				googleApiClient.setId(rs.getString("id"));
				googleApiClient.setSecret(rs.getString("secret"));
			} else {
				logger.warning("Google API client id and secret are missing. Add a record to table googleapiclient");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			databaseUtils.close(rs, ps, connection);
		}
		return googleApiClient;
	}

	public int insert(GoogleApiClient client) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int quantity = 0;
		try {
			connection = databaseUtils.getConnection();
			ps = connection.prepareStatement("insert into googleapiclient (id, secret) values(?,?)");
			int i = 1;
			ps.setString(i++, client.getId());
			ps.setString(i++, client.getSecret());
			quantity = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			databaseUtils.close(rs, ps, connection);
		}
		return quantity;
	}

}
