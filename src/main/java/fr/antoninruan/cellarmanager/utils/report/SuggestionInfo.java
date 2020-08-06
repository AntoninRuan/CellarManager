package fr.antoninruan.cellarmanager.utils.report;

import java.util.Date;

/**
 * @author Antonin Ruan
 */
public class SuggestionInfo {

    private String title, description;
    private Date date;

    public SuggestionInfo(String title, String description, Date date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }
}
