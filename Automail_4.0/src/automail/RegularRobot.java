package automail;

import exceptions.ItemTooHeavyException;
import simulation.IMailDelivery;

import java.util.LinkedList;
import java.util.ListIterator;

public class RegularRobot extends Robot{

    private static final int MAX_LOADING_ITEMS = 2;
    private static int total_operating_time;

    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     *
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     * @param number
     */
    public RegularRobot(IMailDelivery delivery, MailPool mailPool, int number) {
        super(delivery, mailPool, number);
        setId("R" + number);
        setHasHand(true);
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
    public int getMaxLoadingItems() { return RegularRobot.MAX_LOADING_ITEMS; }

    @Override
    protected void incrementOperatingTime() { total_operating_time++; }

    @Override
    protected String chargeFee(int nFloor) {
        double serviceFee = BMS.getInstance().lookupServiceFee(nFloor);
        double averageTime = (double)total_operating_time / Automail.getNumRegRobots();
        double maintenanceCost = RobotBaseRate.REGULAR.getBaseRate() * averageTime;
        double totalCost = serviceFee + maintenanceCost;

        return String.format(" | Service Fee: %.2f | Maintenance: %.2f | Avg. Operating Time: %.2f | Total Charge: %.2f",
                serviceFee, maintenanceCost, averageTime, totalCost);
    }

    @Override
    public void addToRobot(LinkedList<MailItem> pool) throws ItemTooHeavyException {
        if (!pool.isEmpty()) {
            addToHand(pool.get(0));
            pool.remove();
        }

        if (!pool.isEmpty()) {
            addToTube(pool.get(0));
            pool.remove();
        }
    }

    private void addToHand(MailItem mailItem) throws ItemTooHeavyException {
        assert(getDeliveryItem() == null);
        if (mailItem.weight > getIndividualMaxWeight()) throw new ItemTooHeavyException();
        setDeliveryItem(mailItem);
    }

    private void addToTube(MailItem mailItem) throws ItemTooHeavyException {
        assert(getTube().isEmpty());
        if (mailItem.weight > getIndividualMaxWeight()) throw new ItemTooHeavyException();
        getTube().add(mailItem);
    }
}
