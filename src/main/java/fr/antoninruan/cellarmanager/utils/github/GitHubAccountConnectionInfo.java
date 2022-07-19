package fr.antoninruan.cellarmanager.utils.github;

public class GitHubAccountConnectionInfo {

    private String username;
    private String password;
    private boolean stayConnected;

    public GitHubAccountConnectionInfo(String username, String password, boolean stayConnected) {
        this.username = username;
        this.password = password;
        this.stayConnected = stayConnected;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isStayConnected() {
        return stayConnected;
    }
}
