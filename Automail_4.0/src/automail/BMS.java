package automail;

import com.unimelb.swen30006.wifimodem.WifiModem;

import java.util.HashMap;

public class BMS implements BMSAdaptor{

    private static BMS BMS_server = null;

    private final HashMap<Integer, Double> serviceFeeMap = new HashMap<>();
    private final WifiModem wifiModem = WifiModem.getInstance(Building.getInstance().getMailroomLocationFloor());

    /**
     * Private constructor for singleton class
     * @throws Exception
     */
    private BMS() throws Exception {}

    /**
     * Get an instance of BMS class that share globally among all this program
     * Initialise using constructor if no instance exist yet
     * @return Only instance of BMS class
     */
    public static BMS getInstance() {
        if (BMS_server == null) {
            try {
                BMS_server = new BMS();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return BMS_server;
    }

    /**
     * Find the the corresponding service fee of a certain floor
     * return the most recent retrieval if wifi connection failed
     * @param nFloor The destination floor of delivery
     * @return The remotely looked up service fee of nFloor
     */
    @Override
    public double lookupServiceFee(int nFloor) {
        double price = wifiModem.forwardCallToAPI_LookupPrice(nFloor);

        if (price < 0) {
            if (serviceFeeMap.containsKey(nFloor)) {
                price = serviceFeeMap.get(nFloor);
            } else {
                price = 0;
            }
        }
        serviceFeeMap.put(nFloor, price);
        return price;
    }
}
