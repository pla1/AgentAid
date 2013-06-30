package com.agentaid.a.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import com.agentaid.a.client.CachedGeoResult;
import com.agentaid.a.geo.Address_components;
import com.agentaid.a.geo.GeoResult;
import com.agentaid.a.geo.Geometry;
import com.agentaid.a.geo.Location;
import com.agentaid.a.geo.Results;
import com.google.gson.Gson;

public class CachedGeoResultDAO {
	private static final Gson GSON = new Gson();
	private static final DatabaseUtils databaseUtils = new DatabaseUtils();
	private static final Logger logger = Logger.getAnonymousLogger();

	public static void main(String[] args) throws Exception {
		CachedGeoResultDAO dao = new CachedGeoResultDAO();
		CachedGeoResult result = dao.get("29461");
		System.out.println(result.toString());
	}

	public int add(CachedGeoResult geo) {
		logger.info(geo.toString());
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = databaseUtils.getConnection();
			ps = connection
					.prepareStatement("insert into geocache (query, latitude, longitude, formatted_address, city, state, zipcode) values(?,?,?,?,?,?,?)");
			int i = 1;
			ps.setString(i++, geo.getQuery());
			ps.setDouble(i++, geo.getLatitude());
			ps.setDouble(i++, geo.getLongitude());
			ps.setString(i++, geo.getFormattedAddress());
			ps.setString(i++, geo.getCity());
			ps.setString(i++, geo.getState());
			ps.setString(i++, geo.getZipcode());
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			databaseUtils.close(ps, connection);
		}
		return 0;
	}

	public CachedGeoResult get(String query) {
		logger.info(query);
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = databaseUtils.getConnection();
			ps = connection.prepareStatement("select * from geocache where query = ?");
			ps.setString(1, query);
			rs = ps.executeQuery();
			if (rs.next()) {
				CachedGeoResult geo = new CachedGeoResult();
				geo.setQuery(query);
				geo.setLatitude(rs.getDouble("latitude"));
				geo.setLongitude(rs.getDouble("longitude"));
				geo.setFormattedAddress(rs.getString("formatted_address"));
				geo.setCity(rs.getString("city"));
				geo.setState(rs.getString("state"));
				geo.setZipcode(rs.getString("zipcode"));
				logger.info("Got geo from database: " + geo.toString());
				return geo;
			} else {
				return getFromWeb(query);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return new CachedGeoResult();
		} finally {
			databaseUtils.close(rs, ps, connection);
		}
	}

	private CachedGeoResult getFromWeb(String query) {
		logger.info(query);
		CachedGeoResult cachedGeoResult = new CachedGeoResult();
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("http://maps.googleapis.com/maps/api/geocode/json?components=postal_code:");
			sb.append(query);
			sb.append("&sensor=false");
			URL url = new URL(sb.toString());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			GeoResult geoResult = GSON.fromJson(reader, com.agentaid.a.geo.GeoResult.class);
			if (geoResult != null) {
				List<Results> resultsList = geoResult.getResults();
				for (Results results : resultsList) {
					cachedGeoResult.setFormattedAddress(results.getFormatted_address());
					Geometry geometry = results.getGeometry();
					List<Address_components> addressComponents = results.getAddress_components();
					for (Address_components addressComponent : addressComponents) {
						String[] types = addressComponent.getTypes();
						if (types != null) {
							for (String type : types) {
								if (type.equals("postal_code")) {
									cachedGeoResult.setZipcode(addressComponent.getShort_name());
								}
								if (type.equals("locality")) {
									cachedGeoResult.setCity(addressComponent.getShort_name());
								}
								if (type.equals("administrative_area_level_1")) {
									cachedGeoResult.setState(addressComponent.getShort_name());
								}
							}
						}
					}
					if (geometry != null) {
						Location location = geometry.getLocation();
						if (location != null) {
							cachedGeoResult.setFormattedAddress(results.getFormatted_address());
							cachedGeoResult.setLatitude(location.getLat().doubleValue());
							cachedGeoResult.setLongitude(location.getLng().doubleValue());
							cachedGeoResult.setQuery(query);
							add(cachedGeoResult);
						}
					}
				}
			} else {
				logger.info("GSON returned null");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getLocalizedMessage());
		}
		return cachedGeoResult;
	}
}
