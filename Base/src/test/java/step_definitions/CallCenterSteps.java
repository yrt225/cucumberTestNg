package step_definitions;

import constants.Constants;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import page_objects.callCenter.*;
import page_objects.policy.*;
import utils.ReusableMethods;
import utils.db_operations.QueryMongoDB;
import utils.state.TestContext;
@Component
@ScenarioScope
public class CallCenterSteps {
    @Autowired @Lazy
    ICarrierPage iCarrierPage;
    @Autowired @Lazy
    TestContext testContext;
    @Autowired @Lazy
    QueryMongoDB queryMongoDB;
    @Autowired @Lazy
    IBulletinDetails iBulletinDetails;
    @Autowired @Lazy
    ILogin iLogin;
    @Autowired @Lazy
    ISubmissionSummary iSubmissionSummary;
    @Autowired @Lazy
    IDisposition iDisposition;
    @Autowired @Lazy
    ITransferRequest iTransferRequest;
    @Autowired @Lazy
    IPartnerSearch iPartnerSearch;
    @Autowired @Lazy
    IBORTransfer iborTransfer;
    @Autowired @Lazy
    IDocumentUpload iDocumentUpload;
    @Autowired @Lazy
    IApplicationQuestion iApplicationQuestion;
    @Autowired @Lazy
    IBundleUnderWriting iBundleUnderWriting;
    @Autowired @Lazy
    IPolicyCurrentSummary iPolicyCurrentSummary;
    @Autowired @Lazy
    ICustomSearch iCustomSearch;
    @Autowired @Lazy
    ICertificateOfInsurance iCertificateOfInsurance;
    @Autowired @Lazy
    ICustomerSummary iCustomerSummary;
    @Autowired @Lazy
    IAORTransfer iaorTransfer;
    @Autowired @Lazy
    IUwRejection iuwRejection;
    @Autowired @Lazy
    ReusableMethods reusableMethods;

    @And("he should navigates to BORAOR Producer Transfer Page")
    public void heShouldNavigatesToBORAORProducerTransferPage() {
        iCarrierPage.NavigateToBORTransfer();
        iborTransfer.newTransfer();
    }

    @Then("he should try to perform Schedule Transfer on Inactive Agent For the {string} and {string} and {string} and {string}")
    public void heShouldTryToPerformScheduleTransferOnInactiveAgent(String batchType, String newDPDAgent, String currentPartnerNumber, String newPartnerNumber) {
        iTransferRequest.scheduleNewTransferForQuote(batchType);
        iPartnerSearch.searchCurrentBroker(currentPartnerNumber);
        iPartnerSearch.searchNewBroker(newPartnerNumber);
        iTransferRequest.selectnewAgent(newDPDAgent);
        iTransferRequest.navigateToDisplayDetails();
        iTransferRequest.selectQuote();
        iTransferRequest.exitTransfer();
    }

   }

