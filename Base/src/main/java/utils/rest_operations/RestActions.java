package utils.rest_operations;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;
import utils.rest_operations.model.JiraComment;
import utils.rest_operations.model.RestCall;

import java.util.List;
import java.util.Map;
@Component
@ScenarioScope
public class RestActions<T> {

    private Response response;

    public T get(RestCall restCall, Map<String, String> queryParamsMap, List<String> pathParamsList, Object bodyObj) {
        ObjectMapper objectMapper = new ObjectMapper();
        RestEngine restEngine = new RestEngine();

        switch (restCall) {
            case JIRA_ADD_COMMENT:
                // /{jiraTicket}/comment
                try {
                    response = restEngine.call(RestCall.JIRA_ADD_COMMENT, queryParamsMap, pathParamsList, objectMapper.writeValueAsString((JiraComment) bodyObj));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return (T) response;
        }
        return null;
    }
}
