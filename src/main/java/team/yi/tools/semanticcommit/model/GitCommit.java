package team.yi.tools.semanticcommit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class GitCommit implements Serializable, Comparable<GitCommit> {
    private static final long serialVersionUID = -6001492191188530230L;

    @EqualsAndHashCode.Include
    @ToString.Include
    private final String hashFull;
    private final String hash;
    private final String hash7;
    private final String hash8;
    @ToString.Include
    private final GitDate commitTime;
    private final Long commitTimeLong;
    private final String message;
    private final Boolean merge;
    private final GitPersonIdent authorIdent;
    private final GitPersonIdent committerIdent;

    public GitCommit(
        final String hashFull,
        final GitDate commitTime,
        final String message,
        final Boolean isMerge,
        final GitPersonIdent authorIdent,
        final GitPersonIdent committerIdent
    ) {
        this.hashFull = hashFull;
        this.hash = this.hashFull.substring(0, 15);
        this.hash7 = this.hash.substring(0, 7);
        this.hash8 = this.hash.substring(0, 8);
        this.message = message;
        this.commitTimeLong = commitTime.getTime() / 1000;
        this.commitTime = commitTime;
        this.merge = isMerge;
        this.authorIdent = authorIdent;
        this.committerIdent = committerIdent;
    }

    public static String toMessageTitle(final String message) {
        final int pos = message.indexOf('\n');

        return pos > -1 ? message.substring(0, pos).trim() : StringUtils.trimToEmpty(message);
    }

    public static String toMessageBody(final String message) {
        final int pos = message.indexOf('\n');

        return pos > -1 ? message.substring(pos).trim() : StringUtils.EMPTY;
    }

    @Override
    public int compareTo(final GitCommit o) {
        final int compareTo = o.commitTime.compareTo(this.commitTime);

        return compareTo == 0 ? o.hash.compareTo(this.hash) : compareTo;
    }

    public String getMessageBody() {
        return toMessageBody(this.message);
    }

    public String getMessageTitle() {
        return toMessageTitle(this.message);
    }

    public Boolean isMerge() {
        return this.merge;
    }
}
