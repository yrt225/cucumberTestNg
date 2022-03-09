package utils.web;

import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import utils.TestRunConfig;
import utils.state.TestContext;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
@Component
@ScenarioScope
public class BrowserInit {
	private static final Logger log = LoggerFactory.getLogger(BrowserInit.class);

	private WebDriver driver = null;
	private EventFiringWebDriver edriver = null;
	private String scenarioName = null;

	@Autowired @Lazy
	private TestContext testContext;
	@Autowired @Lazy
	private EventListener eventListener = null;

	public String getScenarioName () {
		return scenarioName;
	}

	public void setScenarioName (String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public EventFiringWebDriver getEdriver () {
		return edriver;
	}

	public void openBrowser () {
		DesiredCapabilities caps;
		FirefoxOptions firefoxOptions = null;
		ChromeOptions options = new ChromeOptions();
		/*
		 * Change download directory for forms
		 */
		if(testContext.getBatchName()!=null) {
			String downloadFilepath = "target/" + testContext.getBatchName()+File.separator;
			File file = new File(downloadFilepath);

			if(file.exists()) {
				try{FileUtils.deleteDirectory(new File("target" + File.separator + testContext.getBatchName()));}
				catch (IOException exp){ log.error("deletion of folder failed:"+testContext.getBatchName());}

			}
			file.mkdirs();
			testContext.setDownloadFilePath(downloadFilepath);

			HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
			chromePrefs.put("profile.default_content_settings.popups", 0);
			chromePrefs.put("download.default_directory", file.getAbsolutePath());
			options.setExperimentalOption("prefs", chromePrefs);
		}

		switch (TestRunConfig.BROWSER.toLowerCase()) {
			case "ie":
				WebDriverManager.iedriver().setup();
				caps = new DesiredCapabilities();
				caps.setCapability("requireWindowFocus", false);
				driver = new InternetExplorerDriver();
				break;
			case "mobile":
				Map<String, String> mobileEmulation = new HashMap<>();
				mobileEmulation.put("deviceName", "iPhone X");
				options.setExperimentalOption("mobileEmulation", mobileEmulation);
			case "chrome":
				if (TestRunConfig.isLocal)
					WebDriverManager.chromedriver().proxy(TestRunConfig.PROXY_HOST+":"+TestRunConfig.PROXY_PORT).setup();
				else
					WebDriverManager.chromedriver().forceCache().setup();
				options.addArguments("--disable-web-security");
				options.addArguments("--allow-running-insecure-content");
				driver = new ChromeDriver(options);
				break;
			case "chromeheadless":
				if (TestRunConfig.isLocal)
					WebDriverManager.chromedriver().proxy(TestRunConfig.PROXY_HOST+":"+TestRunConfig.PROXY_PORT).setup();
				else
					WebDriverManager.chromedriver().setup();
				options.addArguments("--disable-web-security");
				options.addArguments("--allow-running-insecure-content");
				options.addArguments("--disable-web-security");
				options.addArguments("--allow-running-insecure-content");
				options.addArguments("--headless");
				options.addArguments("-disable-gpu");
				driver = new ChromeDriver(options);
				break;
			case "grid":
				WebDriverManager.chromedriver().version("79.0.3945.36").setup();
				options.addArguments("--disable-dev-shm-usage");
				options.addArguments("--no-sandbox");
				options.addArguments("--disable-gpu");
				options.addArguments("--incognito");
				options.addArguments("--headless");
				options.addArguments("--window-size=2560,1600");
				options.addArguments("--ignore-certificate-errors");
				options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
				DesiredCapabilities capabilities = new DesiredCapabilities();
  				capabilities.setBrowserName("chrome");
//  				capabilities.setPlatform(Platform.LINUX);
				capabilities.setJavascriptEnabled(true);
//				capabilities.setCapability(ChromeOptions.CAPABILITY, options);
//				capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true);

//				Proxy proxy = new Proxy();
//				proxy.setHttpProxy("\"http://zsproxy.hiscox.com/hiscox.pac\"");
//				capabilities.setCapability(CapabilityType.ForSeleniumServer.PROXY_PAC, proxy);
				try {
					driver = new RemoteWebDriver(new URL("http://selenium-prod-tools-northeurope.azure.hiscox.com/wd/hub"), capabilities);
					((RemoteWebDriver) driver).setLogLevel(Level.INFO);
					((RemoteWebDriver) driver).resetInputState();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				break;
			case "firefoxHeadless":
				firefoxOptions = new FirefoxOptions();
				firefoxOptions.addArguments("--headless");
			case "firefox":
				WebDriverManager.firefoxdriver().setup();
				System.setProperty("webdriver.gecko.driver", "src/test/drivers/geckodriver.exe");
				System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
				System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
				caps = new DesiredCapabilities();
				caps.setJavascriptEnabled(true);
				caps.setCapability(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);
				driver = new FirefoxDriver(caps);
				break;
		}
		edriver = new EventFiringWebDriver(driver);
		eventListener = new EventListener();
		edriver.register(eventListener);
		edriver.manage().timeouts().implicitlyWait(TestRunConfig.IMPLICIT_TIME_OUT, TimeUnit.SECONDS);
		edriver.manage().timeouts().pageLoadTimeout(TestRunConfig.IMPLICIT_TIME_OUT, TimeUnit.SECONDS);
		edriver.manage().window().maximize();
	}

	public void quitBrowser () {
		edriver.quit();
		log.info("Browser is closed");
	}

	public void navigateTo (String url) {
		edriver.get(url);
	}

	public String getCurrentUrl () {
		return edriver.getCurrentUrl();
	}
}
