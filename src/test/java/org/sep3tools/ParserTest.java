package org.sep3tools;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.sep3tools.gen.PetroGrammarLexer;
import org.sep3tools.gen.PetroGrammarParser;

public class ParserTest {

    @Test
    public void verifyThat() {
        CharStream input = CharStreams.fromString("^ms(r2,r3(tw),gs(lw,r2-r3)),^u(t,lw),^gs(r3,bei(113),nf?)");
        PetroGrammarLexer lexer = new PetroGrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PetroGrammarParser parser = new PetroGrammarParser(tokens);
        ParseTree tree = parser.schichtbeschreibung();
        assertThat(tree, is(notNullValue()));
    }
    
}
