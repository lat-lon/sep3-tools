package org.sep3tools;/*----------------------------------------------------------------------------
 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
----------------------------------------------------------------------------*/

import org.junit.Ignore;
import org.junit.Test;

import org.hamcrest.CoreMatchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Enumeration;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;

@Ignore("Class not ready for automatic tests, integrations tests depend on running database")
public class SepExamplesTest {

	static final String DBPROPFILENAME = "db.properties";

	@Test
	public void verifyPetroExamples() {
		verifySepExamples("PETRO", "src/main/resources/petrotest.properties");
	}

	@Test
	public void verifyBeschbgExamples() {
		verifySepExamples("BESCHBG", "src/main/resources/beschbgtest.properties");
	}

	@Test
	public void verifyBeschbvExamples() {
		verifySepExamples("BESCHBV", "src/main/resources/beschbvtest.properties");
	}

	@Test
	public void verifyBgruppeExamples() {
		verifySepExamples("BGRUPPE", "src/main/resources/bgruppetest.properties");
	}

	@Test
	public void verifyGeneseExamples() {
		verifySepExamples("GENESE", "src/main/resources/genesetest.properties");
	}

	@Test
	public void verifyKalkgehExamples() {
		verifySepExamples("KALKGEH", "src/main/resources/kalkgehtest.properties");
	}

	@Test
	public void verifyZusatzExamples() {
		verifySepExamples("ZUSATZ", "src/main/resources/zusatztest.properties");
	}

	@Test
	public void verifyFarbeExamples() {
		verifySepExamples("FARBE", "src/main/resources/farbetest.properties");
	}

	@Test
	public void verifyFremdDatenfeldExamples() {
		verifySepExamples("PETRO", "src/main/resources/fremddatenfeldtest.properties");
	}

	public void verifySepExamples(String df, String propFile) {
		JavaConnector.setPropertiesFile(DBPROPFILENAME);
		JavaConnector.setDf(df);
		Properties properties = loadPropertiesFromFile(propFile, "=");

		Enumeration enuKeys = properties.keys();
		while (enuKeys.hasMoreElements()) {
			String sep3String = (String) enuKeys.nextElement();
			String expectedTranslation = properties.getProperty(sep3String);
			String translation = Launch.convertS3ToText(sep3String);
			assertThat(translation, CoreMatchers.is(expectedTranslation));
		}
	}

	private static Properties loadPropertiesFromFile(String filePath, String delimiter) {
		Properties properties = new Properties();

		try (InputStream input = new FileInputStream(filePath);
				BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

			String line;
			while ((line = reader.readLine()) != null) {
				// Split the line using the custom delimiter
				String[] keyValue = line.split(delimiter, 2);

				// Check if the line has a valid key-value pair
				if (keyValue.length == 2) {
					String key = keyValue[0].trim();
					String value = keyValue[1].trim();

					// Add the key-value pair to the properties
					properties.setProperty(key, value);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return properties;
	}

}
