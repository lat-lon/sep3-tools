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
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;

@Ignore("Class not ready for automatic tests, integrations tests depend on running database")
public class SepExamplesTest {

	static final String DB_URL = "jdbc:postgresql://localhost/petroparser";
	static final String USER = "petroparser";
	static final String PASS = "PetroParser";
	static final String WBTABLE = "woerterbuch.\"Woerterbuch\"";
	static final String STTABLE = "woerterbuch.\"Schluesseltypen\"";

	@Test
	public void verifyPetroExamples() {
		verifySepExamples("PETRO", "petrotest.properties");
	}

	@Test
	public void verifyBeschbgExamples() {
		verifySepExamples("BESCHBG", "beschbgtest.properties");
	}

	@Test
	public void verifyBeschbvExamples() {
		verifySepExamples("BESCHBV", "beschbvtest.properties");
	}

	@Test
	public void verifyBgruppeExamples() {
		verifySepExamples("BGRUPPE", "bgruppetest.properties");
	}

	@Test
	public void verifyGeneseExamples() {
		verifySepExamples("GENESE", "genesetest.properties");
	}

	@Test
	public void verifyKalkgehExamples() {
		verifySepExamples("KALKGEH", "kalkgehtest.properties");
	}

	@Test
	public void verifyZusatzExamples() {
		verifySepExamples("ZUSATZ", "zusatztest.properties");
	}

	@Test
	public void verifyFarbeExamples() {
		verifySepExamples("FARBE", "farbetest.properties");
	}

	public void verifySepExamples(String df, String propFile) {

		JavaConnector.setUrl(DB_URL);
		JavaConnector.setUser(USER);
		JavaConnector.setPass(PASS);
		JavaConnector.setWb(WBTABLE);
		JavaConnector.setSt(STTABLE);
		JavaConnector.setDf(df);

		try {
			File file = new File(propFile);
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			Enumeration enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String sep3String = (String) enuKeys.nextElement();
				String expectedTranslation = properties.getProperty(sep3String);
				String translation = Launch.S3_AsText(sep3String);
				assertThat(translation, CoreMatchers.is(expectedTranslation));
				// System.out.println(sep3String + " transation matched expected value: "
				// + translation);
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

}
