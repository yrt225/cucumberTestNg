package utils.excel_operations;

import com.poiji.config.Casting;
import com.poiji.option.PoijiOptions;
import utils.RandomGenerator;
import utils.TestRunConfig;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MyCasting implements Casting {
    @Override
    public Object castValue(Class<?> fieldType, String value, PoijiOptions options) {
        switch (value.replaceAll("[ ]","").trim().toLowerCase()){
            case "userfirstname":
            case "firstname":
                value= RandomGenerator.firstName()+ LocalDateTime.now(ZoneId.of("America/New_York"));;
                break;
            case "userlastname":
            case "lastname":
                value= RandomGenerator.lastName();
              break;
            case "companyname":
                value= (RandomGenerator.businessName()+ LocalDateTime.now(ZoneId.of("America/New_York"))).replaceAll("[^a-zA-Z0-9]", "");
                break;
            case "ssn":
            case "fein":
                value= RandomGenerator.numeric(9);
                break;
            case "phonenumber":
                value= RandomGenerator.numeric(3)+RandomGenerator.numeric(3)+RandomGenerator.numeric(4);
                break;
            case "email":
                value= TestRunConfig.TESTER_NAME+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddyy_HH_mm_ss"))+"@hiscox.nonprod";
                break;
        }
        return value.trim();
    }
}