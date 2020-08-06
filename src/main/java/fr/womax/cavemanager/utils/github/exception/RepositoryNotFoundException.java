package fr.womax.cavemanager.utils.github.exception;

public class RepositoryNotFoundException extends Exception {

    public RepositoryNotFoundException(String username, String reponame) {
        super("Repo " + reponame + " not found on " + username + " repos list");
    }
}
