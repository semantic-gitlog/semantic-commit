package team.yi.tools.semanticcommit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import team.yi.tools.semanticcommit.parser.ParserConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings("PMD.TooManyFields")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class ReleaseCommit extends GitCommit implements Serializable {
    private static final long serialVersionUID = 8295825971812538595L;

    private final Map<String, List<IssueRef>> quickActions = new ConcurrentHashMap<>();
    private final List<IssueRef> closeIssues = new ArrayList<>();
    private final List<IssueRef> subjectIssues = new ArrayList<>();
    private final List<IssueRef> bodyIssues = new ArrayList<>();
    private final List<MentionRef> mentions = new ArrayList<>();
    private final List<ReleaseCommitLocale> locales = new ArrayList<>();
    private final String defaultLang;

    @Setter
    private String commitUrl;
    @Setter
    @ToString.Include
    private String commitType;
    @Setter
    @ToString.Include
    private String commitPackage;
    @Setter
    @ToString.Include
    private String commitScope;
    @Setter
    @ToString.Include
    private String commitSubject;
    @Setter
    private String commitBody;
    @Setter
    private boolean attention;
    @Setter
    private boolean breakingChange;
    @Setter
    private boolean deprecated;

    public ReleaseCommit(final GitCommit gitCommit, final String defaultLang) {
        super(
            gitCommit.getHashFull(),
            gitCommit.getCommitTime(),
            gitCommit.getMessage(),
            gitCommit.isMerge(),
            gitCommit.getAuthorIdent(),
            gitCommit.getCommitterIdent()
        );

        this.defaultLang = StringUtils.defaultIfEmpty(defaultLang, ParserConstants.DEFAULT_LANG);
    }

    public IssueRef getCommitIssue() {
        return this.subjectIssues.isEmpty() ? null : this.subjectIssues.get(0);
    }

    @EqualsAndHashCode.Include
    @ToString.Include
    @Override
    public String getHashFull() {
        return super.getHashFull();
    }

    public void add(final String action, final IssueRef issueRef) {
        final String issueAction = StringUtils.lowerCase(action);

        if (this.quickActions.containsKey(issueAction)) {
            final List<IssueRef> issues = quickActions.get(issueAction);

            if (!issues.contains(issueRef)) issues.add(issueRef);
        } else {
            final List<IssueRef> issues = new ArrayList<>();
            issues.add(issueRef);

            quickActions.put(issueAction, issues);
        }
    }

    public boolean hasQuickActions() {
        return !this.quickActions.isEmpty();
    }

    public boolean hasSubjectIssues() {
        return !this.subjectIssues.isEmpty();
    }

    public boolean hasBodyIssues() {
        return !this.bodyIssues.isEmpty();
    }

    public boolean hasCloseIssues() {
        return !this.closeIssues.isEmpty();
    }

    public Map<String, ReleaseCommitLocale> getLocaleMap() {
        if (this.locales.isEmpty()) return null;

        return this.locales.stream()
            .collect(Collectors.toMap(ReleaseCommitLocale::getLang, locale -> locale, (a, b) -> b));
    }

    public String getFirstLocaleCommitType() {
        return this.locales.stream()
            .map(ReleaseCommitLocale::getCommitType)
            .filter(StringUtils::isNotEmpty)
            .findFirst()
            .orElse(null);
    }

    public String getFirstLocaleCommitScope() {
        return this.locales.stream()
            .map(ReleaseCommitLocale::getCommitScope)
            .filter(StringUtils::isNotEmpty)
            .findFirst()
            .orElse(null);
    }

    public String getRawCommitType() {
        return this.commitType;
    }

    public String getCommitType() {
        return StringUtils.defaultIfEmpty(this.getFirstLocaleCommitType(), this.commitType);
    }

    public String getRawCommitScope() {
        return this.commitScope;
    }

    public String getCommitScope() {
        return StringUtils.defaultIfEmpty(this.getFirstLocaleCommitScope(), this.commitScope);
    }

    public String getRawCommitSubject() {
        return this.commitSubject;
    }

    public String getCommitSubject() {
        return this.locales.stream()
            .filter(x -> this.defaultLang.equals(x.getLang()))
            .map(ReleaseCommitLocale::getSubject)
            .findFirst()
            .orElse(this.commitSubject);
    }
}
