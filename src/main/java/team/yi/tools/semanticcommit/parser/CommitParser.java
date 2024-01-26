package team.yi.tools.semanticcommit.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import team.yi.tools.semanticcommit.CommitUtils;
import team.yi.tools.semanticcommit.model.GitCommit;
import team.yi.tools.semanticcommit.model.IssueRef;
import team.yi.tools.semanticcommit.model.MentionRef;
import team.yi.tools.semanticcommit.model.ReleaseCommit;
import team.yi.tools.semanticcommit.model.ReleaseCommitLocale;
import team.yi.tools.semanticcommit.parser.lexer.CommitLexer;
import team.yi.tools.semanticcommit.parser.lexer.LexerConstants;
import team.yi.tools.semanticcommit.parser.lexer.Token;
import team.yi.tools.semanticcommit.parser.lexer.TokenKind;

import java.util.List;
import java.util.Locale;

@Slf4j
public class CommitParser extends Parser<ReleaseCommit, CommitLexer> {
    private final CommitParserSettings settings;
    private final GitCommit gitCommit;
    private final List<String> closeIssueActions;
    private ReleaseCommit releaseCommit;

    public CommitParser(final CommitParserSettings settings, final GitCommit gitCommit) {
        super(new CommitLexer(gitCommit.getMessage(), settings.getCloseIssueActions()));

        this.settings = settings;
        this.gitCommit = gitCommit;
        this.closeIssueActions = settings.getCloseIssueActions();
    }

    @Override
    public void reset() {
        super.reset();

        this.releaseCommit = new ReleaseCommit(this.gitCommit, this.settings.getDefaultLang());

        final String commitUrl = CommitUtils.createCommitUrl(this.settings.getCommitUrlTemplate(), this.releaseCommit.getHashFull());

        this.releaseCommit.setCommitUrl(commitUrl);
    }

    @Override
    public ReleaseCommit parse() {
        this.reset();
        this.consume();

        while (this.current.getKind() != TokenKind.eof) {
            switch (this.current.getKind()) {
                case type:
                    this.releaseCommit.setCommitType(this.current.getValue());
                    break;
                case attention:
                    this.releaseCommit.setAttention("!".equals(this.current.getValue()));
                    break;
                case scope:
                    this.readScope();
                    break;
                case subject:
                    this.readSubject();
                    break;
                case body:
                    this.readBody();
                    break;
                case sectionBoundary:
                    this.readSection();
                    break;
                default:
                    break;
            }

            this.consume();
        }

        return this.releaseCommit;
    }

    private IssueRef readIssue(final boolean forSubject) {
        this.consume(TokenKind.issueStart);
        this.consume(TokenKind.text);

        final Integer issueId = Integer.valueOf(this.current.getValue());
        final String url = CommitUtils.createIssueUrl(this.settings.getIssueUrlTemplate(), issueId);
        final IssueRef issueRef = new IssueRef(issueId, url);

        if (forSubject) {
            this.releaseCommit.getSubjectIssues().add(issueRef);
        } else {
            this.releaseCommit.getBodyIssues().add(issueRef);
        }

        return issueRef;
    }

    private IssueRef readIssueAction() {
        final String action = StringUtils.stripStart(this.current.getValue(), "/");

        this.consume(TokenKind.issueAction);

        final String repo = StringUtils.stripToNull(this.current.getValue());

        this.consume(TokenKind.issueRepo);
        this.consume(TokenKind.issueStart);
        this.consume(TokenKind.text);

        final Integer issueId = Integer.valueOf(this.current.getValue());
        final String url = CommitUtils.createIssueUrl(this.settings.getIssueUrlTemplate(), issueId);
        final IssueRef issueRef = new IssueRef(repo, issueId, url, action);

        this.releaseCommit.add(action, issueRef);

        if (this.closeIssueActions.contains(action.toLowerCase(Locale.getDefault()))) {
            releaseCommit.getCloseIssues().add(issueRef);
        }

        return issueRef;
    }

    private MentionRef readMentionRef() {
        this.consume(TokenKind.mentionStart);

        final String username = this.current.getValue();
        final String url = CommitUtils.createMentionUrl(this.settings.getMentionUrlTemplate(), username);
        final MentionRef mentionRef = new MentionRef(username, url);

        this.releaseCommit.getMentions().add(mentionRef);

        this.consume(TokenKind.mention);

        return mentionRef;
    }

