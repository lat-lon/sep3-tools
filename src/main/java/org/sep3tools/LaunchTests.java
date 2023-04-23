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

import java.util.Enumeration;
import java.util.Properties;

import org.postgresql.pljava.annotation.Function;

public class LaunchTests {

	@Function
	public static String verifySepExamples(String wb, String st) {
		JavaConnector.setWb(wb);
		JavaConnector.setSt(st);

		Properties testCases = new Properties();

		testCases.put("^ms", "Mittelsandstein");
		testCases.put("^u", "Schluffstein");
		testCases.put("^gs", "Grobsandstein");
		testCases.put("W,^gs(l-fs),^u", "Wasser, Grobsandstein (lehmig bis feinsandig), Schluffstein");
		testCases.put("^ms(r2,r3(tw),gs(lw,r2-r3)),^u(t,lw),^gs(r3,bei(113),nf?)",
				"Mittelsandstein (kantengerundet, mäßig gerundet (teilweise), grobsandig (lagenweise, kantengerundet bis mäßig gerundet)), Schluffstein (tonig, lagenweise), Grobsandstein (mäßig gerundet, bei 113, Nachfall (fraglich))");
		testCases.put(
				"^ksw-^kal(mas,fe,spt,brc,cav2(bei(25.5)),p(unz,tv(40.4))),Rfl(\"ca\"),^if2(knl,bei(31.4,32.8,34.65,35.8,40.4))",
				"Schwammkalk bis Algenkalk (massig, fest, splittrig, brechend, schwach kavernös (bei 25,5), porös (unten zunehmend, Teufe von 40,4)), Hohlraumfüllung (Kalzit), wenig Flint (knollig, bei 31,4, 32,8, 34,65, 35,8, 40,4)");
		testCases.put("G(fg-gg,ms-gs,mats,mata,grs(tw)),fX-mX(mata),mS(fs,grs,fg-mg,mx(voe))",
				"Kies [gerundet] (feinkiesig bis grobkiesig, mittelsandig bis grobsandig, Schwarzwaldmaterial, alpines Material, grusig (teilweise)), Feinsteinstücke [2,0-6,3 mm] bis Mittelsteinstücke [6,3-20 mm] (alpines Material), Mittelsand [0,2-0,63 mm] (feinsandig, grusig, feinkiesig bis mittelkiesig, mittelsteinig (vereinzelt vorhanden))");
		testCases.put("G(fg-gg,ms-gs,mats,mata,grs(tw)),fX-mX(mata),mS(fs,grs,fg-mg2,mx(voe))",
				"Kies [gerundet] (feinkiesig bis grobkiesig, mittelsandig bis grobsandig, Schwarzwaldmaterial, alpines Material, grusig (teilweise)), Feinsteinstücke [2,0-6,3 mm] bis Mittelsteinstücke [6,3-20 mm] (alpines Material), Mittelsand [0,2-0,63 mm] (feinsandig, grusig, feinkiesig bis schwach mittelkiesig, mittelsteinig (vereinzelt vorhanden))");
		testCases.put("U(hz(res),H(res),zg1)", "Schluff (Holzreste (Reste), Torf (Reste), sehr schwach zersetzt)");
		testCases.put("(S-G)(u,t,pf(zg,vw))",
				"(Sand [allgemein] bis Kies [gerundet]) (schluffig, tonig, Pflanzenreste (zersetzt, verwittert))");
		testCases.put("U(ms2,x(+Gr(gro(0.005))))", "Schluff (schwach mittelsandig, steinig (Granit (groß 0,005)))");
		testCases.put("(^u(fs)-^fs(u)),^d(u,\"ba\")",
				"(Schluffstein (feinsandig) bis Feinsandstein (schluffig)), Dolomitstein (schluffig, Baryt)");
		testCases.put("(fG-gG)(x)", "(Feinkies [2,0-6,3 mm] bis Grobkies [20-63 mm]) (steinig)");
		testCases.put("(^u(t,fs(tw)),^gs,^fs,^d(s,ikl))(wl)",
				"(Schluffstein (tonig, feinsandig (teilweise)), Grobsandstein, Feinsandstein, Dolomitstein (sandig, intraklastisch)) (wechsellagernd)");
		testCases.put("(U,fS)(ms2)", "(Schluff, Feinsand [0,063-0,2 mm]) (schwach mittelsandig)");
		testCases.put("(^k(mas,fla,pof,bel)),(fls(rgu))",
				"Kalkstein (massig, flaserig, Porifera, Belemniten), Flasern (unregelmäßig)");

		String passed = "Passed Tests:\n";
		String failed = "Failed Tests:\n";

		Enumeration enuKeys = testCases.keys();
		while (enuKeys.hasMoreElements()) {
			String sep3String = (String) enuKeys.nextElement();
			String expectedTranslation = testCases.getProperty(sep3String);
			String translation = Launch.S3_AsText(sep3String);
			if (translation.equals(expectedTranslation)) {
				passed = passed + sep3String + "\n";
				// result = result + sep3String + " translated to " + translation + " - OK
				// \n";
			}
			else {
				failed = failed + "Test failed: " + sep3String + " translated to " + translation + " but "
						+ expectedTranslation + " was expected.\n";
			}
		}
		return passed + "\n" + failed;
	}

	@Function
	public static String verifyBmlExamples(String sm) {
		JavaConnector.setSm(sm);

		Properties testCases = new Properties();

		testCases.put("^hzk, fS(ms2, \"gl\"2)", "fS,mS");
		testCases.put("G(fg-gg,ms-gs,mats,mata,grs(tw)),fX-mX(mata),mS(fs,grs,fg-mg2,mx(voe))",
				"G,fG,gG,mS,gS,eG,X,fS,mG");
		testCases.put("U(hz(res),H(res),zg1)", "U,Pfl,H");

		String passed = "Passed Tests:\n";
		String failed = "Failed Tests:\n";

		Enumeration enuKeys = testCases.keys();
		while (enuKeys.hasMoreElements()) {
			String sep3String = (String) enuKeys.nextElement();
			String expectedTranslation = testCases.getProperty(sep3String);
			String translation = LaunchBML.S3_AsBmlLitho(sep3String);
			if (translation.equals(expectedTranslation)) {
				passed = passed + sep3String + "\n";
			}
			else {
				failed = failed + "Test failed: " + sep3String + " translated to " + translation + " but "
						+ expectedTranslation + " was expected.\n";
			}
		}
		return passed + "\n" + failed;

	}

}
