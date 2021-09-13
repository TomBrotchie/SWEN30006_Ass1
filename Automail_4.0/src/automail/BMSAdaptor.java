package automail;

/**
 * New Class: Team 03
 * Indirection BMS adaptor class to prevent future variation
 */
public abstract class BMSAdaptor {

    /**
     * This method is always called regardless of changing in external BMS.
     * A service fee is guarantee returned no matter retrieved successfully or not
     * @param onFloor destination floor of mail item
     * @return service fee
     */
    public static double getServiceFee(int onFloor) {
        return BMS.getInstance().lookupServiceFee(onFloor);
    }

    /**
     * Lookup the service fee from the external Building management system
     * @param onFloor destination floor of mail items
     * @return service fee from external BMS
     */
    public abstract double lookupServiceFee(int onFloor);

}
