package de.tum.bgu.msm.run;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

/**
 * Implements SILO for for the Maryland Statewide Transportation Model
 *
 * @author Rolf Moeckel
 */
public class SiloMstm {

    private final static Logger logger = Logger.getLogger(SiloMstm.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Starting SILO land use model for the Munich Metropolitan Area");
        DataContainer dataContainer = DataBuilder.buildDataContainer(properties);
        DataBuilder.readInput(properties, dataContainer);

        ModelContainer modelContainer = ModelBuilder.getModelContainerForMstm(dataContainer, properties, config);
        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.runModel();
        logger.info("Finished SILO.");
    }
}