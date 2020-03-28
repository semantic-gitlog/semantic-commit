package team.yi.tools.semanticcommit.parser.lexer;

import org.apache.commons.lang3.CharUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import static team.yi.tools.semanticcommit.parser.lexer.LexerMode.text;

@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class ScopeProfileLexer extends Lexer {
    public ScopeProfileLexer(final Path path) throws IOException {
        super(path);
    }

    public ScopeProfileLexer(final File file) throws IOException {
        super(file);
    }

    public ScopeProfileLexer(final File file, final Charset charset) throws IOException {
        super(file, charset);
    }

    public ScopeProfileLexer(final String contents) throws IOException {
        super(contents);
    }

    @Override
    public Token next() {
        switch (this.currentMode) {
            case record:
                return this.nextRecord();
            case scope:
                return this.nextScope();
            case subject:
                return this.nextSubject();
            case body:
                return this.nextBody();
            default:
                return this.nextText();
        }
    }

    @Override
    public void reset() {
        super.reset();

        this.currentMode = text;
    }

    private Token nextBody() {
        this.startRead();

        outer:
        while (true) {
            final Character ch = this.la(0);

            switch (ch) {
                case LexerConstants.EOF:
                    if (this.savedPos == this.position) return this.createToken(TokenKind.eof);

                    break outer;

                case CharUtils.LF:
                case CharUtils.CR:
                    this.leaveMode();

                    break outer;

                default:
                    this.consume();

                    break;
            }
        }

        return this.createToken(TokenKind.body);
    }

    private Token nextSubject() {
        this.startRead();

        outer:
        while (true) {
            final Character ch = this.la(0);

            switch (ch) {
                case LexerConstants.EOF:
                    if (this.savedPos == this.position) return this.createToken(TokenKind.eof);

                    break outer;

                case CharUtils.LF:
                case CharUtils.CR:
                    this.leaveMode();

                    break outer;

                case LexerConstants.OPEN_BRACKET:
                    this.consume();

                    return this.createToken(TokenKind.subjectStart);

                case LexerConstants.CLOSE_BRACKET:
                    if (this.savedPos == this.position) {
                        this.consume();
                        this.leaveMode();

                        return this.createToken(TokenKind.subjectEnd);
                    }

                    break outer;

                default:
                    this.consume();

                    break;
            }
        }

        return this.createToken(TokenKind.subject);
    }

    private Token nextScope() {
        this.startRead();

        outer:
        while (true) {
            final Character ch = this.la(0);

            switch (ch) {
                case LexerConstants.EOF:
                    if (this.savedPos == this.position) return this.createToken(TokenKind.eof);

                    break outer;

                case CharUtils.LF:
                case CharUtils.CR:
                    this.leaveMode();

                    break outer;

                case LexerConstants.OPEN_BRACKET:
                    this.enterMode(LexerMode.subject);

                    break outer;

                case LexerConstants.ASTERISK:
                    final Character ch1 = this.la(1);

                    if (LexerConstants.ASTERISK == ch1) {
                        this.consume(2);
                        this.leaveMode();

                        return this.createToken(TokenKind.scopeEnd);
                    }

                    this.consume();
                    break;

                default:
                    this.consume();

                    break;
            }
        }

        return this.createToken(TokenKind.scope);
    }

    private Token nextRecord() {
        this.startRead();

        outer:
        while (true) {
            final Character ch = this.la(0);
            final Character ch1 = this.la(1);

            switch (ch) {
                case LexerConstants.EOF:
                    if (this.savedPos == this.position) return this.createToken(TokenKind.eof);

                    break outer;

                case CharUtils.LF:
                case CharUtils.CR:
                    this.readWhitespace();
                    this.leaveMode();

                    return this.createToken(TokenKind.bodyEnd);

                case LexerConstants.SPACE:
                case LexerConstants.TAB:
                case LexerConstants.COLON:
                    this.consume();
                    this.readWhitespace();
                    this.enterMode(LexerMode.body);

                    break outer;

                case LexerConstants.ASTERISK:
                    if (this.column == 3) {
                        final Character ch2 = this.la(2);

                        if (LexerConstants.ASTERISK == ch1 && !Character.isWhitespace(ch2)) {
                            this.consume(2);
                            this.enterMode(LexerMode.scope);

                            return this.createToken(TokenKind.scopeStart);
                        }
                    }

                    this.consume();
                    break;

                default:
                    this.consume();

                    break;
            }
        }

        return this.createToken(TokenKind.text);
    }

    private Token nextText() {
        this.startRead();

        outer:
        while (true) {
            final Character ch = this.la(0);

            switch (ch) {
                case LexerConstants.EOF:
                    if (this.savedPos == this.position) return this.createToken(TokenKind.eof);

                    break outer;

                case CharUtils.LF:
                case CharUtils.CR:
                    this.readWhitespace();

                    break;

                case LexerConstants.HYPHEN:
                    if (this.column == 1) {
                        final Character ch1 = this.la(1);
                        final Character ch2 = this.la(2);
                        final Character ch3 = this.la(3);
                        final Character ch4 = this.la(4);

                        if (LexerConstants.SPACE == ch1
                            && LexerConstants.ASTERISK == ch2
                            && LexerConstants.ASTERISK == ch3
                            && !Character.isWhitespace(ch4)
                        ) {
                            this.consume(2);
                            this.enterMode(LexerMode.record);

                            break outer;
                        }
                    }

                    this.consume();
                    break;

                default:
                    this.consume();

                    break;
            }
        }

        return this.createToken(TokenKind.text);
    }
}
