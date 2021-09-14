package org.sep3tools;

import java.sql.*;

public class PLJavaConnector {

	private static String m_url = "jdbc:default:connection";

	public static String getS3Name(String sep3Code) throws SQLException {
		Connection conn = DriverManager.getConnection(m_url);
		String query = "SELECT sep3_term from bml.bml_schluesselmapping where bml_codelist='RockNameList' and sep3_code=";

		PreparedStatement stmt = conn.prepareStatement(query + "'" + sep3Code + "'");
		ResultSet rs = stmt.executeQuery();
		rs.next();
		String ret;
		ret = rs.getString("sep3_term");

		stmt.close();
		conn.close();

		return (ret);

	}

}
