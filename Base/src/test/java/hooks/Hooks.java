package hooks;


import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import model.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.TestRunConfig;
import utils.db_operations.DB_Loader;
import utils.rest_operations.RestActions;
import utils.rest_operations.model.JiraComment;
import utils.rest_operations.model.RestCall;
import utils.state.TestContext;
import utils.web.BrowserInit;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@ScenarioScope
public class Hooks {
    private Logger log = LoggerFactory.getLogger(Hooks.class);

    private static final String BAMBOO_BUILD_NUMBER = "bamboo_buildNumber";
    private static final String BAMBOO_BRANCH_NAME = "bamboo_planRepository_branchName";
    private static final String BAMBOO_RESULTS_URL = "bamboo_resultsUrl";

    @Autowired @Lazy
    private BrowserInit browserInit;
    @Autowired @Lazy
    private TestContext testContext;
    @Autowired @Lazy
    private RestActions<Response> restActions;

    @Before("not @Service and not @StaticContent")
    public void beforeScenario(Scenario scenario) {
        log.info("Executing:" + scenario.getName());
        browserInit.openBrowser();
    }

    public void pushCommentToJira(String scenarioName,String jiraTicket,String status){
        List<String> pathParams=Arrays.asList(jiraTicket,"comment");
        JiraComment comment=new JiraComment(String.format("{status:%s,buildNumber:%s,resultsUrl:%s,branch:%s}",status,System.getenv(BAMBOO_BUILD_NUMBER),System.getenv(BAMBOO_RESULTS_URL),System.getenv(BAMBOO_BRANCH_NAME)));
        log.info("Scenario:"+scenarioName+"->Upload status:"+restActions.get(RestCall.JIRA_ADD_COMMENT,null,pathParams,comment).statusCode());
    }

    @After("not @Service")
    public void afterScenario(Scenario scenario)throws Exception {
            log.info("Completed:" + scenario.getName() + "-" + scenario.getStatus());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            /*
             * Capture Screen shot & close browser
             */
            if(TestRunConfig.isLocal || scenario.isFailed()) {
                File file = ((TakesScreenshot) browserInit.getEdriver().getWrappedDriver()).getScreenshotAs(OutputType.FILE);
                BufferedImage image = ImageIO.read(file);
                ImageIO.write(image, "png", byteArrayOutputStream);
                scenario.attach(byteArrayOutputStream.toByteArray(), "image/png",scenario.getName());
            }
            browserInit.quitBrowser();
            /*
             * Validate tags
             */
            validateTags(scenario.getSourceTagNames());
            /*
             * Additional attributes for qTest logging
             */
            StringBuilder builder = new StringBuilder();
            if (testContext.getLocation() != null) {
                builder.append("state=" + testContext.getLocation().getState());
            }
            if(testContext.getPolicy()!=null){
                scenario.attach("testcase_notes=("+testContext.getPolicy().toString()+","+testContext.getPolicy().getQuoteReferenceNumber()+")","text/plain",scenario.getName());
                scenario.attach("\nRefer collection:ff_execution_results for screen shots of test execution","text/plain",scenario.getName());
            }
            scenario.attach(builder.toString().trim(),"text/plain",scenario.getName());
            /*
             * Save execution results to mongo db
             */
            saveExecutionResultsInDb(scenario,byteArrayOutputStream.toByteArray());
           /*
            * Push comment to jira ticket
            *
           if(!TestRunConfig.isLocal) {
               List<String> jiraTag=scenario.getSourceTagNames().stream().filter(s -> s.contains("jira")).collect(Collectors.toList());
                if(jiraTag.size()>0){
                    pushCommentToJira(scenario.getName(), jiraTag.get(0).split("=")[1], scenario.getStatus().toString());
               }
           }*/
    }

    @Before("@Service")
    public void beforeService(Scenario scenario) {
        log.info("Executing:" + scenario.getName());
    }


