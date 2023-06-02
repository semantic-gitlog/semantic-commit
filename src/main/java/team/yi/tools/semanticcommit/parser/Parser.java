package team.yi.tools.semanticcommit.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import team.yi.tools.semanticcommit.parser.lexer.Lexer;
import team.yi.tools.semanticcommit.parser.lexer.Token;
import team.yi.tools.semanticcommit.parser.lexer.TokenKind;

import java.text.MessageFormat;
import java.util.Objects;

@Slf4j
public abstract class Parser<T, L extends Lexer> {
    protected final L lexer;

    protected Token current;

    protected Parser(final L lexer) {
        this.lexer = Objects.requireNonNull(lexer);
    }

    public void parseTokens() {
        this.reset();

        Token token = this.lexer.next();

        while (token.getKind() != TokenKind.eof) {
            String value = StringUtils.replace(token.getValue(), "\r", "\\r");
            value = StringUtils.replace(value, "\n", "\\n");

            log.info(MessageFormat.format("{0}: {1}", StringUtils.leftPad(token.getKind().name(), 20), value));

            token = this.lexer.next();
        }

        log.info(MessageFormat.format("{0}: {1}", StringUtils.leftPad(token.getKind().name(), 20), token.getValue()));
    }

    public void reset() {
        this.lexer.reset();
    }

    public abstract T parse();

    @SuppressWarnings("UnusedReturnValue")
    protected Token consume() {
        final Token old = this.current;

        this.current = this.lexer.next();

        return old;
    }

    protected Token consume(final TokenKind kind) {
        final Token old = this.current;

        this.current = this.lexer.next();

        if (old.getKind() != kind) {
            final String message = MessageFormat.format("Invalid token：{0}, kind：{1}.", this.current.getKind(), kind);

            throw new ParseException(this.current.getLine(), this.current.getColumn(), message);
        }

        return old;
    }
}
