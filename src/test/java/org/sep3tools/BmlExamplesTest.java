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

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Class not ready for automatic tests, integrations tests depend on running database")
public class BmlExamplesTest {

	static final String DB_URL = "jdbc:postgresql://localhost/petroparser";
	static final String USER = "petroparser";
	static final String PASS = "PetroParser";
	static final String SMTABLE = "bml.bml_schluesselmapping";

	@Test
	public void verifyBmlExamples() {

		JavaConnector.setUrl(DB_URL);
		JavaConnector.setUser(USER);
		JavaConnector.setPass(PASS);
		JavaConnector.setSm(SMTABLE);

		try {
			File file = new File("bmltest.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			Enumeration enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String sep3String = (String) enuKeys.nextElement();
				String expectedTranslation = properties.getProperty(sep3String);
				String translation = LaunchBML.S3_AsBmlLitho(sep3String);
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
