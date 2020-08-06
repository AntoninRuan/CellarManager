package fr.womax.cavemanager.utils.github.exception;

public class GitHubAPIConnectionException extends Exception {

    public GitHubAPIConnectionException(int errorCode, String errorMessage) {
        super("GitHubAPI responded with HTTP error code: " + errorCode + " " + errorMessage);
    }

}
