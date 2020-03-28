package team.yi.tools.semanticcommit.parser.lexer;

public enum TokenKind {
    eof,

    text,
    attention,
    numberInteger,
    numberDouble,

    looped,
    word,

    type,

    scopeStart,
    scope,
    scopeEnd,

    bodyStart,
    body,
    bodyEnd,

    sectionBoundary,

    // # Locales
    localeListHeader,
    localeItemStart,
    localeLang,
    localeCommitType,
    localeSubject,
    localeItemEnd,

    subjectStart,
    subject,
    subjectEnd,

    commitHash,

    // ` #123` `(#123)`
    issueStart,
    issueEnd,
    issueAction,
    issueRepo,

    // ` @ymind `
    mentionStart,
    mention,
    mentionEnd
}
