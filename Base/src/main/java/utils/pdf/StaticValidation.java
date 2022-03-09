package utils.pdf;

import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import model.FormValidation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.asserts.SoftAssert;
import utils.db_operations.DB_Loader;
import utils.state.TestContext;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static constants.Constants.formsConstants.*;

@Component
@ScenarioScope
public class StaticValidation {
	private static Logger log = LoggerFactory.getLogger(StaticValidation.class);
	@Autowired @Lazy
	private TestContext testContext;

	private boolean validatePDF (String filePath,String policyNumber) {
		String stream = null;
		String command = String.format("java -jar %s -k %s -f %s -lf output_eyes -p %s -a %s -fb %s -br %s",
				IMAGE_TESTER_PATH, APPLI_TOOLS_KEY, filePath, PROXY_APPLI_TOOLS,policyNumber,testContext.getBatchName(),testContext.getLocation().getStateCode());
		try {
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			stream = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
			log.info(stream);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return stream == null || !stream.contains("Mismatch");
	}

	public void appliToolsValidation (List<String> formNumbers) {
		SoftAssert softAssert = new SoftAssert();

		FormValidation form = new FormValidation();
		form.setPolicyNumber(testContext.getPolicy().getPolicyNumber());
		form.setFormNumber(Arrays.toString(formNumbers.toArray()));
		form.setProductType(testContext.getSubmission().getLob());
		form.setState(testContext.getLocation().getState());
		form.setStatus(validatePDF(new File("target" + File.separator + testContext.getBatchName()).getAbsolutePath(),testContext.getPolicy().getPolicyNumber()) ? "passed" : "failed");

		for (int i = 0; i < formNumbers.size(); i++) {
			formNumbers.set(i, formNumbers.get(i).replace("/", "").replace(" ", ""));
		}
		softAssert.assertTrue(form.getStatus().equalsIgnoreCase("passed"), form.getFormNumber() + " validation failed, please validate in applitools");
		for (File file : new File("target" + File.separator + testContext.getBatchName()+File.separator+testContext.getPolicy().getPolicyNumber()).listFiles()) {
			formNumbers.remove(file.getName().replace(".pdf", ""));
		}
		for (String curr : formNumbers) {
			softAssert.fail("Missing:" + curr + " is not extracted and not validated in applitools");
		}
		// insert status of the form validation in form_validation collection
		DB_Loader.insertFormValidationObj(form);
		softAssert.assertAll();
	}
}
