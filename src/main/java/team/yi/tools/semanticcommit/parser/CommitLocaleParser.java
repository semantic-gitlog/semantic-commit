package team.yi.tools.semanticcommit.parser;

import org.apache.commons.lang3.StringUtils;
import team.yi.tools.semanticcommit.model.ReleaseCommitLocale;
import team.yi.tools.semanticcommit.parser.lexer.CommitLocaleLexer;
import team.yi.tools.semanticcommit.parser.lexer.LexerConstants;
import team.yi.tools.semanticcommit.parser.lexer.TokenKind;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommitLocaleParser extends Parser<List<ReleaseCommitLocale>, CommitLocaleLexer> {
    private final String lang;
    private List<ReleaseCommitLocale> commitLocales;

    public CommitLocaleParser(final String lang, final File file) throws IOException {
        super(new CommitLocaleLexer(file));

        this.lang = lang;
    }

    @Override
    public void reset() {
        super.reset();

        this.commitLocales = new ArrayList<>();
    }

    @Override
    public List<ReleaseCommitLocale> parse() {
        this.reset();
        this.consume();

        while (TokenKind.eof != this.current.getKind()) {
            if (TokenKind.localeItemStart == this.current.getKind()) {
                this.readLocale();
            }

            this.consume();
        }

        return this.commitLocales;
    }

    private void readLocale() {
        String commitHash = null;
        String commitType = null;
        String commitScope = null;
        String subject = null;

        while (TokenKind.eof != this.current.getKind() && TokenKind.localeItemEnd != this.current.getKind()) {
            switch (this.current.getKind()) {
                case commitHash:
                    commitHash = StringUtils.trimToNull(this.current.getValue());
                    break;
                case localeCommitType:
                    commitType = StringUtils.trimToNull(this.current.getValue());

                    if (StringUtils.endsWith(commitType, ")")) {
                        final int offset = commitType.indexOf(LexerConstants.OPEN_BRACKET);

                        if (offset > 0) {
                            commitScope = commitType.substring(offset + 1, commitType.length() - 1);
                            commitType = commitType.substring(0, offset);
                        }
                    }

                    break;
                case localeSubject:
                    subject = StringUtils.trimToNull(this.current.getValue());
                    break;
                default:
                    break;
            }

            this.consume();
        }

        if (StringUtils.isEmpty(commitHash)) return;

        final ReleaseCommitLocale commitLocale = new ReleaseCommitLocale(commitHash, this.lang, commitType, commitScope, subject);

        this.commitLocales.add(commitLocale);
    }
}
