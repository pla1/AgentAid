package com.agentaid.a.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

public class DatabaseUtils {
	private final static Logger logger = Logger.getAnonymousLogger();
	private final static Properties properties = new Properties();
	private String databaseUser;
	private String databasePassword;
	private String databaseServer;
	private String database;

	public static void main(String[] args) {
		DatabaseUtils databaseUtils = new DatabaseUtils();
		databaseUtils.createTables();
	}

	public Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String connectionString = String.format("jdbc:mysql://%s/%s?user=%s&password=%s", databaseServer, database, databaseUser,
					databasePassword);
			connection = DriverManager.getConnection(connectionString);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getLocalizedMessage());
		}
		return connection;
	}

	public DatabaseUtils() {
		try {
			properties.load(new FileInputStream("/etc/AgentAid.properties"));
			databaseUser = properties.getProperty("databaseUser");
			databasePassword = properties.getProperty("databasePassword");
			databaseServer = properties.getProperty("databaseserver");
			database = properties.getProperty("database");
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning(e.getLocalizedMessage());
		}
	}

	private void createTables() {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement.execute("drop table if exists agent");
			statement.execute("create table agent (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, username varchar(20), "
					+ "emailaddress varchar(132), name varchar(132), city varchar(50), state char(2), "
					+ "zipcode varchar(9),  latitude DECIMAL(18,12), longitude DECIMAL(18,12), level smallint, "
					+ "profileid char(21), imageurl varchar(132), role smallint, vetted char(1)) ");
			statement.execute("create unique index agent1 on agent (emailaddress)");
			statement.execute("create unique index agent2 on agent (profileid)");
			statement.execute("drop table if exists googleapiclient");
			statement.execute("create table googleapiclient (id varchar(132), secret varchar(50))");
			statement.execute("drop table if exists geocache");
			statement.execute("create table geocache "
					+ "(query varchar(132) primary key, latitude DECIMAL(18,12), longitude DECIMAL(18,12), "
					+ "formatted_address varchar(256) ,city varchar(50), state char(2), zipcode char(9) )");
			statement.execute("drop table if exists comment");
			statement
					.execute("create table comment (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, commenttype smallint, commenttext varchar(256), emailaddress varchar(132), logtime timestamp)");
			statement.execute("create index comment1 on comment (emailaddress)");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(statement, connection);
		}
		GoogleApiClientDAO googleApiClientDAO = new GoogleApiClientDAO();
		GoogleApiClient googleApiClient = new GoogleApiClient();
		googleApiClient.setId(properties.getProperty("googleapiclientid"));
		googleApiClient.setSecret(properties.getProperty("googleapisecret"));
		googleApiClientDAO.insert(googleApiClient);
	}

	public void close(Statement statement, Connection connection) {
		close(statement);
		close(connection);
	}

	public void close(PreparedStatement statement, Connection connection) {
		close(statement);
		close(connection);
	}

	public void close(ResultSet rs, PreparedStatement statement, Connection connection) {
		close(rs);
		close(statement);
		close(connection);
	}

	private void close(Connection connection) {
		if (connection == null) {
			return;
		}
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void close(Statement statement) {
		if (statement == null) {
			return;
		}
		try {
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void close(PreparedStatement statement) {
		if (statement == null) {
			return;
		}
		try {
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void close(ResultSet rs) {
		if (rs == null) {
			return;
		}
		try {
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
