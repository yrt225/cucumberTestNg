package utils;

import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import model.CustomError;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import utils.db_operations.DB_Loader;
import utils.web.BrowserInit;
import utils.web.Waits;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
@Component
@ScenarioScope
public class ReusableMethods {
    protected static final String DELIMITER = ":,|;";
    private EventFiringWebDriver edriver = null;
    private WebDriver driver = null;
    private Actions actions;

    private Waits waits;
    private Logger log = LoggerFactory.getLogger(ReusableMethods.class);
    int retryCounter = 0;

    @Autowired @Lazy
    ReusableMethods(BrowserInit browserInit, Waits waits) {
        edriver = browserInit.getEdriver();
        actions = new Actions(edriver);
        this.waits = waits;

    }

    //Method to get driver instance from reusable methods

    public WebDriver getDriver() {
        return edriver;
    }


    public void switchToLatestTab() {
        String currentTab = edriver.getWindowHandle();
        for (String curr : edriver.getWindowHandles()) {
            if (!curr.equals(currentTab)) {
                edriver.switchTo().window(curr);
                break;
            }
        }
    }


    public boolean isDisplayed(WebElement... element) {
        for (WebElement curr : element) {
            try {
                if (!curr.isDisplayed())
                    return false;
            } catch (Exception exp) {
                return false;
            }
        }
        return true;
    }

    public boolean isDisplayed(By... element) {
        for (By curr : element) {
            try {
                if (!edriver.findElement(curr).isDisplayed())
                    return false;
            } catch (Exception exp) {
                return false;
            }
        }
        return true;
    }

    public WebElement getVisibleElement(List<WebElement> elementList) {
        for (WebElement element : elementList) {
            if (element.isDisplayed()) {
                return element;
            }
        }
        return null;
    }

