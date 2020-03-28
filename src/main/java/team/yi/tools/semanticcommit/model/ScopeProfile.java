package team.yi.tools.semanticcommit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class ScopeProfile implements Serializable {
    private static final long serialVersionUID = 2711673609375799052L;

    @EqualsAndHashCode.Include
    private final String name;
    @EqualsAndHashCode.Include
    private final String lang;
    private final String displayName;
    private final String description;

    public ScopeProfile(final String name, final String lang, final String displayName, final String description) {
        this.name = name;
        this.lang = lang;
        this.displayName = displayName;
        this.description = description;
    }
}
