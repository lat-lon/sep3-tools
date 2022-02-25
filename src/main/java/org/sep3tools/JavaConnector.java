package org.sep3tools;

import java.sql.*;

public class JavaConnector {

	private static String m_url = "jdbc:postgresql://localhost/petroparser";

	private static String user = "petroparser";

	private static String pass = "PetroParser";

	private static String wb = "woerterbuch.\"Woerterbuch\"";

	private static String st = "woerterbuch.\"Schluesseltypen\"";

	public static void setWb(String wb) {
		JavaConnector.wb = wb;
	}

	public static void setSt(String st) {
		JavaConnector.st = st;
	}

	public static void setUser(String user) {
		JavaConnector.user = user;
	}

	public static void setPass(String pass) {
		JavaConnector.pass = pass;
	}

	public static void setUrl(String url) {
		JavaConnector.m_url = url;
	}

	public static String getS3Name(String sep3Code) throws SQLException {
		Connection conn = DriverManager.getConnection(m_url, user, pass);
		// String query = "SELECT Klartext from woerterbuch.Woerterbuch; where Kuerzel=";
		String query = "select \"Kuerzel\", \"Klartext\" from " + wb + "w join " + st + " s "
				+ "on w.\"Typ\" = s.\"Nebentypbez\" "
				+ "where (s.\"Datenfeld\" = 'PETRO' OR s.\"Datenfeld\" = 'diverse') AND \"Kuerzel\"=";

		PreparedStatement stmt = conn.prepareStatement(query + "'" + sep3Code + "'");
		ResultSet rs = stmt.executeQuery();
		boolean validRS = rs.next();
		String ret = "";
		if (validRS)
			ret = rs.getString(2);

		stmt.close();
		conn.close();

		return (ret);

	}

}
