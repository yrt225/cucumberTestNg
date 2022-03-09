package utils.state;


import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.selenium.Eyes;
import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import model.MidTermAdjustment;
import model.VendorInfo;
import model.billing.Billing;
import model.policy.*;
import org.springframework.stereotype.Component;
import page_objects.EyesOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
@Component
@ScenarioScope
public class TestContext {
	public static String NOT_STATIC = "Not Static Content";

	private Submission submission;
	private Mta mta;
	private CustomerDetails customerDetails;
	private Location location;
	private Statements statements;
	private CreditCardBilling creditCardBilling;
	private Policy policy;
	private List<Policy> policies;
	private MidTermAdjustment midTermAdjustment;
	private Billing billing;
	private GlAdditionalInsured glAdditionalInsured;
	public IncidentInformation incidentInformation;
	private Actor actor;
	private List<Policy> policyList;
	private EyesOperations eyesOperations;
	private String scenarioName;
	private String batchName;
	private String downloadFilePath;
	private Eyes eyes=null;
	private StringBuilder testLogs =new StringBuilder();
	private Task task;
	private VendorInfo vendorInfo;
    private List<String> locationListDetails;

    public String getTestLogs (){
		return testLogs.toString();
	}

	public void setTestLogs(String log){
		testLogs.append(log);
	}

	public static Map<String, BatchInfo> eyesMap = new HashMap<>();

	public static BatchInfo getEyesMap (String batchName) {
		return eyesMap.get(batchName);
	}

	public static void setEyesMap (String batchName, BatchInfo batchInfo) {
		eyesMap.put(batchName, batchInfo);
	}

	public Billing getBilling () {
		if (billing == null) {
			billing = new Billing();
		}
		return billing;
	}

	public Policy getPolicy () {
		if (policy == null) {
			policy = new Policy();
		}
		return policy;
	}

	public String getScenarioNameNotNull () {
		if (scenarioName == null) {
			scenarioName = NOT_STATIC;
		}
		return scenarioName;
	}

	public Actor getActor () {
		if (actor == null) {
			actor = new Actor();
		}
		return actor;
	}

	public void setListOfPolicies(List<Policy> policyList) {
		if(this.policyList ==null){
			this.policyList =new ArrayList<>();
		}
		this.policyList.addAll(policyList);
	}

	public List<Policy> getListOfPolicies() {
		return policyList;
	}

	public void setLocationDetails(List<String> locationListDetails){
		this.locationListDetails=new ArrayList<>();
		this.locationListDetails.addAll(locationListDetails);
	}
	public List<String> getLocationDetails(){
		return locationListDetails;
	}



}