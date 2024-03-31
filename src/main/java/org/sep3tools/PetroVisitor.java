package org.sep3tools;

import org.sep3tools.gen.*;

import java.sql.SQLException;
import java.util.logging.Logger;

import static java.util.Objects.isNull;
import static org.sep3tools.JavaConnector.*;

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

	private static String getS3InDfResultSet(String df, String searchTerm) {
		try {
			return JavaConnector.getS3inDfName(df, searchTerm);
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
	 * Visit a parse tree produced by the {@code bestandteil_klammer} labeled alternative
	 * in {@link PetroGrammarParser}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public String visitBestandteil_klammer(PetroGrammarParser.Bestandteil_klammerContext ctx) {
		String boden = "";
		if (ctx.bestandteil() instanceof PetroGrammarParser.Bestandteil_simpleContext) {
			boden = "(" + visitBestandteil_simple((PetroGrammarParser.Bestandteil_simpleContext) ctx.bestandteil())
					+ ")";
		}
		if (ctx.bestandteil() instanceof PetroGrammarParser.Bestandteil_fremddatenfeldContext) {
			boden = "(" + visitBestandteil_fremddatenfeld(
					(PetroGrammarParser.Bestandteil_fremddatenfeldContext) ctx.bestandteil()) + ")";
		}
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

		return boden + attrib;
	}

	/**
	 * Visit a parse tree produced by the {@code bestandteil_simple} labeled alternative
	 * in {@link PetroGrammarParser}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public String visitBestandteil_simple(PetroGrammarParser.Bestandteil_simpleContext ctx) {
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
		return boden + attrib;
	}

	// @Override
	public String visitBestandteil_fremddatenfeld(PetroGrammarParser.Bestandteil_fremddatenfeldContext ctx) {
		String dfKuerzel = ctx.DATENFELDKUERZEL().getText();
		String teil = ctx.TEIL().getText();
		if (!isNull(teil)) {
			String attrTerm = getS3InDfResultSet(dfKuerzel, teil);
			if (!attrTerm.isEmpty()) {
				return attrTerm;
			}
		}
		String teilQuant = getQuantifiedDfTerm(dfKuerzel, teil);
		if (teilQuant != null)
			return teilQuant;
		String colorTerm = getColorString(teil);
		if (colorTerm != null)
			return colorTerm;
		return dfKuerzel + teil;
	}

	/**
	 * Visit a parse tree produced by the {@code bestandteil_sicher} labeled alternative
	 * in {@link PetroGrammarParser}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public String visitBestandteil_sicher(PetroGrammarParser.Bestandteil_sicherContext ctx) {
		return visitChildren(ctx) + " sicher";
	}

	/**
	 * Visit a parse tree produced by the {@code bestandteil_fraglich} labeled alternative
	 * in {@link PetroGrammarParser}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public String visitBestandteil_fraglich(PetroGrammarParser.Bestandteil_fraglichContext ctx) {
		return visitChildren(ctx) + " fraglich";
	}

	private String getBodenTerm(String boden) {
		String bodenTerm = getS3ResultSet(boden);
		if (bodenTerm.length() > 0)
			return bodenTerm;

		String bodenQuant = getQuantifiedTerm(boden);
		if (bodenQuant != null)
			return bodenQuant;
		bodenTerm = getColorString(boden);
		if (bodenTerm != null)
			return bodenTerm;
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

	private static String getQuantifiedTerm(String sepItem) {
		String sep3Term;
		for (int i = 0; i <= MAX_QUANTIFIER; i++) {
			if (sepItem.endsWith(String.valueOf(i))) {
				String shortItem = sepItem.substring(0, sepItem.length() - 1);
				sep3Term = getS3ResultSet(shortItem);
				if (!sep3Term.isEmpty()) {
					String itemQuant = String.valueOf(i);
					try {
						itemQuant = getItemQuant(shortItem, String.valueOf(i));
						return itemQuant + " " + sep3Term;
					}
					catch (SQLException e) {
						LOG.warning(
								"Quantifier not found, fallback to digit." + " Caused by " + e.getLocalizedMessage());
					}
					return sep3Term + itemQuant;
				}
			}
		}
		return null;
	}

	private static String getQuantifiedDfTerm(String datenfeld, String sepItem) {
		String sep3Term;
		for (int i = 0; i <= MAX_QUANTIFIER; i++) {
			if (sepItem.endsWith(String.valueOf(i))) {
				String shortItem = sepItem.substring(0, sepItem.length() - 1);
				sep3Term = getS3InDfResultSet(datenfeld, shortItem);
				if (!sep3Term.isEmpty()) {
					String itemQuant = String.valueOf(i);
					try {
						itemQuant = getItemQuant(shortItem, String.valueOf(i));
						return itemQuant + " " + sep3Term;
					}
					catch (SQLException e) {
						LOG.warning(
								"Quantifier not found, fallback to digit." + " Caused by " + e.getLocalizedMessage());
					}
					return sep3Term + itemQuant;
				}
			}
		}
		return null;
	}

	private static String getColorString(String color) {
		String bodenTerm = "";
		String forColorSeparation = color;
		int partialBodenLength = 2;
		while (partialBodenLength <= forColorSeparation.length()) {
			String partialTermForColor = forColorSeparation.substring(forColorSeparation.length() - partialBodenLength);
			String colorPart = getS3InDfResultSet("F:", partialTermForColor);
			if (!colorPart.isEmpty()) {
				bodenTerm = colorPart + bodenTerm;
				if (partialTermForColor.equals(forColorSeparation)) {
					return bodenTerm;
				}
				forColorSeparation = forColorSeparation.substring(0, forColorSeparation.length() - partialBodenLength);
				partialBodenLength = 1;
				if (!forColorSeparation.endsWith("dd")
						&& (forColorSeparation.endsWith("h") || forColorSeparation.endsWith("d"))) {
					partialBodenLength = 0;
				}
			}
			partialBodenLength++;
		}
		return null;
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
			String attrQuant = getQuantifiedTerm(attr);
			if (attrQuant != null)
				return attrQuant;
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
	public String visitAttr_fremddatenfeld(PetroGrammarParser.Attr_fremddatenfeldContext ctx) {
		String dfKuerzel = ctx.DATENFELDKUERZEL().getText();
		String attr = ctx.TEIL().getText();
		if (!isNull(attr)) {
			String attrTerm = getS3InDfResultSet(dfKuerzel, attr);
			if (!attrTerm.isEmpty()) {
				return attrTerm;
			}
		}
		String colorTerm = getColorString(attr);
		if (colorTerm != null)
			return colorTerm;
		return dfKuerzel + attr;
	}

	/**
	 * process soil part of transition
	 * @param ctx the parse tree
	 * @return translated string of soil transition in parse tree
	 */
	@Override
	public String visitUebergang_bes(PetroGrammarParser.Uebergang_besContext ctx) {
		String teile = "";
		String attrib = "";
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
		for (PetroGrammarParser.AttributeContext teil : ctx.attribute()) {
			attrib = attrib + " (" + visit(teil) + ")";
			if (attrib.startsWith(" ( (")) {
				attrib = attrib.substring(2, attrib.length() - 1);
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
		String attrib = "";
		for (PetroGrammarParser.AttributeContext teil : ctx.attribute()) {
			if (attrib.isEmpty()) {
				attrib = visit(teil);
			}
			else {
				attrib = attrib + ", " + visit(teil);
			}
			if (attrib.startsWith(" ( (")) {
				attrib = attrib.substring(2, attrib.length() - 1);
			}
		}
		return attrib;
	}

	/**
	 * process enumeration (aufzaehlung) of attributes
	 * @param ctx the parse tree
	 * @return translated string for attribute enumeration parse tree
	 */
	@Override
	public String visitAufzaehlung_a_klammer(PetroGrammarParser.Aufzaehlung_a_klammerContext ctx) {
		String att1 = visit(ctx.attribute(0));
		String att2 = visit(ctx.attribute(1));
		if (att1.startsWith(" ("))
			att1 = att1.substring(2, att1.length() - 1);
		if (att2.startsWith(" ("))
			att2 = att2.substring(2, att2.length() - 1);
		return "(" + att1 + ") (" + att2 + ")";
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