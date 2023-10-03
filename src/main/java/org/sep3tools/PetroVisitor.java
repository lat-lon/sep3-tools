package org.sep3tools;

import org.sep3tools.gen.*;

import java.sql.*;
import java.util.logging.Logger;

import static java.util.Objects.isNull;
import static org.sep3tools.JavaConnector.getBodenQuant;

/**
 * This class parses a SEP3 String and translates it
 *
 * @author Jeronimo Wanhoff <kontakt@jeronimowanhoff.de>
 * @author <a href="mailto:friebe@lat-lon.de">Torsten Friebe</a>
 */
public class PetroVisitor extends PetroGrammarBaseVisitor<String> {

	private static final int MAX_QUANTIFIER = 5;

	private static final Logger LOG = Logger.getLogger(JavaConnector.class.getName());

	/**
	 * get translation for SEP3-String
	 * @param searchTerm
	 * @return translation
	 */
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

	/**
	 * process complete SEP3 string
	 * @param ctx the parse tree
	 * @return translated string complete SEP3 parse tree
	 */
	@Override
	public String visitSchichtbeschreibung(PetroGrammarParser.SchichtbeschreibungContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * process single soil takes quantifier into account, if present
	 * @param ctx the parse tree
	 * @return translated string for soil parse tree
	 */
	@Override
	public String visitBestandteil(PetroGrammarParser.BestandteilContext ctx) {
		String boden = getBodenTerm(ctx.TEIL().getText());
		String attrib;
		if (isNull(ctx.attribute())) {
			attrib = "";
		}
		else {
			String attr = visit(ctx.attribute());
			if (isNull(attr)) {
				attrib = "";
			}
			else if (attr.trim().startsWith("(")) {
				attrib = attr;
			}
			else {
				attrib = " (" + attr + ")";
			}
		}
		// if (ctx.getText().startsWith("("))
		// return "(" + boden + attrib + ")";
		return boden + attrib;
	}

	private String getBodenTerm(String boden) {
		String bodenTerm = getS3ResultSet(boden);
		if (!bodenTerm.isEmpty())
			return bodenTerm;
		for (int i = 0; i <= MAX_QUANTIFIER; i++) {
			if (boden.endsWith(String.valueOf(i))) {
				String bodenShort = boden.substring(0, boden.length() - 1);
				bodenTerm = getS3ResultSet(bodenShort);
				if (!bodenTerm.isEmpty()) {
					String bodenQuant = String.valueOf(i);
					try {
						bodenQuant = getBodenQuant(bodenShort, String.valueOf(i));
						return bodenQuant + " " + bodenTerm;
					}
					catch (SQLException e) {
						LOG.warning(
								"Quantifier not found, fallback to digit." + " Caused by " + e.getLocalizedMessage());
					}
					return bodenTerm + bodenQuant;
				}
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

	/**
	 * process single attribute takes quantifier into account, if present
	 * @param ctx the parse tree
	 * @return translated string for attribute parse tree
	 */
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
					String attrShort = attr.substring(0, attr.length() - 1);
					attrTerm = getS3ResultSet(attrShort);
					if (!attrTerm.isEmpty()) {
						String attrQuant = String.valueOf(i);
						try {
							attrQuant = getBodenQuant(attrShort, String.valueOf(i));
							return attrQuant + " " + attrTerm;
						}
						catch (SQLException e) {
							LOG.warning("Quantifier not found, fallback to digit." + " Caused by "
									+ e.getLocalizedMessage());
						}
						return attrTerm + attrQuant;
					}
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

	/**
	 * process soil part of transition
	 * @param ctx the parse tree
	 * @return translated string of soil transition in parse tree
	 */
	@Override
	public String visitUebergang_bes(PetroGrammarParser.Uebergang_besContext ctx) {
		String teile = "";
		String attrib;
		if (ctx.getText().trim().startsWith("(")) {
			teile = "(" + visit(ctx.uebergang_bes()) + ")";
		}
		else {
			for (PetroGrammarParser.BestandteilContext teil : ctx.bestandteil()) {
				if (teile.isEmpty()) {
					teile = visit(teil);
				}
				else {
					teile = teile + " bis " + visit(teil);
				}
			}
		}
		if (isNull(ctx.attribute())) {
			attrib = "";
		}
		else {
			String attr = visit(ctx.attribute());
			if (isNull(attr)) {
				attrib = "";
			}
			else if (attr.trim().startsWith("(")) {
				attrib = attr;
			}
			else {
				attrib = " (" + attr + ")";
			}
		}
		return teile + attrib;
	}

	/**
	 * process enumeration (aufzaehlung) of soil
	 * @param ctx the parse tree
	 * @return translated string for soil enumeration parse tree
	 */
	@Override
	public String visitAufzaehlung_b(PetroGrammarParser.Aufzaehlung_bContext ctx) {
		return visit(ctx.bestandteile(0)) + ", " + visit(ctx.bestandteile(1));
	}

	/**
	 * process enumeration (aufzaehlung) of soil in brackets
	 * @param ctx the parse tree
	 * @return translated string for soil enumeration parse tree
	 */
	@Override
	public String visitAufzaehlung_b_k(PetroGrammarParser.Aufzaehlung_b_kContext ctx) {
		String aufz;
		String attrib;

		aufz = "(" + visit(ctx.bestandteile(0)) + ", " + visit(ctx.bestandteile(1)) + ")";
		if (isNull(ctx.attribute())) {
			attrib = "";
		}
		else {
			String attr = visit(ctx.attribute());
			if (isNull(attr)) {
				attrib = "";
			}
			else if (attr.trim().startsWith("(")) {
				attrib = attr;
			}
			else {
				attrib = " (" + attr + ")";
			}
		}
		return aufz + attrib;

	}

	/**
	 * process transition (uebergang) for soil with attributes
	 * @param ctx the parse tree
	 * @return translated string for transition parse tree
	 */
	@Override
	public String visitUebergang_b(PetroGrammarParser.Uebergang_bContext ctx) {
		return visit(ctx.uebergang_bes());
	}

	/**
	 * process part of soil composition
	 * @param ctx the parse tree
	 * @return translated string for soil composition parse tree
	 */
	@Override
	public String visitTeil(PetroGrammarParser.TeilContext ctx) {
		return visit(ctx.bestandteil());
	}

	/**
	 * process enumeration (aufzaehlung) of attributes
	 * @param ctx the parse tree
	 * @return translated string for attribute enumeration parse tree
	 */
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

	/**
	 * process transition (uebergang) for attributes
	 * @param ctx the parse tree
	 * @return translated string for transition parse tree
	 */
	@Override
	public String visitUebergang_att(PetroGrammarParser.Uebergang_attContext ctx) {
		return visit(ctx.attribut(0)) + " bis " + visit(ctx.attribut(1));
	}

	/**
	 * process sub attributes
	 * @param ctx the parse tree
	 * @return translated string for sub attribute parse tree
	 */
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

	/**
	 * process attribute "fraglich"
	 * @param ctx the parse tree
	 * @return translated string for attribute "fraglich"
	 */
	@Override
	public String visitAtt(PetroGrammarParser.AttContext ctx) {
		return visit(ctx.attribut());
	}

	@Override
	public String visitAttr_fraglich(PetroGrammarParser.Attr_fraglichContext ctx) {
		return visitChildren(ctx) + " (fraglich)";
	}

	/**
	 * process attribute "sicher"
	 * @param ctx the parse tree
	 * @return translated string for attribut "sicher"
	 */
	@Override
	public String visitAttr_sicher(PetroGrammarParser.Attr_sicherContext ctx) {
		return visitChildren(ctx) + " (sicher)";
	}

	/**
	 * extracts the "tiefe"-Attibute and replaces "." with "," as separator for decimal
	 * point
	 * @param ctx the parse tree
	 * @return tiefe with "," as decimal point
	 */
	@Override
	public String visitAttr_tiefe(PetroGrammarParser.Attr_tiefeContext ctx) {
		String tiefe = ctx.getText();
		tiefe = tiefe.replace(".", ",");
		return tiefe;
	}

	/**
	 * combines the old result with new element
	 * @param aggregate result so far
	 * @param nextResult new item to add
	 * @return complete result
	 */
	@Override
	protected String aggregateResult(String aggregate, String nextResult) {
		if (aggregate == null)
			return nextResult;
		if (nextResult == null)
			return aggregate;
		return aggregate + nextResult;
	}

}