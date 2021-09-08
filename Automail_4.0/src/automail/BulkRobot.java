package automail;

import exceptions.ItemTooHeavyException;
import simulation.IMailDelivery;

import java.util.LinkedList;
import java.util.ListIterator;


public class BulkRobot extends Robot{

    private static final int MAX_LOADING_ITEMS = 5;
    private static int total_operating_time;

    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     *
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     * @param number
     */
    public BulkRobot(IMailDelivery delivery, MailPool mailPool, int number) {
        super(delivery, mailPool, number);
        setId("B" + number);
        setHasHand(false);
    }

    @Override
    public void moveTowards(int destination) {
        if(getCurrent_floor() < destination){
            goUpFloor(1);
        } else {
            goDownFloor(1);
        }
    }

    @Override
    public int getMaxLoadingItems() { return BulkRobot.MAX_LOADING_ITEMS; }

    @Override
    protected void incrementOperatingTime() { total_operating_time++; }

    @Override
    protected String chargeFee(int nFloor) {
        double serviceFee = BMS.getInstance().lookupServiceFee(nFloor);
        double averageTime = (double)total_operating_time / Automail.getNumBulkRobots();
        double maintenanceCost = RobotBaseRate.BULK.getBaseRate() * averageTime;
        double totalCost = serviceFee + maintenanceCost;

        return String.format(" | Service Fee: %.2f | Maintenance: %.2f | Avg. Operating Time: %.2f | Total Charge: %.2f",
                serviceFee, maintenanceCost, averageTime, totalCost);
    }

    @Override
    public void addToRobot(LinkedList<MailItem> pool) throws ItemTooHeavyException {
        assert(getTube().isEmpty());
        for (int i = 0; i < MAX_LOADING_ITEMS; i++) {
            if (!pool.isEmpty()) {
                MailItem mail = pool.get(0);
                if (mail.weight > getIndividualMaxWeight()) throw new ItemTooHeavyException();
                getTube().add(0, mail);
                pool.remove();
            } else break;
        }
        setDeliveryItem(getTube().get(0));
    }
}
