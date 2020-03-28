package team.yi.tools.semanticcommit;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import team.yi.tools.semanticcommit.parser.ParserConstants;

@UtilityClass
public class CommitUtils {
    public String createIssueUrl(final String issueUrlTemplate, final Integer issueId) {
        if (StringUtils.isEmpty(issueUrlTemplate)) return null;

        return issueUrlTemplate.replaceAll(ParserConstants.ISSUE_ID_PLACEHOLDER, String.valueOf(issueId));
    }

    public String createCommitUrl(final String commitUrlTemplate, final String commitId) {
        if (StringUtils.isEmpty(commitUrlTemplate)) return null;

        return commitUrlTemplate.replaceAll(ParserConstants.COMMIT_ID_PLACEHOLDER, commitId);
    }

    public String createMentionUrl(final String mentionUrlTemplate, final String username) {
        if (StringUtils.isEmpty(mentionUrlTemplate)) return null;

        return mentionUrlTemplate.replaceAll(ParserConstants.MENTION_USERNAME_PLACEHOLDER, username);
    }
}
