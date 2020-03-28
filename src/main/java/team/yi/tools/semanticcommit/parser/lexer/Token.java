package team.yi.tools.semanticcommit.parser.lexer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Token implements Serializable {
    private static final long serialVersionUID = 1247962245504693908L;

    @ToString.Include
    private final TokenKind kind;

    @EqualsAndHashCode.Include
    @ToString.Include
    private final int line;

    @EqualsAndHashCode.Include
    @ToString.Include
    private final int column;

    @EqualsAndHashCode.Include
    @ToString.Include
    private final String value;

    private final int length;

    public Token(final TokenKind kind, final String value, final int line, final int column) {
        this.kind = kind;
        this.value = value;
        this.line = line;
        this.column = column;
        this.length = StringUtils.length(value);
    }
}
