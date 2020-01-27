package de.tum.bgu.msm.scenarios.av;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix1D;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class ParkingDataManager implements ModelUpdateListener {

    private final Logger logger = Logger.getLogger(ParkingDataManager.class);
    private final DataContainer dataContainer;
    private final Random random;

    Multiset<DwellingType> parkingSpacesByDwellingType = HashMultiset.create();
    Multiset<DwellingType> dwellinggsByType = HashMultiset.create();

    public ParkingDataManager(DataContainer dataContainer, Random random) {
        this.dataContainer = dataContainer;
        this.random = random;
    }

    @Override
    public void setup() {
        //assign initial data to locations from file (todo currently initialized to zero)
        for (Zone zone : dataContainer.getGeoData().getZones().values()) {
            LocationParkingData zonalParkingData = new LocationParkingData(0);
            zone.getAttributes().put("PARKING", zonalParkingData);
        }

        //find dwelling without number of parking spaces and fill it as a function of dewlling type (hard coded here)
        int counter  = 0;
        for (Dwelling dwelling : dataContainer.getRealEstateDataManager().getDwellings()) {
            if (dwelling.getAttributes().get("PARKING_SPACES") == null) {
                int spaces = getNumberOfParkingSpaces((DefaultDwellingTypeImpl)dwelling.getType());
                dwellinggsByType.add(dwelling.getType());
                parkingSpacesByDwellingType.add(dwelling.getType(), spaces);
                dwelling.getAttributes().put("PARKING_SPACES",spaces);
                counter++;
            }
        }

        logger.info("Updated the parking spaces of " + counter + " in the base year ");



    }

    @Override
    public void prepareYear(int year) {


        //generate a synthetic parking quality by zone based on accessibility
        for (Zone zone : dataContainer.getGeoData().getZones().values()){
            double accessibility = dataContainer.getAccessibility().getAutoAccessibilityForZone(zone);
            int parkingQuality;
            if (accessibility > 46){
                parkingQuality = 0;
            } else if (accessibility > 20) {
                parkingQuality = 1;
            } else if (accessibility > 7) {
                parkingQuality = 2;
            } else {
                parkingQuality = 3;
            }
            LocationParkingData zonalParkingData = new LocationParkingData(parkingQuality);
            zone.getAttributes().put("PARKING", zonalParkingData);
        }



        //collect jobs by zone
        final Map<Integer, List<Dwelling>> dwellingsByZone = dataContainer.getRealEstateDataManager().getDwellings().stream().collect(Collectors.groupingBy(Location::getZoneId));
        IndexedDoubleMatrix1D dwellingsByZoneMap = new IndexedDoubleMatrix1D(dataContainer.getGeoData().getZones().values());
        for(int zoneId : dataContainer.getGeoData().getZones().keySet()){
            if (dwellingsByZone.keySet().contains(zoneId)){
                dwellingsByZoneMap.setIndexed(zoneId, dwellingsByZone.get(zoneId).size());
            } else {
                dwellingsByZoneMap.setIndexed(zoneId, 0.);
            }

        }

        //update the assignment to regions (weighted by dwellings)
        for (Region region : dataContainer.getGeoData().getRegions().values()) {
            int parkingQuality = 0;
            int sumOfWeights = 0;
            for (Zone zone : region.getZones()) {
                double ddThisZone = dwellingsByZoneMap.getIndexed(zone.getZoneId());
                parkingQuality += ((LocationParkingData) zone.getAttributes().get("PARKING")).getParkingQuality() * ddThisZone;
                sumOfWeights+= ddThisZone;
            }
            if (sumOfWeights > 0){
                region.getAttributes().put("PARKING", new LocationParkingData(parkingQuality/sumOfWeights));
            } else {
                region.getAttributes().put("PARKING", new LocationParkingData(0));
            }

        }


    }

    @Override
    public void endYear(int year) {

        for (DefaultDwellingTypeImpl type : DefaultDwellingTypeImpl.values()){
            double parkings = parkingSpacesByDwellingType.count(type)/Math.max(dwellinggsByType.count(type),1.);
            logger.info("Parking spaces at dwelling type " + type.toString() + " is " + parkings);
        }


    }

    @Override
    public void endSimulation() {

    }


    public static int getNumberOfParkingSpaces(DefaultDwellingTypeImpl type) {
        float[] probabilities;
        switch (type) {
            case SFD:
                probabilities = new float[]{0f,0.5f,0.4f,0.1f};
                return SiloUtil.select(probabilities);
            case SFA:
                probabilities = new float[]{0.1f,0.55f,0.3f,0.05f};
                return SiloUtil.select(probabilities);
            case MF234:
                probabilities = new float[]{0.2f,0.6f,0.2f,0f};
                return SiloUtil.select(probabilities);
            case MF5plus:
                probabilities = new float[]{0.3f,0.6f,0.1f,0f};
                return SiloUtil.select(probabilities);
            default:
                return 0;
        }


    }


}