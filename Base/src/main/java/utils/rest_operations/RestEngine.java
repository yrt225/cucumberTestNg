package utils.rest_operations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import utils.rest_operations.model.RestCall;
import utils.rest_operations.model.RestConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@ScenarioScope
public class RestEngine {
	private static String rootFolder = RestEngine.class.getClassLoader().getResource("rest_calls").getPath();;

	private static Logger log = LoggerFactory.getLogger(RestEngine.class);

	/**
	 * @param restCall
	 * @param queryParamsMap - Used to pass the query parameters in a map
	 * @param pathParamsList - Used to pass the path parameters in a list
	 * @return Response object
	 */
	public Response call (RestCall restCall, Map<String, String> queryParamsMap, List<String> pathParamsList, String body) {
		RestConfig restConfig = null;
		Response response = null;
		String endPoint = "", queryParams = "", pathParams = "";

		restConfig = getConfig(restCall);
		/*
		 * Adding query parameters
		 */
		if (queryParamsMap != null) {
			for (Map.Entry<String, String> entry : queryParamsMap.entrySet()) {
				queryParams += entry.getKey() + "=" + entry.getValue() + "&";
			}
			queryParams = queryParams.substring(0, queryParams.length() - 1);
		}
		/*
		 * Adding path parameters
		 */
		if (pathParamsList != null) {
			for (String param : pathParamsList) {
				pathParams += "/" + param;
			}
		}
		/*
		 * Framing end point
		 */
		endPoint = restConfig.getEndPoint();
		if (!queryParams.isEmpty()) {
			endPoint += "?" + queryParams;
		}
		if (!pathParams.isEmpty()) {
			endPoint += pathParams;
		}
		switch (restConfig.getMethod()) {
			case GET:
				response = RestAssured.given().when().get(endPoint).thenReturn();
				break;
			case POST:
				response = RestAssured.given().headers(restConfig.getHeaders()).body(body).when().post(endPoint).thenReturn();
				break;
			case PUT:
			case DELETE:
		}
		log.info("EndPoint->" + endPoint);
		log.info("Body->" + body);
		response.prettyPrint();

		//Verify.verify(response.getStatusCode() == 200, "Status code is not 200:Actual-" + response.getStatusCode());

		return response;
	}

	private RestConfig getConfig (RestCall restCall) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(FileUtils.readFileToString(new File(rootFolder + File.separator + restCall.getFileName()), "UTF-8"), RestConfig.class);
		} catch (IOException exp) {
			exp.printStackTrace();
			return null;
		}
	}
}
