package page_objects.policy.login_page;


import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.web.BrowserInit;

import java.util.List;

@Getter
@Component
@ScenarioScope
public class LoginPage {


    @FindBy(partialLinkText = "Login as Partner/Employee")
    WebElement loginAsPartnerEmp;
    @FindBy(css = "input[osviewid*='CI_6376001']")
    public WebElement partnerNo;
    @FindBy(css = "input[osviewid*='CI_8780402']")
    public WebElement userName;
    @FindBy(css = "input[osviewid*='CI_8780502']")
    public WebElement password;
    @FindBy(id = "userNameInput")
    public WebElement ssoUserName;
    @FindBy(id = "passwordInput")
    public WebElement ssoPassword;
    @FindBy(id = "submitButton")
    public WebElement ssoSubmit;
    @FindBy(xpath = "//*[contains(text(),'Advanced')]")
    public WebElement sslCertificateAdvancedLink;
    @FindBy(xpath = "//h1[contains(text(),'Proxy Error')]")
    public WebElement proxyErrorPage;

    @FindBy(xpath = "//a[contains(text(),'Proceed to ')]")
    public WebElement sslProceedLink;

    @FindBy(css = "span[osviewid*='AB_559205']")
    public WebElement loginLink;


    @FindBy(xpath = "//span[contains(text(),'xit')]")
    public WebElement firstExit;

    @FindBy(css = "span[osviewid*='AB_1350948']")
    public WebElement secondExit;

    @FindBy(css = "span[osviewid*='AB_113401']")
    public WebElement logout;

    @FindBy(xpath = "//span[text()='OK']")
    public WebElement alert;


    @FindBy(xpath = "//span[text()='preferences']")
    public WebElement preferences;


    @FindBy(css = "input[osviewid*='CI_16641564']")
    public WebElement selectRoleDropdown;

    @FindBy(xpath = "//*[contains(@id,'R4C0-picker-listWrap')]//li")
    public List<WebElement> dropDropList;
    @FindBy(css = "span[osviewid*='AB_709146']")
    public WebElement saveButton;

    @Autowired @Lazy
    LoginPage(BrowserInit browserInit) {
        PageFactory.initElements(browserInit.getEdriver(), this);
    }
}
