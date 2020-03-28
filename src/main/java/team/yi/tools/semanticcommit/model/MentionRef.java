package team.yi.tools.semanticcommit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class MentionRef implements Serializable {
    private static final long serialVersionUID = -2958658493632357049L;

    @EqualsAndHashCode.Include
    @ToString.Include
    private final String username;
    private final String url;

    public MentionRef(final String username, final String url) {
        this.username = username;
        this.url = url;
    }
}
