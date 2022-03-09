@FastFollow_Smoke

@valuestream=Policy

Feature: Smoke Test

  @lob=PL
    @userstory=MUSTDEV-13521,MUSTDEV-482,MUSTDEV-1078
    @functionality=PAS_Out_of_Sequence_Endorsement
  Scenario Outline:Create Policy perform Insured Cancellation

    Given the actor is configured with below variables
      | submissionId   | paymentId | mtaId | locationId   | customerId | statementId |
      | <submissionId> | 1         | 35    | <locationId> | 1          | 4           |
    And user logs into Oneshield Application
      | userRole   | partnerNo   |
      | <userRole> | <partnerNo> |
    And user enters all details and submit Entity Details page
    When User Rates quote on Limits and Deductibles page, navigating from Customer Information by entering all required details on each page
    Then User submits Offers page
    And User fills Important Statements and Submit the page
    Then User fills Delivery Preference and submits the page
    And User fills payment details on Billing page and submits the Page
    Then User makes payment and generates Policy Number
    When User searches and opens the policy
    Then He should be able to perform Cancellation Insured  on the Policy

    Examples:
      | submissionId | locationId | userRole    | partnerNo |
      | PL2307       | SD1        | Underwriter | 0         |


  @lob=Cyber

    @userstory=MUSTDEV-13521,MUSTDEV-482
    @functionality=PAS_Out_of_Sequence_Endorsement
  Scenario Outline: Create Policy and perform Renewal transaction with Underwriter referral
    Given the actor is configured with below variables
      | submissionId   | paymentId | mtaId | locationId   | customerId | statementId |
      | <submissionId> | 1         | 27    | <locationId> | 1          | 4           |
    And user logs into Oneshield Application
      | userRole   | partnerNo   |
      | <userRole> | <partnerNo> |
    And user enters all details and submit Entity Details page
    When User Rates quote on Limits and Deductibles page, navigating from Customer Information by entering all required details on each page
    Then User submits Offers page
    And User fills Important Statements and Submit the page
    Then He enters all required details for referral transaction on quote generation
    And User fills payment details on Billing page and submits the Page
    Then User makes payment and generates Policy Number
    When User searches and opens the policy
    And User should be able to navigate policy current summary page
    Then User should be able to  initiate renewal transaction and navigate to  policy premium summary page
    And User should be able to enter all the details for under writer referrals Transaction on Renewal Policy
    Then User Should be able to change Summary on Renewal Policy
    Then User should be able to issue the renewal transaction on Policy

    Examples:
      | submissionId | locationId | userRole    | partnerNo |
      | CYB2303      | SC1        | Underwriter | 0         |


  @lob=PL

    @userstory=MUSTDEV-13521,MUSTDEV-482
    @functionality=PAS_Out_of_Sequence_Endorsement
  Scenario Outline:Create Policy and perform Cancellation and Reinstate Transactions

    Given the actor is configured with below variables
      | submissionId   | paymentId | mtaId | locationId   | customerId | statementId |
      | <submissionId> | 1         | 19    | <locationId> | 1          | 4           |
    And user logs into Oneshield Application
      | userRole   | partnerNo   |
      | <userRole> | <partnerNo> |
    And user enters all details and submit Entity Details page
    When User Rates quote on Limits and Deductibles page, navigating from Customer Information by entering all required details on each page
    Then User submits Offers page
    And User fills Important Statements and Submit the page
    Then User fills Delivery Preference and submits the page
    And User fills payment details on Billing page and submits the Page
    Then User makes payment and generates Policy Number
    When User searches and opens the policy
    Then User should be able to cancel the Policy
    And He should be able to navigate to TransactionListPage
    And He should be able to Perform The Reinstate Transaction on same Policy

    Examples:
      | submissionId | locationId | userRole    | partnerNo |
      | PL2302       | FL1        | Underwriter | 0         |

  @lob=GL
    @userstory=MUSTDEV-482
    @functionality=PAS_Rating
  @plglregression_smoketest
  Scenario Outline: Create GL Policy for <locationId> and for Submission ID  <submissionId>
    Given the actor is configured with below variables
      | submissionId   | locationId   | customerId | statementId | paymentId | billingId |
      | <submissionId> | <locationId> | 1          | 4           | 1         | 1         |
    And user logs into Oneshield Application
      | userRole    | partnerNo |
      | Underwriter | 0         |
    And user enters all details and submit Entity Details page
    And User enters LOB and COB Details on Submission Summary page and Submits the page
    And User submits Submission Disposition page and save Quote reference number
    And User navigates and enters details on Location List page
    And User navigates and enters details on Application Questions page
    And User navigates and enters details on Limits and Deductables page and click on save
    And User Rates quote on Limits and Deductibles page and navigates and submits Billing page
    Then User issues policy by completing Payments and saves Policy Number with  <formId> in to Mongo

    @smoke_4
    Examples:
      | submissionId | locationId | formId     |
      | FGL_ALL_TC24 | GA1        | smoke_test |

  @valuestream=Policy
    @lob=BOP
    @functionality=PAS_Quote
    @BOPSmokeTest
    @userstory=MUSTDEV-13521
  Scenario Outline:create a policy for BOP for <locationId> and for Submission ID  <submissionId>
    Given the actor is configured with below variables
      | submissionId   | paymentId | billingId | newLocation   | mtaId | locationId   | incidentId | customerId | statementId |
      | <submissionId> | 1         | 1         | <newLocation> | 20    | <locationId> | 1          | 1          | 4           |
    And user logs into Oneshield Application
      | userRole   | partnerNo   |
      | <userRole> | <partnerNo> |
    And user enters all details and submit Entity Details page
    When User submits Billing page navigating from Customer Summary page by entering all required details
    Then User makes payment and generates Policy Number
    Then User saves Policy Number from bundled policies <product>
    @level=MiniRegression
    @level=Functional
    @wip
    Examples:
      | submissionId | locationId | userRole    | partnerNo | product                |
      | BOPSmoke     | NY22       | Underwriter | 0         | Business Owner Package |
