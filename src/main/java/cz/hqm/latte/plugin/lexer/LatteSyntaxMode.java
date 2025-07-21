package cz.hqm.latte.plugin.lexer;

/**
 * Enum representing the different syntax modes available in Latte templates.
 * These modes affect how the lexer processes the template content.
 */
public enum LatteSyntaxMode {
    /**
     * Default syntax mode. Macros are delimited by single braces: {macro}
     */
    DEFAULT,
    
    /**
     * Double syntax mode. Macros are delimited by double braces: {{macro}}
     */
    DOUBLE,
    
    /**
     * Off syntax mode. Latte syntax processing is disabled until {/syntax} is encountered.
     */
    OFF
}