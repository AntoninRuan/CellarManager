package fr.womax.cavemanager.utils.report;

import java.util.Date;

/**
 * @author Antonin Ruan
 */
public class BugInfo {

    private String title, description, stackTrace;
    private Date date;

    public BugInfo(String title, String description, String stackTrace, Date date) {
        this.title = title;
        this.description = description;
        this.stackTrace = stackTrace;
        this.date = date;
        if(this.title == null) {
            if (this.description.length() > 16) {
                this.title = description.substring(0, 16) + "...";
            } else {
                this.title = description;
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public Date getDate() {
        return date;
    }
}
