package org.sep3tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * This class connects and communicates with a SEP3 Database to execute the queries and to
 * retrieve translations for SEP3-Codes, quantifiers, etc.
 *
 * @author Jeronimo Wanhoff <kontakt@jeronimowanhoff.de>
 * @author <a href="mailto:friebe@lat-lon.de">Torsten Friebe</a>
 */
public final class JavaConnector {

	private static boolean credChanged = true;

	private static final Logger LOG = Logger.getLogger(JavaConnector.class.getName());

	private static String m_url = "jdbc:default:connection";

	private static String user;

	private static String pass;

	private static String wb;

	private static String st;

	private static String sm;

	private static String df;

	private static Connection conn;

	private JavaConnector() {
	}

	public static void setPropertiesFile(String filename) {
		credChanged = true;
		try {
			File file = new File(filename);
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			setUrl(properties.getProperty("URL"));
			setUser(properties.getProperty("USER"));
			setPass(properties.getProperty("PASSWORD"));
			setWb(properties.getProperty("WOERTERBUCH"));
			setSt(properties.getProperty("SCHLUESSELTYPEN"));
			setSm(properties.getProperty("SCHLUESSELMAPPING"));
			setDf(properties.getProperty("DATEFIELD"));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setWb(String wb) {
		JavaConnector.wb = wb;
	}

	public static void setSt(String st) {
		JavaConnector.st = st;
	}

	public static void setSm(String sm) {
		JavaConnector.sm = sm;
	}

	public static void setDf(String df) {
		JavaConnector.df = df;
	}

	public static void setUser(String newUser) {
		credChanged = true;
		JavaConnector.user = newUser;
	}

	public static void setPass(String newPass) {
		credChanged = true;
		JavaConnector.pass = newPass;
	}

	public static void setUrl(String newUrl) {
		credChanged = true;
		JavaConnector.m_url = newUrl;
	}

	// String query = "SELECT Klartext from woerterbuch.Woerterbuch where Kuerzel=";

	private static void setConn(String url, String user, String pass) throws SQLException {
		if (conn != null && !conn.isClosed())
			conn.close();
		JavaConnector.conn = DriverManager.getConnection(url, user, pass);
	}

	/**
	 * translates a SEP3 code to clear text
	 * @param sep3Code code for translation
	 * @return translated SEP3 string
	 * @throws SQLException if DB error occurs
	 */
	public static String getS3Name(String sep3Code) throws SQLException {
		String query = "select kuerzel, klartext from " + wb + " w join " + st + " s "
				+ "on w.typ = s.nebentypbez where (s.datenfeld = '" + df + "' "
				+ "OR s.datenfeld = 'diverse') AND kuerzel= ?";

		if (credChanged)
			setConn(m_url, user, pass);
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, sep3Code);
		LOG.fine("Executing statement: " + stmt);
		try (ResultSet rs = stmt.executeQuery()) {
			boolean validRS = rs.next();
			String result = "";
			if (validRS) {
				result = rs.getString(2);
			}
			LOG.fine("Returning: " + result);

			rs.close();
			stmt.close();

			return result;
		}
	}

	/**
	 * translates a SEP3 code to BML litho code
	 * @param sep3Code code for translation
	 * @return BML litho string
	 * @throws SQLException if DB error occurs
	 */
	public static String getS3AsBMmlLitho(String sep3Code) throws SQLException {
		String query = "select bml_code from " + sm + " where sep3_codelist = 'S3PETRO' AND sep3_code = ?";

		if (credChanged)
			setConn(m_url, user, pass);
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, sep3Code);
		LOG.fine("Executing statement: " + stmt);
		try (ResultSet rs = stmt.executeQuery()) {
			boolean validRS = rs.next();
			String result = "";
			if (validRS) {
				result = rs.getString(1);
			}
			LOG.fine("Returning: " + result);

			rs.close();
			stmt.close();

			return result;
		}
	}

	/**
	 * Retrieves allowed attributes for a given SEP3 code
	 * @param sep3Code for which attributes are requested
	 * @return String containing information about allowed attributes, quantifiers, etc.
	 * @throws SQLException if DB error occurs
	 */
	public static String getAllowedAttribs(String sep3Code) throws SQLException {
		String query = "select kuerzel, attribute from " + wb + " w join " + st + " s " + "on w.typ = s.nebentypbez "
				+ "where (s.datenfeld = 'PETRO' OR s.datenfeld = 'diverse') AND kuerzel= ?";
		if (credChanged)
			setConn(m_url, user, pass);
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, sep3Code);
		LOG.fine("Executing statement: " + stmt);
		try (ResultSet rs = stmt.executeQuery()) {
			boolean validRS = rs.next();
			String result = "";
			if (validRS) {
				result = rs.getString(2);
			}
			LOG.fine("Returning: " + result);

			rs.close();
			stmt.close();

			return result;
		}
	}

	/**
	 * returns appropriate quantifier string for a SEP3 code and quantifier (as digit)
	 * @param sep3Code that is quantified with quant
	 * @param quant quantifyer for sep3code (as digit)
	 * @return quantifier based on sep3 code and quantifyer
	 * @throws SQLException in case of DB error
	 */
	public static String getBodenQuant(String sep3Code, String quant) throws SQLException {
		String allowedAttributes;
		String quantBez;

		allowedAttributes = getAllowedAttribs(sep3Code);
		quantBez = getQuantBezFromAttribs(allowedAttributes);

		String query = "select w.kuerzel, w.klartext, s.nebentypbez from " + wb + " w join " + st + " s "
				+ "on w.typ = s.nebentypbez where (s.nebentypbez = ? AND w.kuerzel = ?);";
		if (credChanged)
			setConn(m_url, user, pass);
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, quantBez);
		stmt.setString(2, quant);
		LOG.fine("Executing statement: " + stmt);
		try (ResultSet rs = stmt.executeQuery()) {
			boolean validRS = rs.next();
			String result = "";
			if (validRS) {
				result = rs.getString(2);
			}
			LOG.fine("Returning: " + result);

			rs.close();
			stmt.close();

			return result;
		}
	}

	/**
	 * extracts quantifyer type from a comma separated list of attributes
	 * @param attributes as comma separated list
	 * @return quantifyer type
	 */
	public static String getQuantBezFromAttribs(String attributes) {
		String[] attribs;
		attribs = attributes.split(",");
		for (int i = 0; i < attribs.length; i++) {
			if (attribs[i].startsWith("Quant_")) {
				return attribs[i];
			}
		}
		return "";
	}

}
