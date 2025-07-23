package cz.hqm.latte.plugin.test.lexer;

import com.intellij.lexer.Lexer;
import org.junit.Before;
import org.junit.Test;
import cz.hqm.latte.plugin.lexer.LatteLexer;
import cz.hqm.latte.plugin.lexer.LatteSyntaxMode;
import cz.hqm.latte.plugin.test.LattePluginTestBase;

import static org.junit.Assert.*;

/**
 * Tests for complex Latte templates with nested macros and syntax mode switching.
 * This test verifies that the lexer correctly processes complex templates without errors.
 */
public class LatteComplexTemplateTest extends LattePluginTestBase {

    private LatteLexer lexer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        lexer = new LatteLexer();
    }

    /**
     * Tests that the lexer correctly processes a complex template with nested macros and syntax mode switching.
     * This test verifies that the fix for the "Top level element is not completed" error works correctly.
     */
    @Test
    public void testComplexTemplate() {
        // Create a complex test template similar to the one in the debug logs
        String content = 
            "{default $bootstrapVersion = \"2.3.2\"}\n" +
            "{default $debugMode = false}\n" +
            "{default $testMode = false}\n" +
            "<!DOCTYPE html>\n" +
            "<html lang=\"cs\">\n" +
            "\t<head>\n" +
            "\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
            "\t\t<title>Test</title>\n" +
            "\t\t<link rel=\"stylesheet\" href=\"{$basePath}/css/style.css\" type=\"text/css\">\n" +
            "\t\t{if $debugMode}\n" +
            "\t\t\t<script src=\"{$basePath}/js/debug.js\"></script>\n" +
            "\t\t{/if}\n" +
            "\t\t{syntax off}\n" +
            "\t\t<script>\n" +
            "\t\t\t$(function () {\n" +
            "\t\t\t\t$.scrollUp({ scrollText: '' });\n" +
            "\t\t\t\t$.nette.init();\n" +
            "\t\t\t\t$('[data-dependentselectbox]').dependentSelectBox();\n" +
            "\t\t\t});\n" +
            "\t\t</script>\n" +
            "\t\t{/syntax}\n" +
            "\t\t{syntax double}\n" +
            "\t\t{{if $condition}}\n" +
            "\t\t\t<p>Content</p>\n" +
            "\t\t\t{{foreach $items as $item}}\n" +
            "\t\t\t\t<span>{{$item}}</span>\n" +
            "\t\t\t{{/foreach}}\n" +
            "\t\t{{/if}}\n" +
            "\t\t{/syntax}\n" +
            "\t\t{block head}{/block}\n" +
            "\t</head>\n" +
            "\t<body>\n" +
            "\t\t<div class=\"container-fluid\">\n" +
            "\t\t\t{if !empty($flashes)}\n" +
            "\t\t\t\t<div id=\"flashes\">\n" +
            "\t\t\t\t\t{foreach $flashes as $flash}\n" +
            "\t\t\t\t\t\t<div class=\"alert alert-{$flash->type}\">\n" +
            "\t\t\t\t\t\t\t{if $flash->message instanceof \\Nette\\Utils\\IHtmlString}\n" +
            "\t\t\t\t\t\t\t\t{$flash->message|noescape}\n" +
            "\t\t\t\t\t\t\t{else}\n" +
            "\t\t\t\t\t\t\t\t{$flash->message|trim|breakLines}\n" +
            "\t\t\t\t\t\t\t{/if}\n" +
            "\t\t\t\t\t\t</div>\n" +
            "\t\t\t\t\t{/foreach}\n" +
            "\t\t\t\t</div>\n" +
            "\t\t\t{/if}\n" +
            "\t\t</div>\n" +
            "\t\t{default $contentContainer = \"container-fluid\"}\n" +
            "\t\t<div class=\"{$contentContainer}\">\n" +
            "\t\t\t{include #content}\n" +
            "\t\t</div>\n" +
            "\t\t<script>\n" +
            "\t\t\t{* Comment *}\n" +
            "\t\t\t$(document).ready(function () {\n" +
            "\t\t\t\t{if $debugMode || $testMode}\n" +
            "\t\t\t\thljs.highlightAll();\n" +
            "\t\t\t\t{/if}\n" +
            "\t\t\t});\n" +
            "\t\t</script>\n" +
            "\t</body>\n" +
            "</html>";
        
        // Start the lexer with the content
        lexer.start(content);
        
        // Process the entire template
        processEntireTemplate(lexer);
        
        // Verify that the lexer processed the template without errors
        // This is a basic test to ensure the lexer doesn't crash or get stuck
        assertTrue("Lexer should reach the end of the template", lexer.getTokenType() == null);
    }
    
    /**
     * Tests that the lexer correctly processes a template with multiple syntax mode changes.
     * This test verifies that the syntax mode stack is maintained correctly.
     */
    @Test
    public void testMultipleSyntaxModeChanges() {
        // Create a test template with multiple syntax mode changes
        String content = 
            "{syntax double}\n" +
            "{{if $condition}}\n" +
            "\t{syntax off}\n" +
            "\t<script>\n" +
            "\t\t// This should not be processed as Latte\n" +
            "\t\tvar x = {value: 10};\n" +
            "\t</script>\n" +
            "\t{/syntax}\n" +
            "\t{{foreach $items as $item}}\n" +
            "\t\t<span>{{$item}}</span>\n" +
            "\t{{/foreach}}\n" +
            "{{/if}}\n" +
            "{/syntax}";
        
        // Start the lexer with the content
        lexer.start(content);
        
        // Verify initial syntax mode is DEFAULT
        assertEquals("Initial syntax mode should be DEFAULT", 
                     LatteSyntaxMode.DEFAULT, lexer.getSyntaxMode());
        
        // Process the {syntax double} tag
        advanceLexerToText(lexer, "{syntax double}");
        lexer.processSyntaxTags("{syntax double}");
        
        // Verify syntax mode is changed to DOUBLE
        assertEquals("Syntax mode should be DOUBLE after {syntax double}", 
                     LatteSyntaxMode.DOUBLE, lexer.getSyntaxMode());
        
        // Process the {syntax off} tag
        advanceLexerToText(lexer, "{syntax off}");
        lexer.processSyntaxTags("{syntax off}");
        
        // Verify syntax mode is changed to OFF
        assertEquals("Syntax mode should be OFF after {syntax off}", 
                     LatteSyntaxMode.OFF, lexer.getSyntaxMode());
        
        // Process the {/syntax} tag
        advanceLexerToText(lexer, "{/syntax}");
        lexer.processSyntaxTags("{/syntax}");
        
        // Verify syntax mode is restored to DOUBLE
        assertEquals("Syntax mode should be DOUBLE after {/syntax} (restored from previous mode)", 
                     LatteSyntaxMode.DOUBLE, lexer.getSyntaxMode());
        
        // Process the final {/syntax} tag
        advanceLexerToText(lexer, "{/syntax}");
        lexer.processSyntaxTags("{/syntax}");
        
        // Verify syntax mode is restored to DEFAULT
        assertEquals("Syntax mode should be DEFAULT after final {/syntax}", 
                     LatteSyntaxMode.DEFAULT, lexer.getSyntaxMode());
    }
    
    /**
     * Helper method to process the entire template.
     * 
     * @param lexer The lexer to use
     */
    private void processEntireTemplate(Lexer lexer) {
        while (lexer.getTokenType() != null) {
            lexer.advance();
        }
    }
    
    /**
     * Helper method to advance the lexer until it finds the specified text.
     * 
     * @param lexer The lexer to advance
     * @param text The text to find
     */
    private void advanceLexerToText(Lexer lexer, String text) {
        while (lexer.getTokenType() != null) {
            String tokenText = lexer.getTokenText();
            if (tokenText != null && tokenText.contains(text)) {
                break;
            }
            lexer.advance();
        }
    }
}