    public void selectFilesToUpload(String oneOrMoreFiles) {
        String[] files = oneOrMoreFiles.split(DELIMITER);

        String dir = System.getProperty("user.dir");
        String resourceScriptsPath = "\\src\\main\\resources\\autoIT\\scripts\\";
        String resourceFilesPath = "\\src\\main\\resources\\autoIT\\files\\";
        for (String file : files) {
            try {
                hardWait(4);
                Process process = Runtime.getRuntime().exec(dir + resourceScriptsPath + "UploadFile.exe " + dir + resourceFilesPath + file);
                process.waitFor();
                hardWait(3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getTextList(List<WebElement> webElementList) {
        List<String> list = new ArrayList<>();
        for (WebElement element : webElementList) {
            try {
                list.add(element.getText());
            } catch (StaleElementReferenceException exp) {
            }
        }
        return list;
    }

    public static List<String> getListOf(String[] array) {
        return Arrays.asList(array);
    }

    public void waitFor(int seconds) {
        try {
            // temp code
            if (seconds > 10) {
                seconds = seconds % 1000;
            }
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void hardWait(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
        }
    }

    public void wait_and_enter_text(WebElement element, String text) {
        try {
            waits.until_element_displayed(element);
            element.sendKeys(text);
        } catch (StaleElementReferenceException exp) {
            log.error(exp.getMessage());
        }
    }

    public void switchToPaymentIFrame(String id) {
        hardWait(5);
        edriver.switchTo().frame(id);
    }

    public void acceptPopUp() {

        edriver.switchTo().alert().accept();
    }

    public void switchToDefaultIFrame() {
        edriver.switchTo().defaultContent();
    }

    public WebElement getClickableElement(WebElement element) {
        return waits.until_element_clickable(element);
    }

    /**
     * Used to click web element and handle stale element reference exception
     * Dynamic menu bar having the multiple anchor tags so we are navigating to the page with anchor tag text
     * @param message - web element to click
     */

    public void clickMainBuildingInfoUsingText(String message)
    {

        retryCounter++;
        if (retryCounter == 3) {
            retryCounter = 0;
            Assert.fail("Can't click " + this + " Session ID: " + getSessionId() + " Current page:" + pageName());
        }
        try {
            WebElement element = edriver.findElement(By.xpath("//a[contains(text(),'" + message + "')]//ancestor::table//following-sibling::table['2']//a[contains(text(),'Main Building - Building Information')]"));
            element.click();
        } catch (Exception ex) {
            hardWait(1);
            clickMainBuildingInfoUsingText(message);
        }
        retryCounter = 0;
        return;

    }
    public void clickAnchorByUsingTableIndex(int i) {
        retryCounter++;
        if (retryCounter == 3) {
            retryCounter = 0;
            Assert.fail("Can't click " + this);
        }
        try {
            WebElement elementsdata = edriver.findElement(By.xpath("//*[@class='x-grid-item-container']//table[" + i + "]//a"));
            elementsdata.click();
            waitUntilSpinnerNotVisible();
        } catch (Exception ex) {
            waitUntilSpinnerNotVisible();
            clickAnchorByUsingTableIndex(i);
        }
        retryCounter = 0;
        return;
    }
    public void clickAndWait(WebElement element) {
        try {
            waits.until_element_clickable(element);
            hardWait(2);
            this.actions.click(waits.until_element_clickable(element)).perform();

        } catch (Exception exp) {
            log.error(exp.getMessage());
            Assert.fail("Can't click " + element + " Session ID: " + getSessionId() + " Current page:" + pageName());
        }
        waitUntilSpinnerNotVisible();
    }


    public void clickAndSort(WebElement element) {
        try {
            ((JavascriptExecutor) edriver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });", element);
            if (!element.getAttribute("aria-sort").equalsIgnoreCase("ascending"))
                element.click();
        } catch (Exception exp) {
            log.error(exp.getMessage());
            Assert.fail("Can't click " + element + " Session ID: " + getSessionId() + " Current page:" + pageName());
        }
        waitUntilSpinnerNotVisible();
    }

    public void clickUnselectedOn(WebElement element) {
        try {
            ((JavascriptExecutor) edriver).executeScript("arguments[0].removeAttribute('unselectable'); return arguments[0];", element);
            element.click();
        } catch (Exception exp) {
            log.error(exp.getMessage());
            Assert.fail("Can't click " + element + " Session ID: " + getSessionId() + " Current page:" + pageName());
        }
        waitUntilSpinnerNotVisible();
    }
    /**
     * If dropDownOption is null then below actions are skipped
     *
     * @param dropDownOption - Used to select value from drop down
     * @param explain        - Used for explaination incase of Others
     * @param dropDownWE     - Web Element to enter dropDownOption
     * @param explainWE      - Web Element to enter explain when Others is selected from dropDownOption
     */
    public void setReasonWithExplain(String dropDownOption, String explain, WebElement dropDownWE, WebElement explainWE) {
        if (dropDownOption != null) {
            try {
                ((JavascriptExecutor) edriver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });", dropDownWE);
                waits.until_element_displayed(dropDownWE);
                Select choose = new Select(dropDownWE);
                choose.selectByVisibleText(dropDownOption);
                if (explain != null) {
                    explainWE.sendKeys(explain);
                }
            } catch (StaleElementReferenceException exp) {
                log.info("Stale Reference exception while selecting:" + dropDownOption);
            }
        }
    }

    /**
     * Used in case of Yes or No options
     *
     * @param yesOrNo - variable to hold yes or no choice
     * @param yesWE   - web element to be clicked on selecting yes
     * @param noWE    - web element to be clicked on selecting no
     */
    public void selectCheckBox(String yesOrNo, WebElement yesWE, WebElement noWE) {
        if (yesOrNo != null) {
            ((JavascriptExecutor) edriver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });", yesWE);
            if (yesOrNo.equalsIgnoreCase("yes")) {
                this.actions.click(yesWE).perform();
            } else {
                this.actions.click(noWE).perform();
            }
        }
    }

    /**
     * Used to select from drop down
     *
     * @param option     - Value selected from dropdown
     * @param dropDownWE - web element used to select value
     */
    public void dropDownWithoutSelect(String option, WebElement dropDownWE, List<WebElement> dropvaluesWE) {
        if (option != null && !option.isEmpty()) {
            retryCounter++;
            if (retryCounter == 4) {
                retryCounter = 0;
                getSessionId();
                Assert.fail("Can't select " + option + " From " + dropDownWE + " Session ID: " + getSessionId() + " Current page:" + pageName());
            }
            try {
                waits.until_element_clickable(dropDownWE);
                actions.moveToElement(dropDownWE).pause(Duration.ofSeconds(1)).click(dropDownWE).perform();
                for (WebElement e : dropvaluesWE) {
                    if (e.getAttribute("textContent").trim().equalsIgnoreCase(option)) {
                        actions.moveToElement(dropDownWE).pause(Duration.ofSeconds(1)).click(e).perform();
                        break;
                    }
                }
            } catch (Exception exp) {
                log.info(exp.getMessage() + "selecting for selectFromDropDown:" + option);
                actions.sendKeys(Keys.TAB).perform();
                dropDownWithoutSelect(option, dropDownWE, dropvaluesWE);
            }
            retryCounter = 0;
        }
    }

    public void selectFromDropDown(String option, WebElement dropDownWE) {
        if (option != null && !option.isEmpty()) {
            class_name(dropDownWE);
            retryCounter++;
            if (retryCounter == 4) {
                retryCounter = 0;
                getSessionId();
                Assert.fail("Can't select " + option + " From " + dropDownWE + " Session ID: " + getSessionId() + " Current page:" + pageName());
            }
            try {
                waits.until_element_clickable(dropDownWE);
                if (dropDownWE.getTagName().equalsIgnoreCase("select")) {
                    waits.until_element_displayed(dropDownWE);
                    Select choose = new Select(dropDownWE);
                    choose.selectByVisibleText(option);
                } else {
                    hardWait(1);
//					waits.until_element_clickable(dropDownWE);
                    actions.click(dropDownWE).perform();
                    hardWait(1);
                    actions.click(edriver.findElement(By.xpath("//li[text()='" + option + "']"))).perform();
                    actions.sendKeys(Keys.TAB).perform();
                    if (retryCounter == 3) {
                        actions.moveToElement(dropDownWE).pause(Duration.ofSeconds(1)).click(dropDownWE).perform();
                        actions.sendKeys(Keys.TAB).perform();
                        dropDownWE.clear();
                        waitUntilSpinnerNotVisible();
                        dropDownWE.sendKeys(option);
                        waitUntilSpinnerNotVisible();

                    }
                }

            } catch (Exception exp) {
                log.info(exp.getMessage() + "selecting for selectFromDropDown:" + option);
                actions.sendKeys(Keys.TAB).perform();
                selectFromDropDown(option, dropDownWE);
            }
            retryCounter = 0;
        }
        waitUntilSpinnerNotVisible();
        try {
            if (option != null && dropDownWE.getText().isEmpty())
                clickTypeAndTab(option, dropDownWE);
            waitUntilSpinnerNotVisible();
        } catch (Exception ignored)
        {}
    }



    /**
     * Used to select from drop down with contains
     *
     * @param option     - Value selected from dropdown
     * @param dropDownWE - web element used to select value
     */
    public void selectFromDropDownWithContains(String option, WebElement dropDownWE) {
        if (option != null && !option.isEmpty()) {
            retryCounter++;
            if (retryCounter == 3) {
                retryCounter = 0;
                Assert.fail("Can't select " + option + "From" + dropDownWE + " Session ID: " + getSessionId() + " Current page:" + pageName());
            }

            try {
                waits.until_element_clickable(dropDownWE);
                if (dropDownWE.getTagName().equalsIgnoreCase("select")) {
                    waits.until_element_displayed(dropDownWE);
                    Select choose = new Select(dropDownWE);
                    choose.selectByVisibleText(option);
                } else {
                    waits.until_element_clickable(dropDownWE);
                    actions.click(dropDownWE).perform();
                    hardWait(1);
                    actions.click(edriver.findElement(By.xpath("//li[contains(text(),'" + option + "')]"))).perform();
                }

            } catch (Exception exp) {
                log.info(exp.getMessage() + "exception for selectFromDropDownWithContains while selecting:" + option);
            }
            waitUntilSpinnerNotVisible();
            retryCounter = 0;
        }
        waitUntilSpinnerNotVisible();
        if (dropDownWE.getAttribute("value").equalsIgnoreCase("- Select -"))
            clickTypeAndTab(option, dropDownWE);
        waitUntilSpinnerNotVisible();

    }

    /**
     * Used to select one of the (yes,no,not required) options
     *
     * @param yesOrNoOrNA - value holding choice
     * @param yesWE       - web element to be selected on choice yes
     * @param noWE        - web element to be selected on choice no
     * @param naWE        - web element to be selected on choice not required
     */
    public void yesOrNoOrNA(String yesOrNoOrNA, WebElement yesWE, WebElement noWE, WebElement naWE) {
        if (yesOrNoOrNA != null) {
            ((JavascriptExecutor) edriver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });", yesWE);
            if (yesOrNoOrNA.equalsIgnoreCase("yes")) {
                this.actions.click(yesWE).perform();
            } else if (yesOrNoOrNA.equalsIgnoreCase("no")) {
                this.actions.click(noWE).perform();
            } else {
                this.actions.click(naWE).perform();
            }
        }
    }

    /**
     * Used to select check box or radio button
     *
     * @param option     - Used for yes or no
     * @param checkboxWE - web element to check or uncheck
     */
    public void checkCheckBox(String option, WebElement checkboxWE) {
        retryCounter++;
        if (retryCounter == 3) {
            retryCounter = 0;
            Assert.fail("Can't click " + checkboxWE + " Session ID: " + getSessionId() + " Current page:" + pageName());
        }
        try {
            if (option != null) {
                waits.until_element_clickable(checkboxWE);
                hardWait(1);
                if (option.equalsIgnoreCase("yes")) {
                    if (!checkboxWE.isSelected()) {
                        actions.moveToElement(checkboxWE);
                        hardWait(2);
                        actions.click(checkboxWE).perform();
                    }
                } else if (option.equalsIgnoreCase("no")) {
                    if (checkboxWE.isSelected()) {
                        actions.moveToElement(checkboxWE);
                        actions.click(checkboxWE).perform();
                    }
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage() + "selecting for CheckBox:" + option);
            actions.sendKeys(Keys.TAB).perform();
            checkCheckBox(option, checkboxWE);
        }
        retryCounter = 0;
        waitUntilSpinnerNotVisible();
        hardWait(1);
    }

    /**
     * Used to send keys to the fields
     *
     * @param input   - text to be send to textField
     * @param inputWE - web element used to send text
     */
    public void clickTypeAndTab(String input, WebElement inputWE) {
        if (input != null && !input.isEmpty()) {
            retryCounter++;
            if (retryCounter == 3) {
                retryCounter = 0;
                Assert.fail("Can't click " + inputWE + " Session ID: " + getSessionId() + " Current page:" + pageName());
            }
            try {
                waits.until_element_clickable(inputWE);
                actions.moveToElement(inputWE).pause(Duration.ofSeconds(1)).click(inputWE).perform();
                inputWE.clear();
                waitUntilSpinnerNotVisible();
                actions.moveToElement(inputWE).sendKeys(inputWE, input).pause(Duration.ofSeconds(1)).sendKeys(Keys.TAB).perform();
                waitUntilSpinnerNotVisible();
            } catch (Exception exp) {
                log.error(exp.getMessage());
                clickTypeAndTab(input, inputWE);
            }
            retryCounter = 0;
            return;
        }
    }

    /**
     * Used to send keys to the fields with timeout
     *
     * @param input   - text to be send to textField
     * @param inputWE - web element used to send text
     */
    public void clickTypeAndTab(String input, WebElement inputWE, int timeout) {
        if (input != null) {
            ((JavascriptExecutor) edriver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });", inputWE);
            retryCounter++;
            if (retryCounter == 3) {
                retryCounter = 0;
                Assert.fail("Failed clickTypeAndTab for value" + input + " in to the field" + inputWE + " Session ID: " + getSessionId() + " Current page:" + pageName());
            }
            try {
                hardWait(timeout);
                inputWE.clear();
                waitUntilSpinnerNotVisible();
                hardWait(timeout);
                inputWE.sendKeys(input);
            } catch (Exception exp) {
                log.error(exp.getMessage() + "exception for clickTypeAndTab on:" + inputWE);
                clickTypeAndTab(input, inputWE);
            }
            retryCounter = 0;
            return;
        }
    }

    /**
     * Used to send zip code in location entries
     *
     * @param zipCode   - if the zip code length is < 5 then 0 is prepended
     * @param zipCodeWE - web element used to input zip code
     */
    public void inputZipCode(String zipCode, WebElement zipCodeWE) {
        if (zipCode != null) {
            waits.until_element_clickable(zipCodeWE);
            if (zipCode.length() < 5)
                zipCode = "0" + zipCode;
            zipCodeWE.sendKeys(zipCode);
        }
    }

    /**
     * Used to select one of (agree,disagree) radio group
     *
     * @param agreeOrNo  - variable holding agree or disagree
     * @param agreeWE    - web element to select agree option
     * @param disagreeWE - web element to select disagree option
     */
    public void agreeOrDisagree(String agreeOrNo, WebElement agreeWE, WebElement disagreeWE) {
        if (agreeOrNo != null) {
            ((JavascriptExecutor) edriver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });", agreeWE);
            if (agreeOrNo.toLowerCase().contains("disagree")) {
                this.actions.click(disagreeWE).perform();
            } else {
                this.actions.click(agreeWE).perform();
            }
        }
    }

