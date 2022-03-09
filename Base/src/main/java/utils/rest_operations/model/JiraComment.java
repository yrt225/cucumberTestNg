package utils.rest_operations.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JiraComment {
    private String body;

    public JiraComment() {}

    public JiraComment(String body) {
        this.body = body;
    }
}
