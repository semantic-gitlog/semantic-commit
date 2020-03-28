package team.yi.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import team.yi.tools.semanticcommit.model.GitCommit;
import team.yi.tools.semanticcommit.model.GitDate;
import team.yi.tools.semanticcommit.model.GitPersonIdent;
import team.yi.tools.semanticcommit.model.ReleaseCommit;
import team.yi.tools.semanticcommit.parser.CommitParser;
import team.yi.tools.semanticcommit.parser.CommitParserSettings;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class GitCommitParserTests {
    private CommitParser parser;

    @BeforeEach
    public void init() throws URISyntaxException, ParseException, IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final URL resource = classLoader.getResource("commit-message.md");

        if (resource == null) return;

        final Path path = Paths.get(resource.toURI());

        final Date date = DateUtils.parseDate("2020-02-10 12:23:45", "yyyy-MM-dd HH:mm:ss");
        final GitDate commitTime = GitDate.valueOf(date);
        final GitPersonIdent authorIdent = new GitPersonIdent(commitTime, "ymind", "ymind.chan@yi.team");
        final GitPersonIdent committerIdent = new GitPersonIdent(commitTime, "ymind", "ymind.chan@yi.team");
        final String hashFull = "02ce19bbbf7058f474f760fe4a4447301190dea9";
        final String message = new String(Files.readAllBytes(path), UTF_8).trim();
        final Boolean isMerge = false;

        final GitCommit gitCommit = new GitCommit(hashFull, commitTime, message, isMerge, authorIdent, committerIdent);
        final CommitParserSettings settings = CommitParserSettings.builder().build();

        this.parser = new CommitParser(settings, gitCommit);
    }

    @Test
    public void parseTokensTest() {
        assertNotNull(this.parser, "null");

        parser.parseTokens();
    }

    @Test
    public void parseTest() {
        final ReleaseCommit releaseCommit = this.parser.parse();

        assertNotNull(releaseCommit, "null");

        log.info(releaseCommit.getMessageTitle());
    }
}
