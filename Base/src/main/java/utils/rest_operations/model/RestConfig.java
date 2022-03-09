package utils.rest_operations.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import utils.TestRunConfig;

import java.util.Map;

@ToString
@Getter
@Setter
public class RestConfig {
    RestMethod method;
    String endPoint;
    String body;
    Map<String, String> headers;

    /**
     * TODO: Need to change env as per mustang
     *
     * @return
     */
    public String getEndPoint() {
        switch (TestRunConfig.ENV) {
            case "ci":
                return endPoint.replace("<env>", "dev");
            case "systest":
                return endPoint.replace("<env>", "systest");
            case "systest2":
                return endPoint.replace("<env>", "systest2");
            case "uat":
                return endPoint.replace("<env>", "uat");
        }
        return null;
    }
}
