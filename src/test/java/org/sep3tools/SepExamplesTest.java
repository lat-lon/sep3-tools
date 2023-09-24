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

	public void verifySepExamples(String df, String propFile) {

		JavaConnector.setPropertiesFile(DBPROPFILENAME);

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
