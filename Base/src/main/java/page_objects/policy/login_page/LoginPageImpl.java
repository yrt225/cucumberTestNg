package page_objects.policy.login_page;


import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import model.policy.User;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import page_objects.policy.ILogin;
import utils.ReusableMethods;
import utils.TestRunConfig;
import utils.web.BrowserInit;
@Component
@ScenarioScope
public class LoginPageImpl implements ILogin {
	@Autowired @Lazy
	LoginPage loginPage;
	@Autowired @Lazy
	ReusableMethods reusableMethods;
	@Autowired @Lazy
	BrowserInit browserInit;


	public void logsIntoOneShieldAsUser (User user) {

		if (TestRunConfig.SSO) {
			browserInit.navigateTo(TestRunConfig.BASE_URL + "/oneshield/sso");

			acceptSslCertificate();

			reusableMethods.clickTypeAndTab(TestRunConfig.APP_USER_NAME, loginPage.getSsoUserName());
			reusableMethods.clickTypeAndTab(TestRunConfig.PWD, loginPage.getSsoPassword());
			reusableMethods.clickAndWait(loginPage.getSsoSubmit());
			setUserRole(user.getUserRole());
		} else {
			browserInit.navigateTo(TestRunConfig.BASE_URL);
			acceptSslCertificate();
			if (reusableMethods.isDisplayed(loginPage.getLoginAsPartnerEmp()))
				reusableMethods.clickAndWait(loginPage.getLoginAsPartnerEmp());
			reusableMethods.clickTypeAndTab(String.valueOf(user.getPartnerNo()), loginPage.getPartnerNo());
			reusableMethods.clickTypeAndTab(user.getUserId(), loginPage.getUserName());
			reusableMethods.clickTypeAndTab(user.getPassword(), loginPage.getPassword());
			reusableMethods.clickAndWait(loginPage.getLoginLink());
		}

	}


	public void setUserRole (String role) {
		selectPreference();
		changeUserRole(role);

	}

    public void selectPreference() {
		try {
            if (reusableMethods.isCurrentlyVisible(loginPage.getPreferences()))
                reusableMethods.clickAndWait(loginPage.getPreferences());
        } catch (NoSuchElementException e) {
            reusableMethods.hardWait(5);
            reusableMethods.clickAndWait(loginPage.getPreferences());
        }
    }

	public void changeUserRole (String role) {

//        reusableMethods.clickTypeAndTab(role,loginPage.getSelectRoleDropdown());
        reusableMethods.dropDownWithoutSelect(role,loginPage.getSelectRoleDropdown(),loginPage.getDropDropList());
        reusableMethods.hardWait(2);

		if (reusableMethods.isCurrentlyVisible(loginPage.getSaveButton()))
			reusableMethods.clickAndWait(loginPage.getSaveButton());

	}

	public void logOutAfterCreatingSubmission () {
		if (reusableMethods.isCurrentlyVisible(loginPage.getFirstExit())) {
			reusableMethods.clickAndWait(loginPage.getFirstExit());
			if (reusableMethods.isCurrentlyVisible(loginPage.getSecondExit())) {
				reusableMethods.clickAndWait(loginPage.getSecondExit());

			}
		}
		reusableMethods.hardWait(5);
		if (reusableMethods.isCurrentlyVisible(loginPage.getLogout())) {
			reusableMethods.clickAndWait(loginPage.getLogout());

			try {
				/* below line will work if SSO is ON*/
				reusableMethods.acceptPopUp();
			} catch (NoAlertPresentException Ex) {
				/* below line will work if SSO is OFF*/
				if(reusableMethods.isCurrentlyVisible(loginPage.getAlert()))
				reusableMethods.clickAndWait(loginPage.getAlert());
			}
		}
		reusableMethods.hardWait(5);
		reusableMethods.clearBrowserCache();
	}

	public void exitToHomePage () {
		if (reusableMethods.isCurrentlyVisible(loginPage.getFirstExit())) {
			reusableMethods.clickAndWait(loginPage.getFirstExit());
		}
		if (reusableMethods.isCurrentlyVisible(loginPage.getFirstExit())) {
			reusableMethods.clickAndWait(loginPage.getFirstExit());
		}
		if (reusableMethods.isCurrentlyVisible(loginPage.getFirstExit())) {
			reusableMethods.clickAndWait(loginPage.getFirstExit());
		}
		if (reusableMethods.isCurrentlyVisible(loginPage.getFirstExit())) {
			reusableMethods.clickAndWait(loginPage.getFirstExit());
		}
	}

	private void acceptSslCertificate () {
		reusableMethods.hardWait(1);
		if (reusableMethods.isCurrentlyVisible(loginPage.getSslCertificateAdvancedLink())) {
			reusableMethods.clickAndWait(loginPage.getSslCertificateAdvancedLink());
//            reusableMethods.hardWait(1);
			if (reusableMethods.isCurrentlyVisible(loginPage.getSslCertificateAdvancedLink())) {
				browserInit.getEdriver().navigate().refresh();
				reusableMethods.hardWait(1);
				reusableMethods.clickAndWait(loginPage.getSslCertificateAdvancedLink());
//                reusableMethods.hardWait(3);
			}
			reusableMethods.clickAndWait(loginPage.getSslProceedLink());
//            reusableMethods.hardWait(3);
		}
	}


	@Override
	public boolean isDisplayed () {
		return false;
	}
}
