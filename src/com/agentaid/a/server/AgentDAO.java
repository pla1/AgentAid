package com.agentaid.a.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.agentaid.a.client.Agent;

public class AgentDAO {
	private static final DatabaseUtils databaseUtils = new DatabaseUtils();
	private static final Logger logger = Logger.getAnonymousLogger();

	public static void main(String[] args) {
		AgentDAO agentDAO = new AgentDAO();
		if (true) {
			Agent agent = agentDAO.getById(1);
			System.out.println(agent.toString());
			System.exit(0);
		}
		Agent agent = new Agent();
		agent.setCity("Moncks Corner");
		agent.setEmailAddress("patrick.archibald@gmail.com");
		agent.setLatitude(10.12345678);
		agent.setLongitude(11.0987651);
		agent.setName("Patrick L Archibald");
		agent.setState("SC");
		agent.setUserName("pla1");
		agent.setZipCode("29461");
		agent.setLevel(8);
		agent.setProfileId("108191112950724576986");
		agent.setImageUrl("https://lh4.googleusercontent.com/-18qwKH9pnFw/AAAAAAAAAAI/AAAAAAABoig/SieXiGuuqN4/photo.jpg?sz=50");
		System.out.println(agentDAO.insert(agent) + " records added");
		agent = agentDAO.getByEmailAddress("patrick.archibald@gmail.com");
		System.out.println(agent);
		System.out.println(agentDAO.update(agent) + " records updated");
	}

	public Agent getByEmailAddress(String emailAddress) {
		logger.info(emailAddress);
		Agent agent = new Agent();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = databaseUtils.getConnection();
			ps = connection.prepareStatement("select * from agent where emailaddress = ?");
			ps.setString(1, emailAddress);
			rs = ps.executeQuery();
			if (rs.next()) {
				agent = transfer(rs);
			} else {
				agent.setEmailAddress(emailAddress);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			databaseUtils.close(rs, ps, connection);
		}
		return agent;
	}

	public ArrayList<Agent> getAll() {
		ArrayList<Agent> agents = new ArrayList<Agent>();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = databaseUtils.getConnection();
			ps = connection.prepareStatement("select * from agent order by state, city, zipcode, username");
			rs = ps.executeQuery();
			while (rs.next()) {
				agents.add(transfer(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			databaseUtils.close(rs, ps, connection);
		}
		return agents;
	}

	public Agent getById(int id) {
		Agent agent = new Agent();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = databaseUtils.getConnection();
			ps = connection.prepareStatement("select * from agent where id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				agent = transfer(rs);
			} else {
				logger.info("Agent record not found for ID: " + id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			databaseUtils.close(rs, ps, connection);
		}
		return agent;
	}

	public int insert(Agent agent) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int quantity = 0;
		try {
			connection = databaseUtils.getConnection();
			ps = connection.prepareStatement("insert into agent "
					+ "(username, emailaddress, name, city, state, zipcode, latitude, longitude, level, profileid, imageurl, role, vetted) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
			int i = 1;
			ps.setString(i++, agent.getUserName());
			ps.setString(i++, agent.getEmailAddress());
			ps.setString(i++, agent.getName());
			ps.setString(i++, agent.getCity());
			ps.setString(i++, agent.getState());
			ps.setString(i++, agent.getZipCode());
			ps.setDouble(i++, agent.getLatitude());
			ps.setDouble(i++, agent.getLongitude());
			ps.setInt(i++, agent.getLevel());
			ps.setString(i++, agent.getProfileId());
			ps.setString(i++, agent.getImageUrl());
			ps.setInt(i++, agent.getRole());
			ps.setString(i++, Utils.isYes(agent.isVetted()));
			quantity = ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				agent.setId(rs.getInt(1));
				if (agent.getId() == 1) {
					agent.setVetted(true);
					agent.setRole(Agent.ROLE_MODERATOR);
					update(agent);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			databaseUtils.close(rs, ps, connection);
		}
		return quantity;
	}

	private Agent transfer(ResultSet rs) throws SQLException {
		Agent agent = new Agent();
		agent.setId(rs.getInt("id"));
		agent.setCity(rs.getString("city"));
		agent.setEmailAddress(rs.getString("emailaddress"));
		agent.setLatitude(rs.getDouble("latitude"));
		agent.setLongitude(rs.getDouble("longitude"));
		agent.setName(rs.getString("name"));
		agent.setState(rs.getString("state"));
		agent.setUserName(rs.getString("username"));
		agent.setZipCode(rs.getString("zipcode"));
		agent.setLevel(rs.getInt("level"));
		agent.setProfileId(rs.getString("profileid"));
		agent.setImageUrl(rs.getString("imageurl"));
		agent.setVetted(Utils.isYes(rs.getString("vetted")));
		agent.setRole(rs.getInt("role"));
		return agent;
	}

	public int update(Agent agent) {
		int quantity = 0;
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = databaseUtils.getConnection();
			ps = connection.prepareStatement("update agent set city = ?, latitude = ?, longitude = ?, name = ?, "
					+ "state = ?, username = ?, zipcode = ?, level = ?, profileid = ?, imageurl = ?, role = ?, vetted =? where id = ?");
			int i = 1;
			ps.setString(i++, agent.getCity());
			ps.setDouble(i++, agent.getLatitude());
			ps.setDouble(i++, agent.getLongitude());
			ps.setString(i++, agent.getName());
			ps.setString(i++, agent.getState());
			ps.setString(i++, agent.getUserName());
			ps.setString(i++, agent.getZipCode());
			ps.setInt(i++, agent.getLevel());
			ps.setString(i++, agent.getProfileId());
			ps.setString(i++, agent.getImageUrl());
			ps.setInt(i++, agent.getRole());
			ps.setString(i++, Utils.isYes(agent.isVetted()));
			ps.setInt(i++, agent.getId());
			quantity = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			databaseUtils.close(ps, connection);
		}
		logger.info("updated " + quantity + " records. Agent: " + agent.toString());
		return quantity;
	}
}
