package org.sep3tools;

import java.sql.*;

/**
 * Command line launcher for SEP3 CLI.
 *
 * @author Jeronimo Wanhoff <kontakt@jeronimowanhoff.de>
 */
public class LaunchWithSQL {

	static final String DB_URL = "jdbc:postgresql://localhost/petroparser";
	static final String USER = "petroparser";
	static final String PASS = "PetroParser";
	static final String QUERY = "SELECT sep3_term from bml.bml_schluesselmapping where bml_codelist='RockNameList' and sep3_code=";

	public static void main(String[] args) {
		try {
			Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);

			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(QUERY + "'^bio'");
			resultSet.next();
			while (!resultSet.isLast()) {
				System.out.println(resultSet.getString("sep3_term"));
				resultSet.next();
			}
		}
		catch (SQLException e) {
			System.out.println("Connection failure.");
			e.printStackTrace();
		}
	}

}
