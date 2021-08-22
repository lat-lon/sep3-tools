package org.sep3tools;

import org.sep3tools.gen.*;

public class PetroVisitor extends PetroGrammarBaseVisitor<String>{


    @Override public String visitSchichtbeschreibung(PetroGrammarParser.SchichtbeschreibungContext ctx) {
        return visitChildren(ctx);
    }

    @Override public String visitBod_bek(PetroGrammarParser.Bod_bekContext ctx) {
        String boden = ctx.BODEN().getText();
        return switch (boden) {
            case "^u" -> "Schluff";
            case "^ms" -> "Mittelsandstein";
            case "^gs" -> "Grobsandstein";
            default -> boden;
        };
    }

    @Override public String visitAttr(PetroGrammarParser.AttrContext ctx) {
        String attr = ctx.ATTRIBUT().getText();
        return switch (attr) {
            case "r2" -> "kantengerundet";
            case "r3" -> "mäßig gerundet";
            case "tw" -> "teilweise";
            case "lw" -> "lagenweise";
            case "gs" -> "grobsandig";
            case "t" -> "tonig";
            case "nf" -> "Nachfall";
            case "bei" -> "bei";
            default -> attr;
        };
    }

    @Override public String visitUebergang_bes(PetroGrammarParser.Uebergang_besContext ctx)  {
        return  visit(ctx.b1) + " bis " + visit(ctx.b2);
    }

    @Override public String visitAufzaehlung_b(PetroGrammarParser.Aufzaehlung_bContext ctx) {
        return  visit(ctx.bestandteile(0)) + ", " + visit(ctx.bestandteile(1));
    }

    @Override public String visitTeil(PetroGrammarParser.TeilContext ctx) {
        String teil = visit(ctx.bestandteil());
        String attr = visit(ctx.attribute());
        if (attr.startsWith(" (")) return teil + attr;
        return teil + " (" + attr + ")";
    }


    @Override public String visitAufzaehlung_a(PetroGrammarParser.Aufzaehlung_aContext ctx) {
        String att1 = visit(ctx.attribute(0));
        String att2 = visit(ctx.attribute(1));
        if (att1.startsWith(" (")) att1 = att1.substring(2,att1.length()-1);
        if (att2.startsWith(" (")) att1 = att2.substring(2,att2.length()-1);
        return " (" + att1 + ", " + att2 + ")";
    }

    @Override public String visitUebergang_att(PetroGrammarParser.Uebergang_attContext ctx) {
        return visit(ctx.attribut(0)) + " bis " + visit(ctx.attribut(1));
    }

    @Override public String visitUnter_Attribute(PetroGrammarParser.Unter_AttributeContext ctx) {
        String unter = visit(ctx.unter);
        if (unter.startsWith(" (")) return visit(ctx.attr) + unter;
        return visit(ctx.attr) + " (" + visit(ctx.unter) + ")";
    }

    @Override public String visitAtt(PetroGrammarParser.AttContext ctx) {
        return visit(ctx.attribut());
    }

    @Override public String visitAttr_fraglich(PetroGrammarParser.Attr_fraglichContext ctx) {
        return visitChildren(ctx) + " (fraglich)";
    }
    @Override public String visitAttr_sicher(PetroGrammarParser.Attr_sicherContext ctx) {
        return visitChildren(ctx) + " (sicher)";
    }

    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
        if (aggregate == null) return nextResult;
        if (nextResult == null) return aggregate;
        return aggregate + nextResult;
    }
}