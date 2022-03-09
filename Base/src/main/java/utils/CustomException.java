package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public class CustomException {
    ReusableMethods reusableMethods;

    public CustomException(ReusableMethods reusableMethods) {
        this.reusableMethods = reusableMethods;
    }

    public CustomException(EventFiringWebDriver edriver, String message) {
        if (reusableMethods.isDisplayed(By.xpath("//div[contains(text(),'Ajax communication failed')]"))) {
            throw new MyException(String.format("Environment:Ajax communication failed"));
        } else {
            throw new MyException(message);
        }
    }
}

class MyException extends RuntimeException {
    public MyException(String message) {
        super(message);
    }
}