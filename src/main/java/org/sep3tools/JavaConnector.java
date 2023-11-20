package org.sep3tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
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

	private static String wbKuerzel;

	private static String wbKlartext;

	private static String wbTyp;

	private static String wbAttribute;

	private static String st;

	private static String stDatenfeld;

	private static String stNebentypbez;

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
			setWbAttribute(properties.getProperty("WBATTRIBUTE"));
			setWbKlartext(properties.getProperty("WBKLARTEXT"));
			setWbKuerzel(properties.getProperty("WBKUERZEL"));
			setWbTyp(properties.getProperty("WBTYP"));
			setSt(properties.getProperty("SCHLUESSELTYPEN"));
			setStDatenfeld(properties.getProperty("STDATENFELD"));
			setStNebentypbez(properties.getProperty("STNEBENTYPBEZ"));
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

	public static void setWb(String wb) {
		JavaConnector.wb = wb;
	}

	public static void setWbKuerzel(String wbKuerzel) {
		JavaConnector.wbKuerzel = wbKuerzel;
	}

	public static void setWbKlartext(String wbKlartext) {
		JavaConnector.wbKlartext = wbKlartext;
	}

	public static void setWbTyp(String wbTyp) {
		JavaConnector.wbTyp = wbTyp;
	}

	public static void setWbAttribute(String wbAttribute) {
		JavaConnector.wbAttribute = wbAttribute;
	}

	public static void setSt(String st) {
		JavaConnector.st = st;
	}

	public static void setStDatenfeld(String stDatenfeld) {
		JavaConnector.stDatenfeld = stDatenfeld;
	}

	public static void setStNebentypbez(String stNebentypbez) {
		JavaConnector.stNebentypbez = stNebentypbez;
	}

	// String query = "SELECT Klartext from woerterbuch.Woerterbuch where Kuerzel=";

	private static void setConn() throws SQLException {
		if (conn != null && !conn.isClosed())
			conn.close();
		JavaConnector.conn = DriverManager.getConnection(m_url, user, pass);
		credChanged = false;
	}

	/**
	 * translates a SEP3 code to clear text
	 * @param sep3Code code for translation
	 * @return translated SEP3 string
	 * @throws SQLException if DB error occurs
	 */
	public static String getS3Name(String sep3Code) throws SQLException {
		String query = getQueryString(wb, st, df);

		if (credChanged)
			setConn();
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

	private static String getQueryString(String woerterbuch, String schluesselypen, String datenfeld) {
		return "select " + wbKuerzel + ", " + wbKlartext + " from " + woerterbuch + " w join " + schluesselypen + " s "
				+ "on w." + wbTyp + " = s." + stNebentypbez + " where (s." + stDatenfeld + " = '" + datenfeld + "' "
				+ "OR s." + stDatenfeld + " = 'diverse') AND " + wbKuerzel + " = ?";
	}

	public static String getS3inDfName(String datefield, String sep3Code) throws SQLException {
		String query;
		switch (datefield) {
			case "S:":
				query = getQueryString(wb, st, "STRAT");
				break;
			case "P:":
				query = getQueryString(wb, st, "PETRO");
				break;
			case "G:":
				query = getQueryString(wb, st, "GENESE");
				break;
			case "F:":
				query = getQueryString(wb, st, "FARBE");
				break;
			case "Z:":
				query = getQueryString(wb, st, "ZUSATZ");
				break;
			default:
				query = getQueryString(wb, st, df);
		}

		if (credChanged)
			setConn();
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
			setConn();
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
	 * @return String containing information about allowed attributes, quantifiers, etc.
	 * @throws SQLException if DB error occurs
	 */
	public static void setPropertiesFromDB() throws SQLException {
		String dbpart = "part";
		String dbvalue = "value";
		String mdtable = "public.sep3tools";
		Properties properties = new Properties();

		String query = "select " + dbpart + ", " + dbvalue + " from " + mdtable;
		if (credChanged)
			setConn();
		PreparedStatement stmt = conn.prepareStatement(query);
		try (ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				properties.setProperty(rs.getString(1), rs.getString(2));
			}
			setWb(properties.getProperty("WOERTERBUCH"));
			setWbAttribute(properties.getProperty("WBATTRIBUTE"));
			setWbKlartext(properties.getProperty("WBKLARTEXT"));
			setWbKuerzel(properties.getProperty("WBKUERZEL"));
			setWbTyp(properties.getProperty("WBTYP"));
			setSt(properties.getProperty("SCHLUESSELTYPEN"));
			setStDatenfeld(properties.getProperty("STDATENFELD"));
			setStNebentypbez(properties.getProperty("STNEBENTYPBEZ"));
			setSm(properties.getProperty("SCHLUESSELMAPPING"));
			setDf(properties.getProperty("DATEFIELD"));
			// System.out.println (properties.toString());

			rs.close();
			stmt.close();
		}
	}

	/**
	 * Retrieves allowed attributes for a given SEP3 code
	 * @param sep3Code for which attributes are requested
	 * @return String containing information about allowed attributes, quantifiers, etc.
	 * @throws SQLException if DB error occurs
	 */
	public static String getAllowedAttribs(String sep3Code) throws SQLException {
		String query = "select " + wbKuerzel + ", " + wbAttribute + " from " + wb + " w join " + st + " s " + "on w."
				+ wbTyp + " = s." + stNebentypbez + " where (s." + stDatenfeld + " = 'PETRO' OR s." + stDatenfeld
				+ " = 'diverse') AND " + wbKuerzel + " = ?";
		if (credChanged)
			setConn();
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
	public static String getItemQuant(String sep3Code, String quant) throws SQLException {
		String allowedAttributes;
		String quantBez;

		allowedAttributes = getAllowedAttribs(sep3Code);
		quantBez = getQuantBezFromAttribs(allowedAttributes);

		String query = "select w." + wbKuerzel + ", w." + wbKlartext + ", s." + stNebentypbez + " from " + wb
				+ " w join " + st + " s " + "on w." + wbTyp + " = s." + stNebentypbez + " where (s. " + stNebentypbez
				+ " = ? AND w." + wbKuerzel + " = ?);";
		if (credChanged)
			setConn();
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
