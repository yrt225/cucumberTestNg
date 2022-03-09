package utils.db_operations;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import org.jongo.Jongo;
import org.springframework.stereotype.Component;
import utils.TestRunConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@ScenarioScope
public class DB_Connection {

    private static DB mongoDatabase = null;
    private static Jongo jongo = null;

    public static Jongo getJongoConnection() {
        mongoDatabase = new MongoClient(TestRunConfig.MONGO_HOST_NAME, TestRunConfig.MONGO_PORT).getDB(TestRunConfig.MONGO_DBNAME);
        jongo = new Jongo(mongoDatabase);
        return jongo;
    }

    /**
     * TODO : May be not required
     *
     * @return
     */
    public static Connection getSqlConnection() {
        Connection connection = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(TestRunConfig.get("dBUtils.connection") + ";" + "<unknown>");
            return connection;
        } catch (com.microsoft.sqlserver.jdbc.SQLServerException | ClassNotFoundException exp) {
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return connection;
    }
}