package team.yi.tools.semanticcommit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class ReleaseCommitLocale implements Serializable {
    private static final long serialVersionUID = 1614994575461203609L;

    @EqualsAndHashCode.Include
    private final String commitHash;
    @EqualsAndHashCode.Include
    private final String lang;
    private final String commitType;
    private final String commitScope;
    private final String subject;

    public ReleaseCommitLocale(
        final String commitHash,
        final String lang,
        final String subject
    ) {
        this(commitHash, lang, null, null, subject);
    }

    public ReleaseCommitLocale(
        final String commitHash,
        final String lang,
        final String commitType,
        final String commitScope,
        final String subject
    ) {
        this.commitHash = commitHash;
        this.lang = lang;
        this.commitType = commitType;
        this.commitScope = commitScope;
        this.subject = subject;
    }
}
