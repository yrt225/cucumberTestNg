package datatable_convertor;


import io.cucumber.java.DataTableType;
import model.policy.*;

import java.util.Map;

public class DataTypeRegistryConfig {

    @DataTableType
    public Actor convertActor(Map<String, String> entry) {
        Actor actor = new Actor();
        actor.setSubmissionId(entry.get("submissionId"));
        actor.setPaymentId(entry.get("paymentId"));
        actor.setBillingId(entry.get("billingId"));
        actor.setNewLocation(entry.get("newLocation"));
        actor.setMtaId(entry.get("mtaId"));
        actor.setLocationId(entry.get("locationId"));
        actor.setIncidentId(entry.get("incidentId"));
        actor.setCustomerId(entry.get("customerId"));
        actor.setStatementId(entry.get("statementId"));
        actor.setFormId(entry.get("formId"));
        actor.setGlAdditionalInsuredId(entry.get("glAdditionalInsuredId"));
        actor.setTaskId(entry.get("taskId"));
        actor.setVendorId(entry.get("vendorId"));
        return actor;
    }

    @DataTableType
    public User convertUser(Map<String, String> entry) {
        User user = new User();
        user.setUserRole(entry.get("userRole"));
        user.setPartnerNo(Integer.parseInt(entry.get("partnerNo")));
        return user;
    }

    @DataTableType
    public Location convertLocation(Map<String, String> entry) {
        Location location = new Location();
        location.setLocationId(entry.get("locationId"));
        return location;
    }

    @DataTableType
    public GlAdditionalInsured convertGlAdditionalInsured(Map<String, String> entry) {
        GlAdditionalInsured glAdditionalInsured = new GlAdditionalInsured();
        glAdditionalInsured.setGLAdditionalInsuredId(entry.get("glAdditionalInsuredId"));
        return glAdditionalInsured;
    }

    @DataTableType
    public Submission convertSubmission(Map<String, String> entry) {
        Submission submission = new Submission();
        submission.setSubmissionId(entry.get("submissionId"));
        return submission;
    }

    @DataTableType
    public CustomerDetails convertCustomerDetails(Map<String, String> entry) {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setCustomerId(entry.get("customerId"));
        return customerDetails;
    }

}