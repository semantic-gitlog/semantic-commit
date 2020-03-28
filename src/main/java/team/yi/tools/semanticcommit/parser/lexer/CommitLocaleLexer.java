package team.yi.tools.semanticcommit.parser.lexer;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

public class CommitLocaleLexer extends Lexer {
    public CommitLocaleLexer(final Path path) throws IOException {
        super(path);
    }

    public CommitLocaleLexer(final File file) throws IOException {
        super(file);
    }

    public CommitLocaleLexer(final File file, final Charset charset) throws IOException {
        super(file, charset);
    }

    public CommitLocaleLexer(final String contents) throws IOException {
        super(contents);
    }

    @Override
    public Token next() {
        return this.nextLocale();
    }

    @Override
    public void reset() {
        super.reset();

        this.currentMode = LexerMode.record;
    }

    private Token nextLocale() {
        this.startRead();

        outer:
        while (true) {
            final Character ch = this.la(0);
            final Character ch1 = this.la(1);
            final Character ch2 = this.la(2);

            switch (ch) {
                case LexerConstants.EOF:
                    if (this.savedPos == this.position) return this.createToken(TokenKind.eof);

                    break outer;

                case CharUtils.CR:
                case CharUtils.LF:
                    this.readWhitespace();

                    return this.createToken(TokenKind.localeItemEnd);

                case LexerConstants.TAB:
                case LexerConstants.SPACE:
                    final String spaces = this.pickWhitespace(0);
                    final String type = this.pickWord(spaces.length());
                    final String typeColon = String.valueOf(LexerConstants.COLON);

                    if (StringUtils.isNotEmpty(type) && StringUtils.endsWith(type, typeColon)) {
                        this.consume(spaces.length() + type.length());

                        return this.createToken(TokenKind.localeCommitType, StringUtils.stripEnd(type, typeColon));
                    }

                    this.consume();
                    break;

                case LexerConstants.HYPHEN:
                    if (LexerConstants.TAB == ch1 || LexerConstants.SPACE == ch1) {
                        this.consume();
                        this.readWhitespace();

                        return this.createToken(TokenKind.localeItemStart, LexerConstants.HYPHEN);
                    }

                    this.consume();
                    break;

                case LexerConstants.OPENING_BRACKET: {
                    final String commitHash = this.pickTo(1, LexerConstants.CLOSING_BRACKET);

                    if (StringUtils.isNotEmpty(commitHash)) {
                        this.consume(commitHash.length() + 2);

                        return this.createToken(TokenKind.commitHash, commitHash);
                    }

                    this.consume();
                    break;
                }
                case LexerConstants.ASTERISK: {
                    if (LexerConstants.ASTERISK == ch1 && LexerConstants.OPENING_BRACKET == ch2) {
                        final String commitHash = this.pickTo(3, LexerConstants.CLOSING_BRACKET);

                        if (StringUtils.isNotEmpty(commitHash)) {
                            this.consume(commitHash.length() + 6);

                            return this.createToken(TokenKind.commitHash, commitHash);
                        }
                    }

                    this.consume();
                    break;
                }
                default:
                    if (CharUtils.CR == ch1 || CharUtils.LF == ch1) {
                        this.consume();

                        break outer;
                    }

                    this.consume();

                    break;
            }
        }

        return this.createToken(TokenKind.localeSubject);
    }
}
