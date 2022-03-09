package utils.db_operations;

import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import model.*;
import model.policy.Actor;
import model.policy.DataSheetType;
import model.policy.Policy;
import org.jongo.MongoCollection;
import org.jongo.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import utils.TestRunConfig;
import utils.excel_operations.ExcelOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static constants.Constants.*;

@Component
@ScenarioScope
public class DB_Loader {

    static final Logger logger = LoggerFactory.getLogger(DB_Loader.class);
    private static MongoCollection mongoCollection = null;


    public static String getCollectionName(DataSheetType dataSheetType) {
        if (TestRunConfig.CREATE_A_NEW_COLLECTION) {
            return TestRunConfig.TESTER_NAME + "_" + dataSheetType.getSheetName().replaceAll("[_\\s]", "");
        } else {
            return TestRunConfig.PROJECT + "_" + dataSheetType.getSheetName().replaceAll("[_\\s]", "");
        }
    }



    public static void insertExecutionResults(TestExecutionMain testExecutionMain) {
        mongoCollection = DB_Connection.getJongoConnection().getCollection(FF_EXECUTION_RESULTS);
        TestExecutionMain record = mongoCollection.findOne("{'scenario_name':#}", testExecutionMain.getScenarioName()).as(TestExecutionMain.class);

        if (record != null) {
            if(record.getTestExecutionResultsList()==null){
                record.setTestExecutionResultsList(new ArrayList<>());
            }
            record.getTestExecutionResultsList().add(0,testExecutionMain.getTestExecutionResultsList().get(0));
            record.setTags(testExecutionMain.getTags());
            // update existing record
            Update update = mongoCollection.update("{'scenario_name':#}", testExecutionMain.getScenarioName());
            try{update.with(record);}
            catch (org.bson.BsonMaximumSizeExceededException exp){
                record.getTestExecutionResultsList().remove(record.getTestExecutionResultsList().size()-1);
                record.getTestExecutionResultsList().remove(record.getTestExecutionResultsList().size()-2);
                record.getTestExecutionResultsList().remove(record.getTestExecutionResultsList().size()-3);
                record.getTestExecutionResultsList().remove(record.getTestExecutionResultsList().size()-4);
                record.getTestExecutionResultsList().remove(record.getTestExecutionResultsList().size()-5);
                update.with(record);
            }
        } else {
            // insert new record
            mongoCollection.insert(testExecutionMain);
        }
    }

    public static void insertQuoteOrPolicyCreated(Policy policy) {
        mongoCollection = DB_Connection.getJongoConnection().getCollection(FF_TEST_DATA_COLLECTION);
        mongoCollection.insert(policy);
    }

    public static void saveQuoteAndPolicy(List<Policy> policyList) {
        mongoCollection = DB_Connection.getJongoConnection().getCollection(FF_TEST_DATA_POLICY_COLLECTION);
        Predicate<Policy> condition= p->p==null;
        policyList.removeIf(condition);
        PolicyMain policyMain=new PolicyMain();
        policyMain.setPolicyList(policyList);
        mongoCollection.insert(policyMain);
    }


    public static void insertFormValidationObj(FormValidation formValidation) {
        mongoCollection = DB_Connection.getJongoConnection().getCollection(FF_FORM_VALIDATIONS_COLLECTION);
        mongoCollection.insert(formValidation);
    }

    public static void insertErrors(CustomError customError) {
        mongoCollection = DB_Connection.getJongoConnection().getCollection(FF_ERROR_LOGS);
        mongoCollection.insert(customError);
    }

// 
   

}