    /**
     * Used to validate if the drop down has a value
     *
     * @param value
     * @param dropDownWE
     * @return
     */
    public boolean checkIfDropDownHasAValue(String value, WebElement dropDownWE) {
        Select choose = new Select(dropDownWE);
        return getTextList(choose.getOptions()).contains(value);
    }

    /**
     * Used to return text of the web element, if text is empty then value is returned
     *
     * @param textWE
     * @return
     */
    public String getTextWE(WebElement textWE) {
        waits.until_element_displayed(textWE);
        try {
            String text = textWE.getText();
            return text;
//			return (text != null && !text.isEmpty()) ? text : textWE.getAttribute("value");
        } catch (Exception exp) {
            log.error(exp.getMessage() + "exception while getting the text from" + textWE);
//			Assert.fail("unable to get text" + " Session ID: "+ getSessionId()+ " Current page:" + pageName());
        }
        return null;
    }

    /**
     * Used to return text of the  dropdown selected value.
     *
     * @param textWE
     * @param index
     * @return
     */

    public String getSelectedValueAtIndex(WebElement textWE, int index) {
        Select value = new Select(textWE);
        return index == 0 ? value.getFirstSelectedOption().getText() : value.getOptions().get(index).getText();
    }
    /**
     * Used to select Checkbox.
     *
     * @param checkboxWE
     * @param value
     * @return
     */

    public void selectCheckBox(String value, WebElement checkboxWE) {
        retryCounter++;
        if (retryCounter == 3) {
            retryCounter = 0;
            Assert.fail("Can't click " + checkboxWE + " Session ID: " + getSessionId() + " Current page:" + pageName());
        }
        try {
            if (value != null && !value.isEmpty()) {
                ((JavascriptExecutor) edriver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });", checkboxWE);
                if ((((checkboxWE.getAttribute("checked") != null)) && (value.equalsIgnoreCase("No"))) || (checkboxWE.getAttribute("checked") == null) && (value.equalsIgnoreCase("Yes"))) {
                    actions.moveToElement(checkboxWE);
                    hardWait(1);
                    actions.click(checkboxWE).perform();
                    waitUntilSpinnerNotVisible();

                    hardWait(2);
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage() + "selecting for CheckBox:" + value);
            actions.sendKeys(Keys.TAB).perform();
            selectCheckBox(value, checkboxWE);
        }
        retryCounter = 0;
        waitUntilSpinnerNotVisible();
        hardWait(1);
    }


