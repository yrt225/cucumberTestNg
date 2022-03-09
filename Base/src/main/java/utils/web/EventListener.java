package utils.web;

import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class EventListener extends AbstractWebDriverEventListener {
    private Logger log = LoggerFactory.getLogger(EventListener.class);

    @Override
    public void beforeNavigateTo(String s, WebDriver webDriver) {
        log.info("Before navigateTo:" + s);
    }

    @Override
    public void afterNavigateTo(String s, WebDriver webDriver) {
        log.info("After navigateTo:" + s);
    }

    @Override
    public void beforeFindBy(By by, WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void afterFindBy(By by, WebElement webElement, WebDriver webDriver) {
        if(webElement!=null && webElement.isDisplayed()) {
            ((JavascriptExecutor)webDriver).executeScript("arguments[0].style.border='3px solid green'",webElement);
        }
    }

    @Override
    public void beforeClickOn(WebElement webElement, WebDriver webDriver) {
//        log.info("Before clicking element:" + webElement.getText());
    }

    @Override
    public void afterClickOn(WebElement webElement, WebDriver webDriver) {
        if(webElement!=null && webElement.isDisplayed()) {
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].style.border='3px solid green'", webElement);
        }
    }
    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        if (keysToSend != null) {
            ((JavascriptExecutor)driver).executeScript("arguments[0].style.border='3px solid green'",element);
            log.info("After change of value to:" + keysToSend[0]);
        }
    }

    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        if (keysToSend != null)
            log.info("Before change of value to:" + keysToSend[0]);
    }

    @Override
    public void onException(Throwable throwable, WebDriver driver) {
        if(throwable.getMessage().contains("//span[@id='ajax-sub-pre-loading']")){
            log.error("Spinner is not displayed");
        }else{
            throwable.printStackTrace();
        }
    }
}