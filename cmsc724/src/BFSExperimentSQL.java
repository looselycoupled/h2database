
import experiments.Experiment;
import load.PubsLoader;
import java.sql.SQLException;

/**
 *
 */
public class BFSExperimentSQL extends Experiment
{

    /**
     *
     */
    @Override
    public void setup() {
        try {
            PubsLoader loader = new PubsLoader();
            loader.load();
        } catch (SQLException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    /**
     *
     */
    @Override
    public void conduct() {

    }

    /**
     *
     */
    public static void main(String[] args) {
        BFSExperimentSQL driver = new BFSExperimentSQL();
        driver.start();
    }

}
