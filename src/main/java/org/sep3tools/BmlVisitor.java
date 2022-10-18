package org.sep3tools;

import static java.util.Objects.isNull;
import static org.sep3tools.JavaConnector.getBodenQuant;

import java.sql.SQLException;
import java.util.logging.Logger;

import org.sep3tools.gen.PetroGrammarBaseVisitor;
import org.sep3tools.gen.PetroGrammarParser;

/**
 * This class parses a SEP3 String and creates a BML String
 *
 * @author Jeronimo Wanhoff <kontakt@jeronimowanhoff.de>
 */
public class BmlVisitor extends PetroGrammarBaseVisitor<String> {

	private static final Logger LOG = Logger.getLogger(JavaConnector.class.getName());

	private static final int MAX_QUANTIFIER = 5;

	/**
	 * get translation for SEP3-String
	 * @param searchTerm
	 * @return BML translation
	 */
	private static String getBmlResultSet(String searchTerm) {
		try {
			return JavaConnector.getS3AsBMmlLitho(searchTerm);
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
	 * process single soil, remove quantifier, if present
	 * @param ctx the parse tree
	 * @return translated string for soil parse tree
	 */
	@Override
	public String visitBestandteil(PetroGrammarParser.BestandteilContext ctx) {
		String boden = ctx.getText();
		String bodenTerm = getBmlResultSet(boden);
		if (!bodenTerm.isEmpty())
			return bodenTerm;
		for (int i = 0; i <= MAX_QUANTIFIER; i++) {
			if (boden.endsWith(String.valueOf(i))) {
				String bodenShort = boden.substring(0, boden.length() - 1);
				bodenTerm = getBmlResultSet(bodenShort);
				if (!bodenTerm.isEmpty()) {
					return bodenTerm;
				}
			}
		}
		return "";
	}

	/**
	 * process single attribute, removes quantifier, if present
	 * @param ctx the parse tree
	 * @return translated string for attribute parse tree
	 */
	@Override
	public String visitAttr(PetroGrammarParser.AttrContext ctx) {
		String attr = ctx.getText();
		if (!isNull(attr)) {
			String attrTerm = getBmlResultSet(attr);
			if (!attrTerm.isEmpty()) {
				return attrTerm;
			}
			for (int i = 0; i <= MAX_QUANTIFIER; i++) {
				if (attr.endsWith(String.valueOf(i))) {
					String attrShort = attr.substring(0, attr.length() - 1);
					attrTerm = getBmlResultSet(attrShort);
					if (!attrTerm.isEmpty()) {
						return attrTerm;
					}
				}
			}
		}
		return "";
	}

	/**
	 * process soil part of transition
	 * @param ctx the parse tree
	 * @return translated string of soil transition in parse tree
	 */
	@Override
	public String visitUebergang_bes(PetroGrammarParser.Uebergang_besContext ctx) {
		return visit(ctx.b1) + ", " + visit(ctx.b2);
	}

	/**
	 * process enumeration (aufzaehlung) of soil
	 * @param ctx the parse tree
	 * @return translated string for soil enumeration parse tree
	 */
	@Override
	public String visitAufzaehlung_b(PetroGrammarParser.Aufzaehlung_bContext ctx) {
		return visit(ctx.bestandteile(0)) + "," + visit(ctx.bestandteile(1));
	}

	/**
	 * process transition (uebergang) for soil with attributes
	 * @param ctx the parse tree
	 * @return translated string for transition parse tree
	 */
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
		return teil + ", " + attr;
	}

	/**
	 * process part of soil composition
	 * @param ctx the parse tree
	 * @return translated string for soil composition parse tree
	 */
	@Override
	public String visitTeil(PetroGrammarParser.TeilContext ctx) {
		String teil = visit(ctx.bestandteil());

		if (isNull(ctx.attribute()))
			return teil;

		String attr = visit(ctx.attribute());
		if (isNull(attr))
			return teil;

		return teil + "," + attr;
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
		if (att1.startsWith(","))
			att1 = att1.substring(2, att1.length() - 1);
		if (att2.startsWith(","))
			att2 = att2.substring(2, att2.length() - 1);
		return att1 + "," + att2;
	}

	/**
	 * process transition (uebergang) for attributes
	 * @param ctx the parse tree
	 * @return translated string for transition parse tree
	 */
	@Override
	public String visitUebergang_att(PetroGrammarParser.Uebergang_attContext ctx) {
		return visit(ctx.attribut(0)) + "," + visit(ctx.attribut(1));
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
		return attrib + ", " + unter;
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