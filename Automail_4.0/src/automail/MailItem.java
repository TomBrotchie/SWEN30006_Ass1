package automail;

import java.util.Map;
import java.util.TreeMap;

/**
 * Modified Class: Team 03
 * Represents a mail item
 */
public class MailItem {
	
    /** Represents the destination floor to which the mail is intended to go */
    protected final int DESTINATION_FLOOR;
    /** The mail identifier */
    protected final String MAILITEM_ID;
    /** The time the mail item arrived */
    protected final int ARRIVAL_TIME;
    /** The weight in grams of the mail item */
    protected final int WEIGHT;

    /**
     * Constructor for a MailItem
     * @param dest_floor the destination floor intended for this mail item
     * @param arrival_time the time that the mail arrived
     * @param weight the weight of this mail item
     */
    public MailItem(int dest_floor, int arrival_time, int weight){
        this.DESTINATION_FLOOR = dest_floor;
        this.MAILITEM_ID = String.valueOf(hashCode());
        this.ARRIVAL_TIME = arrival_time;
        this.WEIGHT = weight;
    }

    @Override
    public String toString(){
        return String.format("Mail Item:: ID: %6s | Arrival: %4d | Destination: %2d | Weight: %4d",
                MAILITEM_ID, ARRIVAL_TIME, DESTINATION_FLOOR, WEIGHT);
    }

    /**
     *
     * @return the destination floor of the mail item
     */
    public int getDestFloor() {
        return DESTINATION_FLOOR;
    }
    
    /**
     *
     * @return the ID of the mail item
     */
    public String getId() {
        return MAILITEM_ID;
    }

    /**
     *
     * @return the arrival time of the mail item
     */
    public int getArrivalTime(){
        return ARRIVAL_TIME;
    }

    /**
    *
    * @return the weight of the mail item
    */
   public int getWeight(){
       return WEIGHT;
   }
   
	static private int count = 0;
	static private Map<Integer, Integer> hashMap = new TreeMap<Integer, Integer>();

	@Override
	public int hashCode() {
		Integer hash0 = super.hashCode();
		Integer hash = hashMap.get(hash0);
		if (hash == null) { hash = count++; hashMap.put(hash0, hash); }
		return hash;
	}

}
