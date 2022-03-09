package utils.web;

import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import utils.TestRunConfig;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;

@Component
@ScenarioScope
public class Waits {
    public Wait<WebDriver> fluentWait;
    BrowserInit browserInit = null;
    private EventFiringWebDriver edriver = null;
    private WebDriverWait explicitWait;

    @Autowired @Lazy
    Waits(BrowserInit browserInit) {
        this.browserInit = browserInit;
        edriver = browserInit.getEdriver();
        explicitWait = new WebDriverWait(browserInit.getEdriver(), TestRunConfig.EXPLICIT_TIME_OUT);
        fluentWait = new FluentWait<WebDriver>(browserInit.getEdriver()).
                withTimeout(Duration.of(TestRunConfig.FLUENT_TIME_OUT, ChronoUnit.SECONDS)).pollingEvery(Duration.of(1, ChronoUnit.SECONDS)).
                ignoring(StaleElementReferenceException.class);
    }

    public WebElement until_visibility_of_element(WebElement element) {
        return fluentWait.until(ExpectedConditions.visibilityOf(element));
    }

    public List<WebElement> until_visibility_of_all_elements(List<WebElement> webElementList) {
        return fluentWait.until(ExpectedConditions.visibilityOfAllElements(webElementList));
    }

    public boolean until_element_displayed(WebElement element) {
        WebElement ele;
        try {
            ele = fluentWait.until(ExpectedConditions.visibilityOf(element));
            return ele.isDisplayed();
        } catch (Exception exp) {
            return false;
        }
    }

    public List<WebElement> until_elements_count_gt_x(By by, int x) {
        return explicitWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(by, x));
    }

    public List<WebElement> until_elements_count_gt_0(By by) {
        return explicitWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(by, 0));
    }

    public WebElement until_element_clickable(WebElement element) {
        ((JavascriptExecutor) edriver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });", element);
        return fluentWait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public Boolean until_invisibility_of_element(WebElement element) {
        return explicitWait.until(ExpectedConditions.invisibilityOf(element));
    }

    public void click_element_ignore_stale_exception(Object identifier) {
        WebElement element = fluentWait.until(new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                return driver.findElement((By) identifier);
            }
        });
        try {
            element.click();
        } catch (StaleElementReferenceException exp) {
        }
    }
}