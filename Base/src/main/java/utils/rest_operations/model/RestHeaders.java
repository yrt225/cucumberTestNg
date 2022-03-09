package utils.rest_operations.model;

public enum RestHeaders {
    CONTENT_TYPE("Content-Type"),
    ACCEPT_ENCODING("Accept-Encoding");

    private String header;

    RestHeaders(String header) {
        this.header = header;
    }
}
