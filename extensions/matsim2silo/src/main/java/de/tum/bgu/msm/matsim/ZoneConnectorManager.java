package de.tum.bgu.msm.matsim;

import com.google.common.collect.Lists;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Coord;

import java.util.*;
import java.util.stream.Collectors;

public class ZoneConnectorManager {

    private final static Logger logger = Logger.getLogger(ZoneConnectorManager.class);

    public enum ZoneConnectorMethod {RANDOM, WEIGHTED_BY_POPULATION}

    private final Map<Zone, List<Coord>> coordsByZone;

    private ZoneConnectorManager(Map<Zone, List<Coord>> coordsByZone) {
        this.coordsByZone = coordsByZone;
    }

    public static ZoneConnectorManager createRandomZoneConnectors(
            Collection<Zone> zones, int numberOfCalcPoints) {

        final Map<Zone, List<Coord>> coordsByZone = new LinkedHashMap<>();

        for (Zone zone : zones) {
            // Several points in a given origin zone
            for (int i = 0; i < numberOfCalcPoints; i++) {
                Coordinate coordinate = zone.getRandomCoordinate(SiloUtil.getRandomObject());
                Coord originCoord = new Coord(coordinate.x, coordinate.y);
                if (!coordsByZone.containsKey(zone)) {
                    coordsByZone.put(zone, new LinkedList<>());
                }
                coordsByZone.get(zone).add(originCoord);
            }
        }
        logger.warn("There are " + coordsByZone.keySet().size() + " origin zones.");
        return new ZoneConnectorManager(coordsByZone);
    }

    public static ZoneConnectorManager createWeightedZoneConnectors(
            Collection<Zone> zones, RealEstateDataManager realEstateData,
            HouseholdDataManager householdData) {

        final Map<Zone, List<Coord>> coordsByZone = new LinkedHashMap<>();


        Map<Integer, List<Dwelling>> dwellingsByZone = realEstateData.getDwellings().parallelStream().collect(Collectors.groupingBy(Dwelling::getZoneId));

        for (Zone zone : zones) {
            List<Dwelling> dwellings = dwellingsByZone.get(zone.getId());
            if(dwellings == null) {
                logger.warn("Zone [" + zone + "] has no dwellings. Will use geometric centroid as weighted centroid.");
                final Point centroid = ((Geometry) (zone.getZoneFeature().getDefaultGeometry())).getCentroid();
                Coord coord = new Coord(centroid.getX(), centroid.getY());
                coordsByZone.put(zone, Lists.newArrayList(coord));
                continue;
            }
            double xSum = 0;
            double ySum = 0;
            int weightedCount = 0;

            for (Dwelling dwelling : dwellings) {
                final Coordinate coordinate = dwelling.getCoordinate();
                final Household household = householdData.getHouseholdFromId(dwelling.getResidentId());
                int weight = 1;
                if(household!=null) {
                    weight += household.getHhSize();
                }
                xSum += (weight * coordinate.x);
                ySum += (weight * coordinate.y);
                weightedCount += weight;
            }
            double avgX = xSum / weightedCount;
            double avgY = ySum / weightedCount;

            Coord coord = new Coord(avgX, avgY);
            coordsByZone.put(zone, Lists.newArrayList(coord));
        }

        logger.warn("There are " + coordsByZone.keySet().size() + " origin zones.");
        return new ZoneConnectorManager(coordsByZone);
    }

    public List<Coord> getCoordsForZone(Zone zone) {
        return coordsByZone.get(zone);
    }
}