    public void selectCheckBoxByLabel(String label, String value) {
        if (value != null && !value.isEmpty()) {
            if (value.equalsIgnoreCase("Yes")) {
                By bySelector = By.xpath("//label[contains(text(),'" + label + "')]/../span/input");
                WebElement webElement = edriver.findElement(bySelector);
                ((JavascriptExecutor) edriver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });", webElement);
                actions.click(webElement).perform();
                hardWait(1);
            }
        }
//		iAjaxSpinner.isDisplayed();
    }

    public void enterTabularInput(String id, int row, int index, String val) {
        if (val != null && !val.isEmpty()) {
            retryCounter++;
            if (retryCounter == 3) {
                retryCounter = 0;
                Assert.fail("Can't click " + this + " Session ID: " + getSessionId() + " Current page:" + pageName());
            }
            try {
                actions.click(edriver.findElement(By.xpath("//div[contains(@id, '" + id + "')]")).findElements(By.tagName("table")).get(row).findElement(By.tagName("tr")).findElements(By.tagName("td")).get(index).findElements(By.tagName("div")).get(0)).perform();
                hardWait(2);
                waitUntilSpinnerNotVisible();
                edriver.findElement(By.xpath("//div[contains(@id, '" + id + "')]")).findElements(By.tagName("table")).get(row).findElement(By.tagName("tr")).findElements(By.tagName("td")).get(index).findElements(By.tagName("div")).get(1).findElement(By.tagName("input")).sendKeys(val);
                waitUntilSpinnerNotVisible();
            } catch (Exception exp) {
                log.error(exp.getMessage() + "while enterTabularInput" + val);
                hardWait(1);
                waitUntilSpinnerNotVisible();
                try {
                    edriver.findElement(By.xpath("//div[contains(@id, '" + id + "')]")).findElements(By.tagName("table")).get(row + 1).findElement(By.tagName("tr")).findElements(By.tagName("td")).get(index).findElements(By.tagName("div")).get(0);
                    actions.click(edriver.findElement(By.xpath("//div[contains(@id, '" + id + "')]")).findElements(By.tagName("table")).get(row + 1).findElement(By.tagName("tr")).findElements(By.tagName("td")).get(index).findElements(By.tagName("div")).get(0)).perform();
                    waitUntilSpinnerNotVisible();
                } catch (Exception ex) {
                    try {
                        edriver.findElement(By.xpath("//div[contains(@id, '" + id + "')]")).findElements(By.tagName("table")).get(row - 1).findElement(By.tagName("tr")).findElements(By.tagName("td")).get(index).findElements(By.tagName("div")).get(0);
                        actions.click(edriver.findElement(By.xpath("//div[contains(@id, '" + id + "')]")).findElements(By.tagName("table")).get(row - 1).findElement(By.tagName("tr")).findElements(By.tagName("td")).get(index).findElements(By.tagName("div")).get(0)).perform();
                        waitUntilSpinnerNotVisible();
                    } catch (Exception e) {
                    }
                }
                enterTabularInput(id, row, index, val);
            }
            retryCounter = 0;
            waitUntilSpinnerNotVisible();
            return;
        }
    }

    public void waitUntilSpinnerNotVisible() {
        boolean blFlag = true;
        edriver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        while (blFlag) {
            try {
                edriver.findElement(By.xpath("//span[@id='ajax-sub-pre-loading']"));
                blFlag = true;
                Thread.sleep(150);
            } catch (Exception e) {
                blFlag = false;
            }
        }
        edriver.manage().timeouts().implicitlyWait(TestRunConfig.IMPLICIT_TIME_OUT, TimeUnit.SECONDS);
    }

    public boolean isCurrentlyVisible(WebElement... element) {
        edriver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        for (WebElement curr : element) {

            try {
                if (!curr.isDisplayed())
                    return false;
            } catch (Exception exp) {
                edriver.manage().timeouts().implicitlyWait(TestRunConfig.IMPLICIT_TIME_OUT, TimeUnit.SECONDS);
                return false;
            }
        }
        return true;
    }

    public boolean isCurrentlyChecked(WebElement checkboxWE) {
        ((JavascriptExecutor) edriver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });", checkboxWE);
        if ((((checkboxWE.getAttribute("checked") != null)))) {
            return true;
        } else {
            return false;
        }
    }

    public void verifyThePresenceOfTableHeaders(List<String> tableHeaders) {
        for (String tableHeader : tableHeaders) {
            WebElement element = edriver.findElement(By.xpath("//span[text()='" + tableHeader + "']"));
            Assert.assertTrue("Table Header" + tableHeader + "is not present on the page", element.isDisplayed());
        }
    }

    public void verifyTheTabularTextFollowingTo(String baseText, int byNoOfColumns, String expectedText) {
        String actualText = edriver.findElement(By.xpath("//div[contains(text(),'" + baseText + "')]/../following-sibling::td[" + byNoOfColumns + "]/div")).getText();
        Assert.assertTrue("Verification Failed as" + actualText + " and " + expectedText + " are differnt", actualText.equalsIgnoreCase(expectedText));
    }

    public void verifyTheTextFollowingToLink(String baseText, int byNoOfColumns, String expectedText) {
        String actualText = edriver.findElement(By.xpath("//div[contains(text(),'" + baseText + "')]/../following-sibling::td[" + byNoOfColumns + "]/div")).getText();
        Assert.assertTrue("Verification Failed as" + actualText + " and " + expectedText + " are different", actualText.equalsIgnoreCase(expectedText));
    }

    public void verifyTheTextPrecedingToLink(String baseText, int byNoOfColumns, String expectedText) {
        String actualText = edriver.findElement(By.xpath("//div[contains(text(),'" + baseText + "')]/../preceding-sibling::td[" + byNoOfColumns + "]/div")).getText();
        Assert.assertTrue("Verification Failed as" + actualText + " and " + expectedText + " are different", actualText.equalsIgnoreCase(expectedText));
    }


    public void verifyThePresenceOfElementContainingTheTexts(String textsSeparatedByColon) {
        String xpath = "";
        if (!textsSeparatedByColon.contains(":")) {
            xpath = "//*[contains(text(),'" + textsSeparatedByColon + "')]";
        } else {
            String[] texts = textsSeparatedByColon.split(":");
            xpath = "//*[contains(text(),'" + texts[0] + "')]";
            for (int i = 1; i < texts.length; i++) {
                xpath = xpath + "[contains(text(),'" + texts[i] + "')]";
            }
        }
        WebElement element = edriver.findElement(By.xpath(xpath));
        Assert.assertTrue("Element containing the texts is not present on the page", element.isDisplayed());
    }

    public boolean isTextDisplayed(String text) {

        try {
            hardWait(5);
            String xpath = "//*[contains(text(),\"" + text.trim() + "\")]";
            List<WebElement> webElementList = waits.until_elements_count_gt_0(By.xpath(xpath));
            boolean flag = false;
            for (WebElement element : webElementList) {
                if (element.isDisplayed() || element.isEnabled()) {
                    flag = true;
                    break;
                }
            }
            return flag;
        } catch (Exception exp) {
            return false;
        }
    }

    public void verifyTheTabularTextPrecedingTo(String baseText, int byNoOfColumns, String expectedText) {
        String actualText = edriver.findElement(By.xpath("//div[contains(text(),'" + baseText + "')]/../preceding-sibling::td[" + byNoOfColumns + "]/div")).getText();
        Assert.assertTrue("Verification Failed as" + actualText + " and " + expectedText + " are different", actualText.equalsIgnoreCase(expectedText));
    }

    public void clickTextFromTabular(String baseText, int byNoOfColumns) {
        retryCounter++;
        if (retryCounter == 3) {
            retryCounter = 0;
            Assert.fail("Can't click " + this + " Session ID: " + getSessionId() + " Current page:" + pageName());
        }
        try {
            WebElement element = edriver.findElement(By.xpath("//div[contains(text(),'" + baseText + "')]/../preceding-sibling::td[" + byNoOfColumns + "]//div/span"));
            element.click();
        } catch (Exception ex) {
            hardWait(1);
            clickTextFromTabular(baseText, byNoOfColumns);
        }
        retryCounter = 0;
        return;

    }

    public void validateDropdown(String dropdownValues) {
        if (dropdownValues != null && !dropdownValues.isEmpty()) {
            List<WebElement> webElements;
            List<String> dropdownValueList = new ArrayList<>();
            List<String> arrayList = new ArrayList<>();
            String[] array = dropdownValues.split(",");
            for (String value : array) arrayList.add(value);

            webElements = edriver.findElements(By.xpath("//ul[contains(@id, '-picker-listEl')]")).get(edriver.findElements(By.xpath("//ul[contains(@id, '-picker-listEl')]")).size() - 1).findElements(By.tagName("li"));
            for (WebElement ebElement : webElements) dropdownValueList.add(ebElement.getText());
            Assert.assertTrue("No error message displayed", dropdownValueList.containsAll(arrayList));
        }
    }

    public void validateExceptionMessage(String validateMessage) {
        String[] messageArray = validateMessage.split(",");
        String errorMessage = "";
        edriver.findElements(By.xpath("//ul[contains(@id, 'os-messages')]")).get(0).click();
        List<WebElement> webElements = edriver.findElements(By.xpath("//ul[contains(@id, 'os-messages')]")).get(0).findElements(By.tagName("li"));
        for (int i = 1; i < webElements.size(); i++) {
            errorMessage = errorMessage + (webElements.get(i).findElement(By.tagName("span")).getText());
        }
        for (int i = 0; i < messageArray.length; i++) {
            String message = messageArray[i];
            Assert.assertTrue(message + " error message displayed", errorMessage.contains(message));
        }
    }

    public void clickOnTheTabularCheckBoxFollowingTo(String baseText, int byNoOfColumns) {
        retryCounter++;
        if (retryCounter == 3) {
            retryCounter = 0;
            Assert.fail("Can't click " + this + " Session ID: " + getSessionId() + " Current page:" + pageName());
        }
        try {

            String inputBox = "//div[contains(text(),'" + baseText + "')]/../following-sibling::td[" + byNoOfColumns + "]/div/input";
            edriver.findElement(By.xpath(inputBox)).click();
        } catch (Exception ex) {
            hardWait(1);
            clickOnTheTabularCheckBoxFollowingTo(baseText, byNoOfColumns);
        }
        retryCounter = 0;
        return;
    }


    public void clickOnTheTabularCheckBoxPrecedingTo(String baseText, int byNoOfColumns) {
        retryCounter++;
        if (retryCounter == 3) {
            retryCounter = 0;
            Assert.fail("Can't click " + this + " Session ID: " + getSessionId() + " Current page:" + pageName());
        }
        try {

            String inputBox = "//div[contains(text(),'" + baseText + "')]/../preceding-sibling::td[" + byNoOfColumns + "]/div/span";
            edriver.findElement(By.xpath(inputBox)).click();
        }
        catch (StaleElementReferenceException e){
            hardWait(3);
        }
        catch (Exception ex) {
            hardWait(1);
            clickOnTheTabularCheckBoxPrecedingTo(baseText, byNoOfColumns);
        }
        retryCounter = 0;
        return;
    }

    public void clickWithValue(String xpath, String placeholder, String replacement) {
        xpath = xpath.replace(placeholder, replacement);
        By bySelector = By.xpath(xpath);
        actions.click(edriver.findElement(bySelector)).perform();
        waitUntilSpinnerNotVisible();
    }

    public void clickCheckBoxOnTable(String baseText, int byNoOfColumns) {
        retryCounter++;
        if (retryCounter == 3) {
            retryCounter = 0;
            Assert.fail("Can't click " + this + " Session ID: " + getSessionId() + " Current page:" + pageName());
        }
        retryCounter = 0;
        try {
            WebElement element = edriver.findElement(By.xpath("//div[contains(text(),'" + baseText + "')]/../preceding-sibling::td[" + byNoOfColumns + "]//span"));

            actions.click(element).perform();
        } catch (Exception ex) {
            hardWait(1);
            clickCheckBoxOnTable(baseText, byNoOfColumns);
        }
        return;
    }


    public void verifyTheTabularLinkPrecedingTo(String baseLinkText, int byNoOfColumns, String expectedLinkText) {
        String actualText = edriver.findElement(By.xpath("//span[contains(text(),'" + baseLinkText + "')]/../../preceding-sibling::td[" + byNoOfColumns + "]/div/span")).getText();
        Assert.assertTrue("Verification Failed as" + actualText + " and " + expectedLinkText + " are different", actualText.equalsIgnoreCase(expectedLinkText));
    }

    public void verifyTheTabularLinkPrecedingToText(String baseText, int byNoOfColumns, String expectedLinkText) {
        String actualText = edriver.findElement(By.xpath("//div[contains(text(),'" + baseText + "')]/../preceding-sibling::td[" + byNoOfColumns + "]/div/span")).getText();
        Assert.assertTrue("Verification Failed as" + actualText + " and " + expectedLinkText + " are different", actualText.equalsIgnoreCase(expectedLinkText));
    }

    public void verifyTheTabularTextPrecedingToText(String baseText, int num, int byNoOfColumns, String expectedBaseText) {
        String actualText = edriver.findElement(By.xpath("(//div[contains(text(),'" + baseText + "')])[" + num + "]//../preceding-sibling::td[" + byNoOfColumns + "]/div")).getText();
        Assert.assertTrue("Verification Failed as" + actualText + " and " + expectedBaseText + " are different", actualText.equalsIgnoreCase(expectedBaseText));
    }

    public void clickOnTheTabularLinkPrecedingTo(String baseText, int byNoOfColumns) {
        try{
            edriver.findElement(By.xpath("//div[contains(text(),'" + baseText + "')]/../preceding-sibling::td[" + byNoOfColumns + "]//span")).click();
        }catch(StaleElementReferenceException e){
            edriver.findElement(By.xpath("//div[contains(text(),'" + baseText + "')]/../preceding-sibling::td[" + byNoOfColumns + "]//span")).click();
        }
    }

    public void clickOnTheTabularLinkPrecedingToLink(String linkText, int byNoOfColumns) {
        edriver.findElement(By.xpath("//span[contains(text(),'" + linkText + "')]/../../preceding-sibling::td[" + byNoOfColumns + "]//span")).click();
    }

    public void selectTabularDropDown(String id, int row, int col, String value) {
        if (value != null && !value.isEmpty()) {
            retryCounter++;
            if (retryCounter == 3) {
                retryCounter = 0;
                Assert.fail("Can't click in the dropdown " + this + "" + value + " Session ID: " + getSessionId() + " Current page:" + pageName());
            }
            try {
                WebElement element = edriver.findElement(By.xpath("//div[contains(@id, '" + id + "')]")).findElements(By.tagName("table")).get(row).findElement(By.tagName("tr")).findElements(By.tagName("td")).get(col).findElements(By.tagName("div")).get(0);
                actions.click(element).perform();
                waitUntilSpinnerNotVisible();
                actions.click(edriver.findElement(By.xpath("//li[contains(text(),'" + value + "')]"))).perform();

            } catch (Exception ex) {
                hardWait(1);
                selectTabularDropDown(id, row, col, value);
            }
            retryCounter = 0;
            return;
        }
    }

    public void acceptAlert() {
        edriver.switchTo().alert().accept();
    }

    public void selectTabularValueFromDropDownFollowingTo(String baseText, int byNoOfColumns, String dropDownValue) {
        if (dropDownValue != null && !dropDownValue.isEmpty()) {
            retryCounter++;
            if (retryCounter == 3) {
                retryCounter = 0;
                Assert.fail("Can't select " + dropDownValue + " From " + this + " Session ID: " + getSessionId() + " Current page:" + pageName());
            }
            try {
                String inputBox = "//div[contains(text(),'" + baseText + "')]/../following-sibling::td[" + byNoOfColumns + "]/div";
                WebElement dropdown = edriver.findElement(By.xpath("//div[contains(text(),'" + baseText + "')]/../following-sibling::td[" + byNoOfColumns + "]"));
                if (dropdown.getAttribute("class").contains("Editable")) {
                    edriver.findElement(By.xpath(inputBox)).click();
                    hardWait(1);
                    edriver.findElement(By.xpath("//li[text()='" + dropDownValue + "']")).click();
                }
            } catch (Exception ex) {
                hardWait(1);
                actions.sendKeys(Keys.TAB).perform();
                selectTabularValueFromDropDownFollowingTo(baseText, byNoOfColumns, dropDownValue);
            }
            retryCounter = 0;
            return;
        }

    }


    public void enterTabularTextFollowingTo(String baseText, int byNoOfColumns, String inputText) {

        retryCounter++;
        if (retryCounter == 3) {
            retryCounter = 0;
            Assert.fail("Can't enter " + inputText + " into text box " + this + " Session ID: " + getSessionId() + " Current page:" + pageName());
        }
        try {
            String inputBoxXpath = "//div[contains(text(),'" + baseText + "')]/../following-sibling::td[" + byNoOfColumns + "]/div";
            WebElement inputBox = edriver.findElement(By.xpath(inputBoxXpath));
            clickTypeAndTab(inputText, inputBox);

        } catch (Exception ex) {
            hardWait(1);
            actions.sendKeys(Keys.TAB).perform();
            enterTabularTextFollowingTo(baseText, byNoOfColumns, inputText);
        }
        retryCounter = 0;
        return;
    }

    public String getPriceFormat(String price) {
        if (price == null || price.isEmpty()) {
            return price;
        }
        double limit = Double.parseDouble(price.replace(",", ""));
        if (limit != 1000) {
            DecimalFormat formatter = new DecimalFormat("###,###,###.##");
            return formatter.format(limit);
        } else {
            DecimalFormat formatter = new DecimalFormat("###,##,####.##");
            return formatter.format(limit);

        }
    }

    public void clearBrowserCache() {
        edriver.manage().deleteAllCookies(); //delete all cookies
        hardWait(7); //wait 7 seconds to clear cookies.
    }


    public void selectDropDownValueOnTable(String divValue, int tableValue, int trValue, int tdValue, String value) {
        if (!value.isEmpty()) {
            retryCounter++;
            if (retryCounter == 3) {
                retryCounter = 0;
                Assert.fail("Can't click " + value + " From " + this);
            }
            try {
                System.out.println("Enter the TryBlock of it");
                WebElement element = edriver.findElement(By.xpath("//div[contains(@id,'" + divValue + "')]//table['" + tableValue + "']//tr[" + trValue + "]/td[" + tdValue + "]//input"));
                WebElement dropdown = edriver.findElement(By.xpath("//div[contains(@id,'" + divValue + "')]//table['" + tableValue + "']//tr[" + trValue + "]/td[" + tdValue + "]"));
                if (dropdown.getAttribute("class").contains("x-table-layout-cell")) {
                    element.click();
                    hardWait(1);
                    edriver.findElement(By.xpath("//li[text()='" + value + "']")).click();
                }
            } catch (Exception ex) {
                hardWait(1);
                actions.sendKeys(Keys.TAB).perform();
                selectDropDownValueOnTable(divValue, tableValue, trValue, tdValue, value);
            }
            retryCounter = 0;
            return;
        }
    }


    /**
     * This method is used to click on checkbox in table based on row and index
     *
     * @param id
     * @param row
     * @param index
     */

    public void clickTabularCheckbox(String id, int row, int index) {
        retryCounter++;
        if (retryCounter == 3) {
            retryCounter = 0;
            Assert.fail("Can't click " + this + " Session ID: " + getSessionId() + " Current page:" + pageName());
        }
        try {
            actions.click(edriver.findElement(By.xpath("//div[contains(@id, '" + id + "')]")).findElements(By.tagName("table")).get(row).findElement(By.tagName("tr")).findElements(By.tagName("td")).get(index).findElements(By.tagName("div")).get(0).findElement(By.tagName("input"))).perform();
        } catch (Exception ex) {
            try {
                actions.click(edriver.findElement(By.xpath("//div[contains(@id, '" + id + "')]")).findElements(By.tagName("table")).get(row + 1).findElement(By.tagName("tr")).findElements(By.tagName("td")).get(index).findElements(By.tagName("div")).get(0).findElement(By.tagName("input"))).perform();
            } catch (Exception e) {
                clickTabularCheckbox(id, row, index);
            }
            waitUntilSpinnerNotVisible();
        }
        retryCounter = 0;
    }

    public boolean isValueVisible(String xpath, String placeholder, String replacement) {
        xpath = xpath.replace(placeholder, replacement);
        By bySelector = By.xpath(xpath);
        hardWait(1);
        try {
            if (!edriver.findElement(bySelector).isDisplayed())
                return false;
        } catch (Exception exp) {
            return false;
        }
        return true;
    }


    public String getSessionId () {
        String sessionId = "Not captured";
        boolean ajaxerror =false;
        try {
            edriver.findElement(By.xpath("//*[contains(text(),'Ajax communication failed')]"));
            DB_Loader.insertErrors(new CustomError("Environment:Ajax communication failed", sessionId));
            ajaxerror=true;
            actions.keyDown(Keys.CONTROL).keyDown(Keys.SHIFT).sendKeys("L").keyUp(Keys.CONTROL).keyUp(Keys.SHIFT).perform();
            sessionId = edriver.findElement(By.xpath("//*[contains(@id,'diagnos-status-targetEl')]")).getText();
            Assert.fail("Test case failed with ajax error" + " Session ID: " + sessionId + " Current page:" + pageName());
        } catch (Exception e) {
            try {
                actions.keyDown(Keys.CONTROL).keyDown(Keys.SHIFT).sendKeys("L").keyUp(Keys.CONTROL).keyUp(Keys.SHIFT).perform();
                sessionId = edriver.findElement(By.xpath("//*[contains(@id,'diagnos-status-targetEl')]")).getText();
                getVisibleElement(edriver.findElements(By.xpath("//*[contains(@class,'x-window-default-closable')]//div[contains(@class,'x-tool-close')]"))).click();
                log.info(sessionId);
                if(ajaxerror)
                    Assert.fail("Test case failed with ajax error" + " Session ID: " + sessionId + " Current page:" + pageName());
            } catch (Exception ignored) {
            }
        }
        return sessionId;
    }

    public String pageName() {
        String page = "None";
        String submissionId="None";
        try {
            page = edriver.findElement(By.xpath("//*[@class='x-component currentStep x-box-item x-toolbar-item x-component-default']")).getText();
            submissionId=edriver.findElement(By.xpath("//span[contains(text(),'S100.')]")).getText();
            return page+" and SubmissionId: "+submissionId;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                page = edriver.findElement(By.xpath("/html/body/table/tbody/tr/td/center")).getText();
            } catch (Exception ignored) {
            }
        }

        return page;
    }

    public String class_name(WebElement element) {
        String findBy = "notFound";
        try {
            java.lang.reflect.Field field = element.getClass().getDeclaredField("foundBy");
            field.setAccessible(true);
            findBy = field.get(element).toString();
        } catch (Exception ignored) {
        }
        return findBy;
    }


    public void clickRadioButtonOnTable(String baseText, int byNoOfColumns, int byNoOfColumn) {
        retryCounter++;
        if (retryCounter == 3) {
            retryCounter = 0;
            Assert.fail("Can't click " + this);
        }
        try {
            WebElement element = edriver.findElement(By.xpath("//span[contains(text(),'" + baseText + "')]//ancestor::td[" + byNoOfColumns + "]/preceding-sibling::td[" + byNoOfColumn + "]/div/span"));
            element.click();
        } catch (Exception ex) {
            hardWait(2);
            clickRadioButtonOnTable(baseText, byNoOfColumns, byNoOfColumn);
        }
        retryCounter = 0;
        return;
    }

    /**
     * Used to select one of (agree/Yes or disagree/No) radio group
     *
     * @param tableId - variable holding table id
     * @param val     - Variable holding "agree/Yes" or "disagree/No"
     */

    public void selectRadioInput(String tableId, String val) {
        if (val != null && !val.isEmpty()) {
            try {
                if ((val.equalsIgnoreCase("Yes")) || (val.equalsIgnoreCase("Agree"))) {
                    edriver.findElement(By.xpath("//table[contains(@id,'" + tableId + "')]//tbody/tr/td[1]/div//div/span/input")).click();
                    hardWait(1);
                } else {
                    edriver.findElement(By.xpath("//table[contains(@id,'" + tableId + "')]//tbody/tr/td[2]/div//div/span/input")).click();
                    hardWait(1);
                }
            } catch (Exception e) {
                Assert.fail("Can't click radio button" + this);
            }
        }
    }
    public void clickButtonFromTabularFollowingTo (String baseText, int byNoOfColumns) {
        retryCounter++;
        if (retryCounter == 3) {
            retryCounter = 0;
            Assert.fail("Can't click " + this + " Session ID: "+ getSessionId()+ " Current page:" + pageName());
        }
        try {
            WebElement element = edriver.findElement(By.xpath("//div[contains(text(),'" + baseText + "')]/../following-sibling::td[" + byNoOfColumns + "]//div//button"));
            element.click();
        } catch (Exception ex) {
            hardWait(1);
            clickButtonFromTabularFollowingTo(baseText, byNoOfColumns);
        }
        retryCounter = 0;
        return;

    }

    public void enterTabularText (String id, int row, int col, String value) {
        if (value != null && !value.isEmpty()) {
            retryCounter++;
            if (retryCounter == 3) {
                retryCounter = 0;
                Assert.fail("Can't enter " + value + " into text box " + this);
            }
            try {

                String inputBox = "//div[contains(@id,'" + id + "')]//table[" + row + "]//td[" + col + "]/div";
                String input = "//div[contains(@id,'" + id + "')]//table[" + row + "]//td[" + col + "]/div//input";
                edriver.findElement(By.xpath(inputBox)).click();
                hardWait(1);
                ((JavascriptExecutor) edriver).executeScript("arguments[0].scrollIntoView();", edriver.findElement(By.xpath(input)));
                edriver.findElement(By.xpath(input)).sendKeys(value);
                ;
            } catch (Exception ex) {
//                tester.logsException("Retry enterTabularText",ex);
                hardWait(1);
                actions.sendKeys(Keys.TAB).perform();
                enterTabularText(id, row, col, value);
            }
            retryCounter = 0;
            return;
        }
    }

    public boolean isTextVisible(WebElement textWE) {
        waits.until_element_displayed(textWE);
        try {
            String text = textWE.getText();
            if(text!=null || !text.isEmpty()){
                return true;
            }
            else{
                return false;
            }
        } catch (Exception exp) {
            log.error(exp.getMessage() + "exception while getting the text from" + textWE);
            return false;
        }
    }

    public boolean isTextNotVisible(WebElement textWE) {
        waits.until_element_displayed(textWE);
        try {
            String text = textWE.getText();
            if(text==null || text.isEmpty() || text==""){
                return true;
            }
            else{
                return false;
            }
        } catch (Exception exp) {
            log.error(exp.getMessage() + "exception while getting the text from" + textWE);
            return false;
        }
    }

    /**
     * Verify base Text present in Table
     * if Yes
     *check expected present in the table
     *
     */
    public void verifyPresenceOfExpectedTextInTable (String id,String baseText , int baseTextIndex,int targetIndex,String expectedText) {
        if (baseText != null && !baseText.isEmpty()) {
            retryCounter++;
            if (retryCounter == 3) {
                retryCounter = 0;
                Assert.fail("Can't click " + this);
            }
            try {
                List<WebElement> elements=edriver.findElements(By.xpath("//div[contains(@id, '" + id + "')]//table"));
                for(int i=1;i<=elements.size();i++){
                    String actualBaseText=edriver.findElement(By.xpath("//div[contains(@id, '" + id + "')]//table["+i+"]//tbody//tr//td["+baseTextIndex+"]//span")).getText();

                    if(actualBaseText.contains(baseText)){

                        String actualTargetText=edriver.findElement(By.xpath("//div[contains(@id, '" + id + "')]//table["+i+"]//tbody//tr//td["+targetIndex+"]//div")).getText();
                        Assert.assertTrue("Verification Failed as" + actualTargetText + " and " + expectedText + " are different", actualTargetText.equalsIgnoreCase(expectedText));
                        break;
                    }
                }

            } catch (Exception exp) {
                log.error(exp.getMessage() + "while capturing" + expectedText);
                hardWait(1);
                verifyPresenceOfExpectedTextInTable(id,baseText,baseTextIndex, targetIndex,expectedText);
            }
            retryCounter = 0;
            return;
        }
        waitUntilSpinnerNotVisible();
    }


    /**
     * Verify Expected Text present in Table
     * Click on link
     * Expected Task
     * If -Navigate Back
     *
     *
     */
    public void verifyAndClickExpectedTabularLinkText (String baseText, int byNoOfColumns, String expectedText,WebElement expectedTargetElement,WebElement ifNotNavigateback) {

        for (int i=1;i<=byNoOfColumns;i++){
            WebElement element=edriver.findElement(By.xpath("//table[" +i+ "]//tbody//tr//td//div//span[contains(text(),'" + baseText + "')]"));

            if(element.getText().contains(baseText))
                element.click();
            hardWait(3);
            String ActualText=expectedTargetElement.getText();
            hardWait(2);
            if(ActualText.contains(expectedText))
                break;

            ifNotNavigateback.click();;
        }


    }

    public void windowFocus() {
        ((JavascriptExecutor) edriver).executeScript("window.focus();");
    }
    public void verifyThePresenceOfElementContainingTheText(String text) {
        WebElement element = edriver.findElement(By.xpath("//*[contains(text(),'" + text + "')]"));
        Assert.assertTrue("Element containing the text" + text + "is not present on the page", element.isDisplayed());
    }
    public void selectByClicking(WebElement parentEle ,String value) {
        WebElement childEle=null;
        if (value != null && !value.isEmpty()) {
            retryCounter++;
            if (retryCounter == 4) {
                retryCounter = 0;
                Assert.fail("Not able to find " + value + " in to the field " + this);
            }



            if (retryCounter == 3) {
                waitUntilSpinnerNotVisible();
                childEle = edriver.findElement(By.xpath("//li[contains(text(),'" + value + "')]"));

            }
            try {
                clickAndWait(parentEle);
                edriver.findElement(By.xpath("//li[contains(text(),'" + value + "')]")).click();
                waitUntilSpinnerNotVisible();

            } catch (Exception e) {
                selectByClicking(parentEle,value);
            }
            retryCounter = 0;
            return;
        }
    }
    public void selectByTypingValue(WebElement element ,String value) {
        retryCounter++;
        if (value != null && !value.isEmpty()) {
            if (retryCounter == 3) {
                retryCounter = 0;
                Assert.fail("Not able to find " + value + " in to the field " + this);
            }
            try {
                if(element.getTagName().equals("input"))
                {
                    clickTypeAndTab(value,element);
                } else {
                    By bySelector = By.xpath("/div[@id='os-title-bar-innerCt'");
                    WebElement innerElement = (WebElement) element.findElement(bySelector);
                    clickTypeAndTab(value,innerElement);

                }

            } catch (Exception e) {
// tester.logsException("Retry selectByTypingValue",e);
                selectByTypingValue(element,value);
            }
            retryCounter = 0;
            return;
        }
    }
    public void clickJavaScriptAndWait(WebElement element){
        waitUntilSpinnerNotVisible();

        String id = element.getAttribute("id");

        try {
            ((JavascriptExecutor)this.driver).executeScript("document.getElementById(arguments[0]).click()", new Object[]{id});
        } catch (Exception var3) {
            //this.tester = (Tester)Instrumented.instanceOf(Tester.class).withProperties(new Object[0]);
            //this.tester.logsException("Error while clicking element using javascript", var3);
        }

        waitUntilSpinnerNotVisible();
    }
    public void enterInputValueOnTable(String id,int tableValue, int row, int col, String val){

        if (val != null && !val.isEmpty()) {
            retryCounter++;
            if (retryCounter == 3) {
                retryCounter = 0;
                Assert.fail("Can't click " + this);
            }
            try {
                edriver.findElement(By.xpath("//div[contains(@id, '" + id + "')]//table[" + tableValue + "]//tr[" + row + "]//td[" + col + "]//div")).click();

                edriver.findElement(By.xpath("//div[contains(@id, '" + id + "')]//table[" + tableValue + "]//tr[" + row + "]//td[" + col + "]//div//input")).sendKeys(val);

            } catch (Exception ex) {
//                tester.logsException("Retry enterInputValueOnTable");
                waitFor(1);
                enterInputValueOnTable(id, tableValue, row, col, val);
            }

        }

    }
    public boolean isDataFilledFor(WebElement element){
        waitUntilSpinnerNotVisible();
        boolean isNonEmptyField=false;
        String tagName=element.getTagName();
        if(tagName.equals("input")){
            String fieldType= element.getAttribute("type");
            if(fieldType.equals("text")){
                try{
                    String value= element.getAttribute("value");
                    if(value.length()>0 && !(value.equalsIgnoreCase("-Select-"))){
                        isNonEmptyField=true;
                    }
                }catch(Exception e){
                    isNonEmptyField=false;
                }
            } else if(fieldType.equals("checkbox") || fieldType.equals("radio")){
                try{
                    String value= element.getAttribute("checked");
                    if (value.equals("checked")) {
                        isNonEmptyField = true;
                    }
                }catch(Exception e){
                    isNonEmptyField=false;
                }
            }
        }else if(tagName.equals("div")){
            try {
                String fieldValue = element.getText();
                if (fieldValue.trim().length() > 0) {
                    isNonEmptyField = true;
                } else {
                    isNonEmptyField = false;
                }
            }catch(Exception e){
                isNonEmptyField=false;
            }
        }
        return isNonEmptyField;
    }
    public void inputToActiveElement(String input,WebElement inputWE){
        if (input != null && !input.isEmpty()) {
            retryCounter++;
            if (retryCounter == 3) {
                retryCounter = 0;
                Assert.fail("Can't click " + inputWE);
            }
            try {
                ((JavascriptExecutor) edriver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });", inputWE);
                actions.moveToElement(inputWE).pause(Duration.ofSeconds(2)).click(inputWE).perform();
                actions.sendKeys(input).perform();
            } catch (Exception exp) {
                log.error(exp.getMessage());
                clickTypeAndTab(input, inputWE);
            }
            retryCounter = 0;
            return;
        }
    }
    public int getTheTabularIndexOfBlankValue (String id, int noOfRows, int col) {
        int index = 0;
        for (int row = 1; row <= noOfRows; row++) {
            String inputBox = "//div[contains(@id,'" + id + "')]//table[" + row + "]//td[" + col + "]/div";
            String value = edriver.findElement(By.xpath(inputBox)).getText();
            System.out.println(edriver.findElement(By.xpath(inputBox)).getText().length());
            if ((value.length() <= 1) || value.equalsIgnoreCase("- Select -")||value.isEmpty()||value==null) {
                index = row;
                break;
            }
        }
        return index;
    }
    public void selectListValueUsingContains(WebElement element,String value) {
        if (value != null && !value.isEmpty()) {
            retryCounter++;
            if (retryCounter == 3) {
                retryCounter = 0;
                Assert.fail("Not able to find " + value + " in to the field " + this);
            }
//By bySelector = By.xpath("//li[contains(text(),'" + value + "')]");

            try {
                clickAndWait(element);
                driver.findElement(By.xpath("//li[contains(text(),'" + value + "')]")).click();

            } catch (Exception e) {
                selectListValueUsingContains(element,value);
            }
            retryCounter = 0;
            return;
        }
    }
    public void selectRadioInputOfText (String text, String tableId) {
        if (text != null && !text.isEmpty()) {
            actions.click(edriver.findElement(By.xpath("//table[contains(@id,'" + tableId + "')]//label[text()='" + text + "']/../span/input"))).perform();
            waitUntilSpinnerNotVisible();
            hardWait(1);
        }

    }

}

