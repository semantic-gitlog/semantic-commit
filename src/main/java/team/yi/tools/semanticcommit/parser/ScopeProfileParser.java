package team.yi.tools.semanticcommit.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import team.yi.tools.semanticcommit.model.ScopeProfile;
import team.yi.tools.semanticcommit.parser.lexer.ScopeProfileLexer;
import team.yi.tools.semanticcommit.parser.lexer.TokenKind;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ScopeProfileParser extends Parser<List<ScopeProfile>, ScopeProfileLexer> {
    private final String lang;
    private List<ScopeProfile> scopeProfiles;

    public ScopeProfileParser(final String lang, final File file) throws IOException {
        super(new ScopeProfileLexer(file));

        this.lang = lang;
    }

    @Override
    public void reset() {
        super.reset();

        this.scopeProfiles = new ArrayList<>();
    }

    @Override
    public List<ScopeProfile> parse() {
        this.reset();
        this.consume();

        while (TokenKind.eof != this.current.getKind()) {
            if (TokenKind.scopeStart == this.current.getKind()) {
                this.readScopeProfile();
            }

            this.consume();
        }

        return this.scopeProfiles;
    }

    private void readScopeProfile() {
        String name = null;
        String displayName = null;
        String description = null;

        while (TokenKind.eof != this.current.getKind() && TokenKind.bodyEnd != this.current.getKind()) {
            switch (this.current.getKind()) {
                case scope:
                    name = this.current.getValue();

                    break;

                case subject:
                    displayName = this.current.getValue();

                    break;

                case body:
                    description = this.current.getValue();

                    break;

                default:
                    break;
            }

            this.consume();
        }

        if (StringUtils.isEmpty(name)) return;

        final ScopeProfile scopeProfile = new ScopeProfile(name, this.lang, displayName, description);

        this.scopeProfiles.add(scopeProfile);
    }
}
