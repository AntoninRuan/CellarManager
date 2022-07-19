package fr.antoninruan.cellarmanager.utils.github.exception;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(String username) {
        super("User " + username + " not found on GitHubAPI");
    }
}
