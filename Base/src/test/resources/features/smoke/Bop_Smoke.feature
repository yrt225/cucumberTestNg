Feature: Create BOP Policy with incident and Perform Pending Cancellation

  @valuestream=Policy
    @lob=BOP
    @functionality=PAS_Rules
    @userstory=MUSTDEV-8066,MUSTDEV-1088
    @bopsmoke
    @jira=MA-2408
  Scenario Outline: Create policy and insert into mongo database by using the <locationId> and <submissionId>
    Given the actor is configured with below variables
      | submissionId   | paymentId | billingId | mtaId | locationId   | incidentId | customerId | statementId |
      | <submissionId> | 6         | 1         | 43    | <locationId> | 1          | 44         | 4           |
    And user logs into Oneshield Application
      | userRole    | partnerNo |
      | Underwriter | 0         |
    And user enters all details and submit Entity Details page
    And actor enters all required details for getting quote
    When User Rates quote on Limits and Deductibles page and navigates and submits Billing page for current bop policy
    Then User makes payment and generates Policy Number with type as <policyType>
    Then the policy has to be generated and save in  mongo database
@
    Examples:
      | submissionId | locationId | policyType   |
      | BOP23518     | NY22       | PL_BOP_CYBER |

  @PendingCancellation23518
    @valuestream=Policy
    @lob=BOP
    @functionality=PAS_Rules
    @userstory=MUSTDEV-8066,MUSTDEV-1088
  Scenario Outline:Verify user is able to issue policy with claim and issue company cancellation for <locationId> and for Submission ID  <submissionId>
    Given the actor is configured with below variables
      | submissionId   | paymentId | billingId | mtaId | locationId   | incidentId | customerId | statementId |
      | <submissionId> | 6         | 1         | 43    | <locationId> | 1          | 44         | 4           |
    And user logs into Oneshield Application
      | userRole   | partnerNo |
      | Claims Ops | 0         |
    And User should be able to retrieve the policy number by using the<submissionId> and <locationId>  and policytype <policyType>
    And he Stores required <product> policy
    And He creates an incident file
    Then He verify lobcoverages and assign to under writer for BOP Policy
    And He assign to underwriter
    Then User Logouts from Application
    And user logs into Oneshield Application
      | userRole | partnerNo |
      | Advisor  | 0         |
    And User opens the user required <product> policy and search that policy in application
    And he initiate pending cancellation and refer to underwriter
    Then User Logouts from Application
    And user logs into Oneshield Application
      | userRole    | partnerNo |
      | Underwriter | 0         |
    And User opens the user required <product> policy and search that policy in application
    Then User accepts referral and issue Pending Cancellation transaction

    Examples:
      | submissionId | locationId | product                 | policyType   |
      | BOP23518     | NY22       | Business Owners Package | PL_BOP_CYBER |
