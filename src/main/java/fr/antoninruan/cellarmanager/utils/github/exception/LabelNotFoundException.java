package fr.antoninruan.cellarmanager.utils.github.exception;

public class LabelNotFoundException extends Exception {

    public LabelNotFoundException(String labelName, String repoName) {
        super("Label " + labelName + " not found on repo " + repoName);
    }

}
