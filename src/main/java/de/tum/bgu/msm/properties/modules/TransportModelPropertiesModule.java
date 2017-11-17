package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public class TransportModelPropertiesModule {

    public final int[] modelYears;
    public final int[] skimYears;

    public final boolean runTravelDemandModel;
    public final String demandModelPropertiesPath;

    public final boolean runMatsim;
    public final String matsimZoneShapeFile;
    public final String matsimZoneCRS;

    public TransportModelPropertiesModule(ResourceBundle bundle) {
        modelYears = ResourceUtil.getIntegerArray(bundle, "transport.model.years");
        skimYears = ResourceUtil.getIntegerArray(bundle, "skim.years");
        runTravelDemandModel = ResourceUtil.getBooleanProperty(bundle, "mito.run.travel.model", false);
        demandModelPropertiesPath = ResourceUtil.getProperty(bundle, "mito.properties.file");
        runMatsim = ResourceUtil.getBooleanProperty(bundle, "matsim.run.travel.model", false);
        matsimZoneShapeFile = ResourceUtil.getProperty(bundle, "matsim.zones.shapefile");
        matsimZoneCRS = ResourceUtil.getProperty(bundle, "matsim.zones.crs");
    }
}
