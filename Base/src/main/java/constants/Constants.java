package constants;

import utils.TestRunConfig;

import java.util.Arrays;
import java.util.List;

public class Constants {
    /*
     * Mongo DB Collection constants
     */
    public static final String FF_TEST_DATA_COLLECTION = TestRunConfig.PROJECT + "_test_data";

    //THis is for Git operations
    public static class RuntimeEnvironment {
        public static final String GRID = "GRID";0
      
    }

    public static class Selenium_Grid_Config {
        public static final String SELENIUM_GRID_URL = "http://selenium-prod-tools-northeurope.azure.hiscox.com/wd/hub";
       
    }

    public static class DeviceType {
        public static final String DESKTOP = "DESKTOP";
     
    }

  
}