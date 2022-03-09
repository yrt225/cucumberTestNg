package model.policy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poiji.annotation.ExcelCellName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {
    private int partnerNo = 0;

    @ExcelCellName("User Role Id")
    @JsonProperty("User Role Id")
    private String UserRoleId;
    @ExcelCellName("Environment")
    @JsonProperty("Environment")
    private String environment;
    @ExcelCellName("User Name")
    @JsonProperty("User Name")
    private String UserName;
    @ExcelCellName("User Role")
    @JsonProperty("User Role")
    private String UserRole;
    @ExcelCellName("User Id")
    @JsonProperty("User Id")
    private String UserId;
    @ExcelCellName("Password")
    @JsonProperty("Password")
    private String Password;
    @ExcelCellName("Home Page")
    @JsonProperty("Home Page")
    private String HomePage;
    @ExcelCellName("Preferences Available")
    @JsonProperty("Preferences Available")
    private String PreferencesAvailable;
}
