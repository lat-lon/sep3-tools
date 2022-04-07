package org.sep3tools;

import java.sql.*;

public class PLJavaConnector {

	private static String m_url = "jdbc:default:connection";

	private static String wb = "woerterbuch.\"Woerterbuch\"";

	private static String st = "woerterbuch.\"Schluesseltypen\"";

	public static void setWb(String wb) {
		PLJavaConnector.wb = wb;
	}

	public static void setSt(String st) {
		PLJavaConnector.st = st;
	}

	public static String getS3Name(String sep3Code) throws SQLException {

		Connection conn = DriverManager.getConnection(m_url);
		// String query = "SELECT Klartext from woerterbuch.Woerterbuch where Kuerzel=";
		String query = "select \"Kuerzel\", \"Klartext\" from " + wb + " w join " + st + " s "
				+ "on w.\"Typ\" = s.\"Nebentypbez\" "
				+ "where (s.\"Datenfeld\" = 'PETRO' OR s.\"Datenfeld\" = 'diverse') AND \"Kuerzel\"=";

		PreparedStatement stmt = conn.prepareStatement(query + "'" + sep3Code + "'");
		try (ResultSet rs = stmt.executeQuery()) {
			boolean validRS = rs.next();
			String result = "";
			if (validRS) {
				result = rs.getString(2);
			}
			return result;
		}
	}

}
