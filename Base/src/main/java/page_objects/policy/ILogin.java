package page_objects.policy;

import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import model.policy.User;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public interface ILogin extends IPage {
    void logsIntoOneShieldAsUser(User user);
    void logOutAfterCreatingSubmission();
    void exitToHomePage();
    void setUserRole(String role);




}
