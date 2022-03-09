package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestRunConfig{
    private static Logger log = LoggerFactory.getLogger(TestRunConfig.class);

    public static boolean isLocal = System.getProperty("os.name").toLowerCase().contains("windows");;
    public static String TESTER_NAME = System.getProperty("user.name");
    public static String USER_DIR = System.getProperty("user.dir");
    public static String ENV = get("env");
    public static String BASE_URL = getBaseUrl(ENV);
    public static String BROWSER = get("browser");
    public static String TAGS = get("tags");
    public static String PROJECT = get("project");
    public static String EMAIL = get("email");
    public static boolean SSO = Boolean.parseBoolean(get("sso"));
    public static String PASSWORD = get("password");
    public static String APP_USER_NAME = get("usr");
    public static String PWD = get("psw");
    public static final boolean IS_PARALLEL = Boolean.parseBoolean(get("isParallel"));
    public static final String THREAD_COUNT = get("threadCount");
    public static int IMPLICIT_TIME_OUT = Integer.parseInt(get("implicitTimeout"));
    public static int EXPLICIT_TIME_OUT = Integer.parseInt(get("explicitTimeout"));
    public static int FLUENT_TIME_OUT = Integer.parseInt(get("fluentTimeout"));
    public static String APPLIT_NPROD_UNAME = get("applit.nonprod.username");
    public static String APPLIT_NPROD_PWD = get("applit.nonprod.password");
    public static String APPLIT_PROD_UNAME = get("applit.prod.username");
    public static String APPLIT_PROD_PWD = get("applit.prod.password");
    public static String MONGO_HOST_NAME = get("mongodb.hostname");
    public static int MONGO_PORT = Integer.parseInt(get("mongodb.port"));
    public static String MONGO_DBNAME = get("mongodb.dbName");
    public static boolean MONGO_FLAG = Boolean.parseBoolean(get("useMongo"));
    public static boolean CREATE_A_NEW_COLLECTION = Boolean.parseBoolean(get("createNewCollection"));
    //public static String PROXY_HOST = getProxy("automation_proxy").split(":")[0];
    public static int PROXY_PORT = 80;

    public static String QTEST_RESULTS_PATH=get("qTestResultsPath");
    public static String QTEST_CYCLE_ID=get("qTestCycleId");
    public static String QTEST_DRY_RUN=get("dryRun");
    public static String QTEST_PROJECT_ID=get("qTestProjectId");
    public static String QTEST_PROJECT_NAME=get("projectName");
    public static boolean SKIP_QTEST_UPLOAD=Boolean.parseBoolean(get("skipQTestUpload"));
    public static String USER_NAME = System.getProperty("user.name");

    public TestRunConfig() {
    }

    public static String get(String property) {
        String value = null;

        if (System.getenv(property) != null) {
            value = System.getenv(property);
            log.info(String.format("from env:%s:%s", property, value));
        } else if (System.getenv(property.toUpperCase()) != null) {
            value = System.getenv(property);
            log.info(String.format("from env:%s:%s", property.toLowerCase(), value));
        } else if (System.getProperty(property) != null) {
            value = System.getProperty(property);
            log.info(String.format("from property:%s:%s", property, value));
        } else if (System.getenv(String.format("bamboo_%s",property)) != null) {
            value = System.getenv(String.format("bamboo_%s",property));
            log.info(String.format("from bamboo customized variables:%s:%s", property, value));
        } else {
            try {
                FileInputStream inputStream = new FileInputStream(new File("gradle.properties"));
                Properties properties = new Properties();
                properties.load(inputStream);
                value = properties.getProperty(property);
                if(value==null && System.getenv("GRADLE_USER_HOME") != null){
                    inputStream = new FileInputStream(new File(System.getenv("GRADLE_USER_HOME")+File.separator+"gradle.properties"));
                    properties = new Properties();
                    properties.load(inputStream);
                    value = properties.getProperty(property);
                }else if(value==null && property.contains("projectName")){
                    value="native";
                }else if(value==null && property.contains("qTestProjectId")){
                    value="90608";
                }
            }catch (IOException ex){
                log.error("gradle.properties file not found");
            }
        }
        return value;
    }

    private static String getBaseUrl(String key) {
        String value=null;
        try {
            FileInputStream inputStream = new FileInputStream(new File("environment.properties"));
            Properties properties = new Properties();
            properties.load(inputStream);
            value = properties.getProperty(key + ".base.url");
        }catch (IOException ex){
            log.error("environment.properties file not found");
        }
        return value;
    }

    private static String getProxy(String key) {
        return System.getenv(key)==null?"zsproxy1.hiscox.nonprod:80":System.getenv(key);
    }
}