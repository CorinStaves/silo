package de.tum.bgu.msm.io.input;

import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DefaultDwellingReader implements DwellingReader {

    private final static Logger logger = Logger.getLogger(DefaultDwellingReader.class);
    private final RealEstateDataManager realEstateDataManager;

    public DefaultDwellingReader(RealEstateDataManager realEstate) {
        this.realEstateDataManager = realEstate;
    }

    @Override
    public void readData(String path) {
        logger.info("Reading dwelling micro data from ascii file");
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posZone = SiloUtil.findPositionInArray("zone", header);
            int posHh = SiloUtil.findPositionInArray("hhID", header);
            int posType = SiloUtil.findPositionInArray("type", header);
            int posRooms = SiloUtil.findPositionInArray("bedrooms", header);
            int posQuality = SiloUtil.findPositionInArray("quality", header);
            int posCosts = SiloUtil.findPositionInArray("monthlyCost", header);
            int posYear = SiloUtil.findPositionInArray("yearBuilt", header);

            int posCoordX = -1;
            int posCoordY = -1;
            try {
                posCoordX = SiloUtil.findPositionInArray("coordX", header);
                posCoordY = SiloUtil.findPositionInArray("coordY", header);
            } catch (Exception e) {
                logger.warn("No coords given in dwelling input file. Models using microlocations will not work.");
            }

            int noCoordCounter = 0;
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                int zoneId = Integer.parseInt(lineElements[posZone]);
                int hhId = Integer.parseInt(lineElements[posHh]);
                String tp = lineElements[posType].replace("\"", "");
                DwellingType type = DefaultDwellingTypeImpl.valueOf(tp);
                int price = Integer.parseInt(lineElements[posCosts]);
                int area = Integer.parseInt(lineElements[posRooms]);
                int quality = Integer.parseInt(lineElements[posQuality]);
                int yearBuilt = Integer.parseInt(lineElements[posYear]);

                Coordinate coordinate = null;
                if (posCoordX >= 0 && posCoordY >= 0) {
                    try {
                        coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                    } catch (Exception e) {
                        noCoordCounter++;
                    }
                }

                Dwelling dwelling = realEstateDataManager.getDwellingFactory().createDwelling(id, zoneId, coordinate, hhId, type, area, quality, price, yearBuilt);
                realEstateDataManager.addDwelling(dwelling);
                if (id == SiloUtil.trackDd) {
                    SiloUtil.getTrackWriter().println("Read dwelling with following attributes from " + path);
                    SiloUtil.getTrackWriter().println(dwelling.toString());
                }
            }
            if(noCoordCounter > 0) {
                logger.warn("There were " + noCoordCounter + " dwellings without coordinates.");
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop dwelling file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " dwellings.");
    }
}
