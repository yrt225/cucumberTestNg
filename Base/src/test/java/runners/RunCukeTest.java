package runners;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.testng.*;
import io.restassured.RestAssured;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.TestRunConfig;
import utils.web.RetryAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CucumberOptions(
        tags = "@bopsmoke",
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        plugin = {"pretty", "html:target/cucumber-html-report",
                "json:target/cucumber-json-report.json",
                "junit:target/cucumber-results.xml"},
        features = "src/test/resources/features",
        glue = {"classpath:"},
        dryRun = false,
        monochrome = true
)
public class RunCukeTest extends AbstractTestNGCucumberTests {
    private static final String BAMBOO_BUILD_NUMBER = "bamboo_buildNumber";
    private static final String BAMBOO_BRANCH_NAME = "bamboo_planRepository_branchName";
    private static final String THREAD_COUNT_KEY = "dataproviderthreadcount";
    private static final String THREAD_COUNT_VALUE = TestRunConfig.THREAD_COUNT;
    private static final boolean isParallel = true;
    private static final Logger log = LoggerFactory.getLogger(RunCukeTest.class);
    private TestNGCucumberRunner testNGCucumberRunner;

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);

        System.setProperty(THREAD_COUNT_KEY, THREAD_COUNT_VALUE);
        try {
            if (!System.getProperty("os.name").toLowerCase().contains("linux"))
                Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
        } catch (IOException e) {
            log.info(e.getMessage());
        }

    }

    @Override
    @DataProvider(parallel = isParallel)
    public Object[][] scenarios() {
        if (testNGCucumberRunner == null) {
            testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
        }
        return testNGCucumberRunner.provideScenarios();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class,description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
    public void runScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) {
        testNGCucumberRunner.runScenario(pickleWrapper.getPickle());
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass(){
        int scenarioCount = testNGCucumberRunner.provideScenarios().length;
        testNGCucumberRunner.finish();

        File cucumberJsonFile = new File("target/cucumber-json-report.json");
        File encodedStrFile = new File("target/encoded_cucumber_json.txt");
        try {
            encodedStrFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File reportOutputDirectory = new File("target");
        List<String> jsonFiles = new ArrayList<>();
        jsonFiles.add("target/cucumber-json-report.json");

        String projectName = "Fast Follow";

        Configuration configuration = new Configuration(reportOutputDirectory, projectName);

        configuration.setBuildNumber(System.getenv(BAMBOO_BUILD_NUMBER));
        //TODO Uncomment when the cucumner reporting version upgraded to 5.0
//        configuration.addPresentationModes(PresentationMode.EXPAND_ALL_STEPS);
//        configuration.setNotFailingStatuses(Collections.singleton(Status.FAILED));
        configuration.addClassifications("Tags", TestRunConfig.TAGS);
        configuration.addClassifications("Platform", System.getProperty("os.name"));
        configuration.addClassifications("Environment", TestRunConfig.ENV);
        configuration.addClassifications("Browser", TestRunConfig.BROWSER);
        configuration.addClassifications("Branch", System.getenv(BAMBOO_BRANCH_NAME));

        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        reportBuilder.generateReports();

        if (scenarioCount == 0) {
            log.info("No Scenarios ran");
            return;
        }
        if (TestRunConfig.SKIP_QTEST_UPLOAD) {
            log.info("qTest upload skipped");
            return;
        }
        /*
         * Encode cucumber json file
         */
        try {
            log.info("Starting:Encoding json file");
            /*
             * Remove attachments
             */
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(FileUtils.readFileToString(cucumberJsonFile, "US-ASCII"));
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement current : jsonArray) {
                try {
                    JsonElement elements, after;

                    for (int i = 0; i < current.getAsJsonObject().get("elements").getAsJsonArray().size(); i++) {
                        elements = current.getAsJsonObject().get("elements").getAsJsonArray().get(i);
                        for (int j = 0; j < elements.getAsJsonObject().get("after").getAsJsonArray().size(); j++) {
                            after = elements.getAsJsonObject().get("after").getAsJsonArray().get(j);
                            after.getAsJsonObject().get("embeddings").getAsJsonArray().get(0)
                                    .getAsJsonObject().addProperty("data", "imageRemoved");
                        }
                    }
                } catch (NullPointerException exp) {
                    log.error("Image not found");
                } catch (Exception exp) {
      //123              log.error(exp.getMessage());
                }
            }
            /*
             * Encode in base64 encoding
             */
            String encodedString = new String(Base64.encodeBase64(jsonArray.toString().getBytes()));
            log.info("Completed:Encoding json file");
            /*
             * Upload to qTest
             */
            log.info("***************************Started qTest Upload*****************************************************");
            String pulseUrl = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userName", TestRunConfig.TESTER_NAME);
            jsonObject.addProperty("resultsPath", TestRunConfig.QTEST_RESULTS_PATH);
            jsonObject.addProperty("testcycle", TestRunConfig.QTEST_CYCLE_ID);
            jsonObject.addProperty("dryRun", TestRunConfig.QTEST_DRY_RUN);
            jsonObject.addProperty("result", encodedString);
            jsonObject.addProperty("projectId", TestRunConfig.QTEST_PROJECT_ID);

            if (TestRunConfig.QTEST_PROJECT_NAME.equalsIgnoreCase("native")) {
                pulseUrl = "https://pulse-7.qtestnet.com/webhook/7738c698-17a6-4887-bd38-0bec652c60ca";
            } else {
                pulseUrl = "https://pulse-7.qtestnet.com/webhook/306d04b0-28b4-46d5-a3ba-b3c688d7c0d4";
            }
            if (TestRunConfig.isLocal) {
                RestAssured.given()
                        .relaxedHTTPSValidation()
                        .header("cache-control", "no-cache")
                        .header("content-type", "application/json")
                        .proxy(TestRunConfig.PROXY_HOST, TestRunConfig.PROXY_PORT)
                        .body(jsonObject.toString())
                        .when()
                        .post(pulseUrl)
                        .thenReturn().getBody().prettyPrint();
            } else {
                RestAssured.given()
                        .relaxedHTTPSValidation()
                        .header("cache-control", "no-cache")
                        .header("content-type", "application/json")
                        .body(jsonObject.toString())
                        .when()
                        .post(pulseUrl)
                        .thenReturn().getBody().prettyPrint();
            }
            log.info("***************************Completed qTest Upload*****************************************************");
//COde modification
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}