    @After
    public void scenarioPrint(Scenario scenario) {
        if(scenario.getSourceTagNames().contains("@SkipQTest")){
            TestRunConfig.SKIP_QTEST_UPLOAD=true;
        }
        testContext.setTestLogs("\n----------------------------------------------------------------------");
        testContext.setTestLogs("\nEnvironment :"+testContext.getPolicy().getEnv());
        testContext.setTestLogs("\nQuote Reference Number :"+testContext.getPolicy().getQuoteReferenceNumber());
        testContext.setTestLogs("\nMonthly Premium :"+testContext.getPolicy().getMonthlyPremium());
        testContext.setTestLogs("\nPolicy Number :"+testContext.getPolicy().getPolicyNumber());
        testContext.setTestLogs("\nSecond Policy Number :"+testContext.getPolicy().getSecondPolicyNumber());
        testContext.setTestLogs("\n----------------------------------------------------------------------");
        log.info(testContext.getTestLogs());
        scenario.attach(testContext.getTestLogs().getBytes(),"text/plain",scenario.getName());
    }

    public void saveExecutionResultsInDb(Scenario scenario,byte[] screenshot){
        TestExecutionMain testExecutionMain=new TestExecutionMain();
        testExecutionMain.setScenarioName(scenario.getName());
        testExecutionMain.setTags(scenario.getSourceTagNames().toString());
        TestExecutionResults testExecutionResults=new TestExecutionResults();
        testExecutionResults.setTestRunStatus(scenario.getStatus().toString().toLowerCase());
        testExecutionResults.setScreenShot(screenshot.toString());
        testExecutionResults.setPolicy(testContext.getPolicy());
        testExecutionResults.setAdditionalLogs(testContext.getTestLogs());

        testExecutionMain.setTestExecutionResultsList(Arrays.asList(testExecutionResults));
        DB_Loader.insertExecutionResults(testExecutionMain);
    }

    @After("@Service")
    public void afterService(Scenario scenario) {
        log.info("Completed:" + scenario.getName() + "-" + scenario.getStatus());
        /*
         * Additional attributes for qTest logging
         */
        StringBuilder builder=new StringBuilder();
        if(testContext.getLocation()!=null){
                builder.append("state=" + testContext.getLocation().getState());
        }
        scenario.attach(builder.toString().trim(),"text/plain", scenario.getName());
        validateTags(scenario.getSourceTagNames());
    }

    @Before("@StaticContent")
    public void beforeStaticContent(Scenario scenario) {
        Predicate<String> batchNameExists= s->s.contains("batchName");
        List<String> batchNameList=scenario.getSourceTagNames().stream().filter(batchNameExists).collect(Collectors.toList());
        if(batchNameList.size()>0) {
            testContext.setBatchName(batchNameList.get(0).split("=")[1]);
        }
        testContext.setScenarioName("Scn:-" + scenario.getName());
        beforeScenario(scenario);
    }

    @After("@StaticContent")
    public void afterStaticContent() {
        try{testContext.getEyesOperations().disposeVisualTesting();}
        catch (NullPointerException exp) { log.info("Forms test case is executed"); }
    }

    private void validateTags(Collection<String> tags){
        Predicate<String> reqTags= s->s.contains("functionality")||s.contains("valuestream")||s.contains("lob")||s.contains("userstory");
        List<String> reqtagList=tags.stream().filter(reqTags).collect(Collectors.toList());
        if(reqtagList.size()!=4){
            throw new IllegalArgumentException("Required tags missing: Check if the scenario has @functionality , @valuestream , @userstory and @lob");
        }
        for(String tag:reqtagList){
            String[] temp=tag.split("=")[1].split(",");
            if(tag.contains("functionality")){
                try{
                    for(String curr:temp){
                        if(curr.contains("PAS_Mid-Term_Endorsement")){
                            continue;
                        }else {
                            Functionality.valueOf(curr);
                        }
                    }
                }
                catch (IllegalArgumentException exp){
                    throw new IllegalArgumentException("Functionality value has to be one of:"+ Arrays.asList(Functionality.values())+" or PAS_Mid-Term_Endorsement");
                }
            }
            if(tag.contains("valuestream")){
                try{
                    for(String curr:temp){ValueStream.valueOf(curr);}
                }
                catch (IllegalArgumentException exp){
                    throw new IllegalArgumentException("Valuestream value has to be one of:"+ Arrays.asList(ValueStream.values()));
                }
            }
            if(tag.contains("lob")){
                try{
                    for(String curr:temp){Lob.valueOf(curr);}
                }
                catch (IllegalArgumentException exp){
                    throw new IllegalArgumentException("Lob value has to be one of:"+ Arrays.asList(Lob.values()));
                }
            }
        }
    }
}