package com.agentaid.a.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.agentaid.a.client.Comment;

public class CommentDAO {
	private static final DatabaseUtils databaseUtils = new DatabaseUtils();
	private static final Logger logger = Logger.getAnonymousLogger();

	public static void main(String[] args) {
		CommentDAO commentDAO = new CommentDAO();
		commentDAO.insert(new Comment(1, "patrick.archibald@gmail.com", "This is a test comment by PLA."));
		ArrayList<Comment> comments = commentDAO.getAllComments();
		for (Comment comment : comments) {
			System.out.println(comment);
		}
	}

	public ArrayList<Comment> getCommentsByEmailAddress(String emailAddress) {
		ArrayList<Comment> comments = new ArrayList<Comment>();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = databaseUtils.getConnection();
			ps = connection.prepareStatement("select * from comment where emailaddress = ? order by id desc limit 500");
			ps.setString(1, "patrick.archibald@gmail.com");
			rs = ps.executeQuery();
			while (rs.next()) {
				comments.add(transfer(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			databaseUtils.close(rs, ps, connection);
		}
		return comments;
	}

	public ArrayList<Comment> getAllComments() {
		ArrayList<Comment> comments = new ArrayList<Comment>();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = databaseUtils.getConnection();
			ps = connection.prepareStatement("select * from comment order by id desc limit 500");
			rs = ps.executeQuery();
			while (rs.next()) {
				comments.add(transfer(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			databaseUtils.close(rs, ps, connection);
		}
		return comments;
	}

	public int insert(Comment comment) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int quantity = 0;
		try {
			connection = databaseUtils.getConnection();
			ps = connection.prepareStatement("insert into comment (commenttype, commenttext, emailaddress, logtime) "
					+ "values(?,?,?,now())", PreparedStatement.RETURN_GENERATED_KEYS);
			int i = 1;
			ps.setInt(i++, comment.getType());
			ps.setString(i++, comment.getText());
			ps.setString(i++, comment.getEmailAddress());
			quantity = ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				comment.setId(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			databaseUtils.close(rs, ps, connection);
		}
		return quantity;
	}

	private Comment transfer(ResultSet rs) throws SQLException {
		Comment comment = new Comment();
		comment.setId(rs.getInt("id"));
		comment.setEmailAddress(rs.getString("emailaddress"));
		comment.setType(rs.getInt("commenttype"));
		comment.setText(rs.getString("commenttext"));
		comment.setTimeDisplay(rs.getString("logtime"));
		return comment;
	}

}
