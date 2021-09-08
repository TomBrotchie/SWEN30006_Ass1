package automail;

import exceptions.ItemTooHeavyException;
import simulation.IMailDelivery;

import java.util.LinkedList;

public class FastRobot extends Robot{

    private static final int MAX_LOADING_ITEMS = 1;
    private static final int MOVINGSPEED = 3;
    private static int total_operating_time;

    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     *
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     * @param number
     */
    public FastRobot(IMailDelivery delivery, MailPool mailPool, int number) {
        super(delivery, mailPool, number);
        setId("F" + number);
        setHasHand(true);
    }

    @Override
    public void moveTowards(int destination) {
        int floorToGo = Math.abs(destination - getCurrent_floor());
        if(floorToGo <= MOVINGSPEED){
            setCurrent_floor(destination);
        } else {
            if (getCurrent_floor() < destination) {
                goUpFloor(MOVINGSPEED);
            } else {
                goDownFloor(MOVINGSPEED);
            }
        }
    }

    @Override
    public int getMaxLoadingItems() { return FastRobot.MAX_LOADING_ITEMS; }

    @Override
    protected void incrementOperatingTime() { total_operating_time++; }

    @Override
    protected String chargeFee(int nFloor) {
        double serviceFee = BMS.getInstance().lookupServiceFee(nFloor);
        double averageTime = (double)total_operating_time / Automail.getNumFastRobots();
        double maintenanceCost = RobotBaseRate.FAST.getBaseRate() * averageTime;
        double totalCost = serviceFee + maintenanceCost;

        return String.format(" | Service Fee: %.2f | Maintenance: %.2f | Avg. Operating Time: %.2f | Total Charge: %.2f",
                            serviceFee, maintenanceCost, averageTime, totalCost);
    }

    @Override
    public void addToRobot(LinkedList<MailItem> pool) throws ItemTooHeavyException {
        assert(getDeliveryItem() == null);
        MailItem mail = pool.getFirst();
        if (mail.weight > getIndividualMaxWeight()) throw new ItemTooHeavyException();
        setDeliveryItem(mail);
        pool.remove(0);
    }
}
