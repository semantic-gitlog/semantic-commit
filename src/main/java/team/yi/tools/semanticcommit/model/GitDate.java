package team.yi.tools.semanticcommit.model;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class GitDate extends Date {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS Z";

    private static final long serialVersionUID = 2109484695431208009L;

    public GitDate() {
        this(new Date());
    }

    public GitDate(final Date date) {
        this(date.getTime());
    }

    public GitDate(final long date) {
        super(date);
    }

    public static GitDate valueOf(final Date date) {
        if (date == null) return null;

        return new GitDate(date.getTime());
    }

    @Override
    public String toString() {
        return DateFormatUtils.format(this, DATE_FORMAT);
    }
}
