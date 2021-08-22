package org.sep3_tools;

import org.sep3_tools.gen.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class Launch {

    public static void main(String[] args) throws Exception {
        ANTLRInputStream input = new ANTLRInputStream("^ms(r2,r3(tw),gs(lw,r2-r3)),^u(t,lw),^gs(r3,bei(113),nf?)");
        //ANTLRInputStream input = new ANTLRInputStream("^gs(r2,r3(tw),gs)");
        PetroGrammarLexer lexer = new PetroGrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PetroGrammarParser parser = new PetroGrammarParser(tokens);
        ParseTree tree = parser.schichtbeschreibung();
        //System.out.println(tree.toStringTree(parser));

        PetroVisitor visitor = new PetroVisitor();
        System.out.println(visitor.visit(tree));
    }
}