package team.yi.tools.semanticcommit.parser;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Data
@SuperBuilder
public class CommitParserSettings implements Serializable {
    private static final long serialVersionUID = -6597959565512195022L;

    private String defaultLang;
    private String closeIssueActions;
    private String issueUrlTemplate;
    private String commitUrlTemplate;
    private String mentionUrlTemplate;

    public String getDefaultLang() {
        return StringUtils.defaultIfBlank(this.defaultLang, ParserConstants.DEFAULT_LANG);
    }

    public List<String> getCloseIssueActions() {
        final String data = StringUtils.defaultIfBlank(this.closeIssueActions, ParserConstants.DEFAULT_CLOSE_ISSUE_ACTIONS);
        final String[] items = StringUtils.splitPreserveAllTokens(data.toLowerCase(Locale.getDefault()), ",|;");

        return Arrays.asList(items);
    }
}
