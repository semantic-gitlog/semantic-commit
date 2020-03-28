package team.yi.tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import team.yi.tools.semanticcommit.model.ReleaseCommitLocale;
import team.yi.tools.semanticcommit.parser.CommitLocaleParser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class GitCommitLocaleParserTests {
    @Test
    public void parseTest() throws IOException, URISyntaxException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final URL resource = classLoader.getResource("commit-locales.zh-cn.md");

        if (resource == null) return;

        final Path path = Paths.get(resource.toURI());
        final File file = path.toFile();
        final CommitLocaleParser parser = new CommitLocaleParser("zh-cn", file);

        final List<ReleaseCommitLocale> commitLocales = parser.parse();

        assertNotNull(commitLocales, "null");

        log.info(String.valueOf(commitLocales.size()));
    }
}
