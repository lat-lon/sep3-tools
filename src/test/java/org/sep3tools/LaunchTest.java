package org.sep3tools;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class LaunchTest {

	@Test
	public void verifyThatRockNameTextIsReturned() {
		String sep3String = "^ms(r2,r3(tw),gs(lw,r2-r3)),^u(t,lw),^gs(r3,bei(113),nf?)";
		String parsed = Launch.parseS3(sep3String);

		assertThat(parsed, CoreMatchers.is(
				"Mittelsandstein (kantengerundet, mäßig gerundet (teilweise), grobsandig (lagenweise, kantengerundet bis mäßig gerundet)), Schluffstein (tonig, lagenweise), Grobsandstein (mäßig gerundet, bei 113, Nachfall (fraglich))"));
	}

}
