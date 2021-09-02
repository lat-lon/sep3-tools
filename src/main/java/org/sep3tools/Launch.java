package org.sep3tools;

import org.sep3tools.gen.*;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Launch {

    public static void main(String[] args)  {
        ANTLRInputStream input = new ANTLRInputStream("^ms(r2,r3(tw),gs(lw,r2-r3)),^u(t,lw),^gs(r3,bei(113),nf?)");
        //ANTLRInputStream input = new ANTLRInputStream("X");
        PetroGrammarLexer lexer = new PetroGrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PetroGrammarParser parser = new PetroGrammarParser(tokens);
        ParseTree tree = parser.schichtbeschreibung();
        //System.out.println(tree.toStringTree(parser));

        PetroVisitor visitor = new PetroVisitor();
        System.out.println(visitor.visit(tree));
    }

    public static String parseS3 (String s3String)  {

        ANTLRInputStream input = new ANTLRInputStream(s3String);
        PetroGrammarLexer lexer = new PetroGrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PetroGrammarParser parser = new PetroGrammarParser(tokens);
        ParseTree tree = parser.schichtbeschreibung();
        //System.out.println(tree.toStringTree(parser));

        PetroVisitor visitor = new PetroVisitor();
        return (visitor.visit(tree));
    }


}