    private void readLocales() {
        this.consume();

        if (this.current.getKind() != TokenKind.localeItemStart) return;

        String lang = null;
        String subject = null;

        while (this.current.getKind() != TokenKind.sectionBoundary && this.current.getKind() != TokenKind.eof) {
            this.consume();

            switch (this.current.getKind()) {
                case localeLang:
                    lang = this.current.getValue();
                    break;
                case localeSubject:
                    subject = this.current.getValue();
                    break;
                case localeItemEnd:
                case eof:
                    if (StringUtils.isEmpty(lang)) break;

                    final ReleaseCommitLocale locale = new ReleaseCommitLocale(this.gitCommit.getHashFull(), lang, subject);

                    this.releaseCommit.getLocales().add(locale);

                    lang = StringUtils.EMPTY;
                    subject = StringUtils.EMPTY;
                    break;
                default:
                    break;
            }
        }
    }

    private void readSection() {
        this.consume();

        if (this.current.getKind() == TokenKind.localeListHeader) {
            this.readLocales();
        }
    }

    private void readBody() {
        final StringBuilder builder = new StringBuilder("\n\n");

        while (this.current.getKind() != TokenKind.bodyEnd && this.current.getKind() != TokenKind.eof) {
            switch (this.current.getKind()) {
                case issueEnd:
                case body:
                    builder.append(this.current.getValue());
                    break;
                case mentionStart:
                    final MentionRef mentionRef = this.readMentionRef();

                    builder.append(LexerConstants.AT).append(mentionRef.getUsername());
                    break;
                case issueAction: {
                    final IssueRef issueRef = this.readIssueAction();

                    builder.append(issueRef.getAction()).append(LexerConstants.SPACE);

                    if (StringUtils.isNotEmpty(issueRef.getRepo())) {
                        builder.append(issueRef.getRepo()).append(LexerConstants.SLASH);
                    }

                    builder.append(LexerConstants.SHARP).append(issueRef.getId());
                    break;
                }
                case issueStart: {
                    final String issueStart = StringUtils.prependIfMissing(this.current.getValue(), StringUtils.SPACE);
                    final IssueRef issueRef = this.readIssue(false);

                    builder.append(issueStart)
                        .append(LexerConstants.SHARP)
                        .append(issueRef.getId());
                    break;
                }
                // case mentionEnd:
                // case bodyEnd:
                default:
                    break;
            }

            this.consume();
        }

        this.releaseCommit.setCommitBody(builder.toString().trim());

        final String[] lines = StringUtils.split(this.releaseCommit.getCommitBody(), "\r\n");

        for (final String line : lines) {
            if (!this.releaseCommit.isBreakingChange() && StringUtils.startsWith(line, ParserConstants.BREAKING_CHANGE_PATTERN)) {
                this.releaseCommit.setBreakingChange(true);
            }

            if (!this.releaseCommit.isDeprecated() && StringUtils.startsWith(line, ParserConstants.DEPRECATED_PATTERN)) {
                this.releaseCommit.setDeprecated(true);
            }
        }
    }

    private void readSubject() {
        final StringBuilder builder = new StringBuilder();

        while (this.current.getKind() != TokenKind.subjectEnd && this.current.getKind() != TokenKind.eof) {
            switch (this.current.getKind()) {
                case issueEnd:
                case subject:
                    builder.append(this.current.getValue());
                    break;
                case mentionStart:
                    final MentionRef mentionRef = this.readMentionRef();

                    builder.append(LexerConstants.AT).append(mentionRef.getUsername());
                    break;
                case issueStart:
                    final String issueStart = StringUtils.prependIfMissing(this.current.getValue(), StringUtils.SPACE);
                    final IssueRef issueRef = this.readIssue(true);

                    builder.append(issueStart).append(LexerConstants.SHARP).append(issueRef.getId());
                    break;
                // case mentionEnd:
                case subjectEnd:
                default:
                    break;
            }

            this.consume();
        }

        this.releaseCommit.setCommitSubject(builder.toString());
    }

    private void readScope() {
        final Token token = this.consume(TokenKind.scope);
        final int pos = token.getValue().lastIndexOf(LexerConstants.SLASH);

        if (pos > -1) {
            final String packageName = token.getValue().substring(0, pos + 1);
            final String scope = token.getValue().substring(pos + 1);

            this.releaseCommit.setCommitPackage(packageName);
            this.releaseCommit.setCommitScope(scope);
        } else {
            this.releaseCommit.setCommitScope(token.getValue());
        }
    }
}
