package utils.rest_operations.model;

public enum RestCall {
    JIRA_ADD_COMMENT("add_comment.json");

    private String fileName;

    RestCall(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }
}
