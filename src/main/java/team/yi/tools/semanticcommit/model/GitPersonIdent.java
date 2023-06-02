package team.yi.tools.semanticcommit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class GitPersonIdent implements Serializable {
    private static final long serialVersionUID = 5451757231232410532L;

    @ToString.Include
    private final GitDate when;
    @ToString.Include
    private final String name;
    @ToString.Include
    private final String email;

    public GitPersonIdent(final GitDate when, final String name, final String email) {
        this.when = when;
        this.name = name;
        this.email = email;
    }
}
