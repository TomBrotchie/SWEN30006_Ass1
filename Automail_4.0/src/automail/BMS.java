package automail;

import com.unimelb.swen30006.wifimodem.WifiModem;

import java.util.HashMap;

public class BMS {

    private static BMS BMS_server;

    private final HashMap<Integer, Double> serviceFeeMap = new HashMap<>();
    private final WifiModem wifiModem = WifiModem.getInstance(Building.getInstance().getMailroomLocationFloor());

    private BMS() throws Exception {}

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
