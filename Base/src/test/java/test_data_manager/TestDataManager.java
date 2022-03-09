package test_data_manager;

import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import model.policy.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import utils.db_operations.QueryMongoDB;
@Component
@ScenarioScope
public class TestDataManager {
    private static Logger log = LoggerFactory.getLogger(TestDataManager.class);
    private QueryMongoDB queryMongoDB;

    public TestDataManager(QueryMongoDB queryMongoDB) {
        this.queryMongoDB = queryMongoDB;
    }

    public Policy performEvent(Event event, Policy policy) {
        Policy retrievedPolicy = null;
        switch (event) {
            case CREATE_POLICY:
                retrievedPolicy = queryMongoDB.retrieveActorFromTestDataSet(policy);
                if (retrievedPolicy == null) {
                    log.info(policy.toString() + " is not found in test data collection");
                    /*
                     * Implement policy creation
                     */
                    retrievedPolicy = quoteAndBindPolicy();
                }
                break;
            case GL_RECALCULATE_RATINGS:
                break;
            case PL_RECALCULATE_RATINGS:
                break;
            case CYBER_RECALCULATE_RATINGS:
                break;
        }
        return retrievedPolicy;
    }

    private Policy quoteAndBindPolicy() {
        /*
         * TODO
         * Create Policy
         */
        return null;
    }
}