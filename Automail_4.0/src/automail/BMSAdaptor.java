package automail;

public class BMSAdaptor {

    // private static final BMS BMS_server = BMS.getInstance();

    public static double getServiceFee(int onFloor) {
        return BMS.getInstance().lookupServiceFee(onFloor);
    }
}
