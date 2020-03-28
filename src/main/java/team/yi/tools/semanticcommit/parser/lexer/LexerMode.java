package team.yi.tools.semanticcommit.parser.lexer;

public enum LexerMode {
    text,
    section,

    record,

    scope,
    subject,
    body,

    issueRef,
    actionIssueRef,
    mentionRef
}
