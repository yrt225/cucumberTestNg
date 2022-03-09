package utils.db_operations;

import java.util.List;

import static constants.Constants.FF_RATE_CALCULATION;
import static constants.Constants.FF_TEST_DATA_COLLECTION;
@Component
@ScenarioScope
public class QueryMongoDB {
    private Logger log = LoggerFactory.getLogger(QueryMongoDB.class);
    private MongoCollection mongoCollection = null;

    public Submission retrieveSubmissionObj(String submissionId) {
        if (submissionId == null)
            return null;
        // Fetch Submission record from submission sheet
        List<Submission> submissionList = new ExcelOperations<Submission>().get(DataSheetType.SUBMISSION);
        for (Submission curr : submissionList) {
            if (curr.getSubmissionId().equalsIgnoreCase(submissionId)) {
                return curr;
            }
        }
        Assert.fail("Record not found:retrieveSubmissionObj");
        return null;
    }

  
}