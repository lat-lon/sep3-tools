package org.sep3tools;

import org.sep3tools.gen.*;

import java.sql.*;
import java.util.logging.Logger;

import static java.util.Objects.isNull;

public class PetroVisitor extends PetroGrammarBaseVisitor<String> {

	private static final int MAX_QUANTIFIER = 5;

	private static final Logger LOG = Logger.getLogger(JavaConnector.class.getName());

	private static String getS3ResultSet(String searchTerm) {
		try {
			return JavaConnector.getS3Name(searchTerm);
		}
		catch (SQLException e) {
			LOG.warning("Dictionary is not available, fallback to internal dictionary if possible." + " Caused by "
					+ e.getLocalizedMessage());
		}
		return "";
	}

	@Override
	public String visitSchichtbeschreibung(PetroGrammarParser.SchichtbeschreibungContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public String visitBestandteil(PetroGrammarParser.BestandteilContext ctx) {
		String boden = ctx.getText();
		String bodenTerm = getS3ResultSet(boden);
		if (!bodenTerm.isEmpty())
			return bodenTerm;
		for (int i = 0; i <= MAX_QUANTIFIER; i++) {
			if (boden.endsWith(String.valueOf(i))) {
				bodenTerm = getS3ResultSet(boden.substring(0, boden.length() - 1));
				if (!bodenTerm.isEmpty())
					return bodenTerm + i;
			}
		}

		switch (boden) {
		case "^u":
			return "Schluffstein";
		case "^ms":
			return "Mittelsandstein";
		case "^gs":
			return "Grobsandstein";
		default:
			return boden;
		}
	}

	@Override
	public String visitAttr(PetroGrammarParser.AttrContext ctx) {
		String attr = ctx.getText();
		if (!isNull(attr)) {
			String attrTerm = getS3ResultSet(attr);
			if (!attrTerm.isEmpty()) {
				return attrTerm;
			}
			for (int i = 0; i <= MAX_QUANTIFIER; i++) {
				if (attr.endsWith(String.valueOf(i))) {
					attrTerm = getS3ResultSet(attr.substring(0, attr.length() - 1));
					if (!attrTerm.isEmpty())
						return attrTerm + i;
				}
			}
		}
		switch (attr) {
		case "r2":
			return "kantengerundet";
		case "r3":
			return "mäßig gerundet";
		case "tw":
			return "teilweise";
		case "lw":
			return "lagenweise";
		case "gs":
			return "grobsandig";
		case "t":
			return "tonig";
		case "nf":
			return "Nachfall";
		case "bei":
			return "bei";
		default:
			return attr;
		}
	}

	@Override
	public String visitUebergang_bes(PetroGrammarParser.Uebergang_besContext ctx) {
		return visit(ctx.b1) + " bis " + visit(ctx.b2);
	}

	@Override
	public String visitAufzaehlung_b(PetroGrammarParser.Aufzaehlung_bContext ctx) {
		return visit(ctx.bestandteile(0)) + ", " + visit(ctx.bestandteile(1));
	}

	@Override
	public String visitUebergang_b(PetroGrammarParser.Uebergang_bContext ctx) {
		String teil = visit(ctx.uebergang_bes());

		if (isNull(ctx.attribute()))
			return teil;

		String attr = visit(ctx.attribute());
		if (isNull(attr))
			return teil;

		if (attr.startsWith(" ("))
			return teil + attr;
		return teil + " (" + attr + ")";
	}

	@Override
	public String visitTeil(PetroGrammarParser.TeilContext ctx) {
		String teil = visit(ctx.bestandteil());

		if (isNull(ctx.attribute()))
			return teil;

		String attr = visit(ctx.attribute());
		if (isNull(attr))
			return teil;

		if (attr.startsWith(" ("))
			return teil + attr;
		return teil + " (" + attr + ")";
	}

	@Override
	public String visitAufzaehlung_a(PetroGrammarParser.Aufzaehlung_aContext ctx) {
		String att1 = visit(ctx.attribute(0));
		String att2 = visit(ctx.attribute(1));
		if (att1.startsWith(" ("))
			att1 = att1.substring(2, att1.length() - 1);
		if (att2.startsWith(" ("))
			att2 = att2.substring(2, att2.length() - 1);
		return " (" + att1 + ", " + att2 + ")";
	}

	@Override
	public String visitUebergang_att(PetroGrammarParser.Uebergang_attContext ctx) {
		return visit(ctx.attribut(0)) + " bis " + visit(ctx.attribut(1));
	}

	@Override
	public String visitUnter_Attribute(PetroGrammarParser.Unter_AttributeContext ctx) {
		String attrib = visit(ctx.attr);
		String unter = visit(ctx.unter);
		if (isNull(unter))
			return visit(ctx.attr);
		if (attrib.equals("bei")) {
			if (unter.startsWith(" ("))
				unter = unter.substring(2, unter.length() - 1);
			return attrib + " " + unter;
		}
		if (unter.matches("([0-9]|,)+"))
			return attrib + " " + unter;
		if (unter.startsWith(" ("))
			return visit(ctx.attr) + unter;
		return attrib + " (" + unter + ")";
	}

	@Override
	public String visitAtt(PetroGrammarParser.AttContext ctx) {
		return visit(ctx.attribut());
	}

	@Override
	public String visitAttr_fraglich(PetroGrammarParser.Attr_fraglichContext ctx) {
		return visitChildren(ctx) + " (fraglich)";
	}

	@Override
	public String visitAttr_sicher(PetroGrammarParser.Attr_sicherContext ctx) {
		return visitChildren(ctx) + " (sicher)";
	}

	@Override
	public String visitAttr_tiefe(PetroGrammarParser.Attr_tiefeContext ctx) {
		String tiefe = ctx.getText();
		tiefe = tiefe.replace(".", ",");
		return tiefe;
	}

	@Override
	protected String aggregateResult(String aggregate, String nextResult) {
		if (aggregate == null)
			return nextResult;
		if (nextResult == null)
			return aggregate;
		return aggregate + nextResult;
	}

}