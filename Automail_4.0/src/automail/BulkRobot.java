package automail;

import exceptions.ItemTooHeavyException;
import simulation.IMailDelivery;

import java.util.LinkedList;

public class BulkRobot extends Robot{

    private static final int MAX_LOADING_ITEMS = 5;
    private static int total_operating_time;
    private static final int MOVING_SPEED = 1;

    /**
     * Initiates a bulk robot type. Use superclass constructor.
     * Set its robot ID globally, and set hasHand as false
     *
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     * @param number an unique ID among all robot
     */
    public BulkRobot(IMailDelivery delivery, MailPool mailPool, int number) {
        super(delivery, mailPool);
        setId("B" + number);
        setHasHand(false);
    }

    /** @see #moveTowards(int) */
    @Override
    protected void moveTowards(int destination) {
        if(getCurrent_floor() < destination){
            goUpFloor(MOVING_SPEED);
        } else {
            goDownFloor(MOVING_SPEED);
        }
    }

    /** @see #incrementOperatingTime()  */
    @Override
    protected void incrementOperatingTime() { total_operating_time++; }

    /** @see #getMaxLoadingCapacity() */
    @Override
    public int getMaxLoadingCapacity() { return BulkRobot.MAX_LOADING_ITEMS; }

    /** @see #getAverageTime() */
    @Override
    public double getAverageTime() { return (double)total_operating_time / Automail.getNumBulkRobots(); }

    /** @see #getBaseRate() */
    @Override
    public double getBaseRate() { return RobotBaseRate.BULK.getBaseRate(); }


    /** @see #addToRobot(LinkedList) */
    @Override
    public void addToRobot(LinkedList<MailItem> pool) throws ItemTooHeavyException {
        assert(getTube().isEmpty());
        for (int i = 0; i < MAX_LOADING_ITEMS; i++) {
            if (!pool.isEmpty()) {
                MailItem mail = pool.get(0);
                if (mail.WEIGHT > getIndividualMaxWeight()) throw new ItemTooHeavyException();
                getTube().add(0, mail);
                pool.remove();
            } else break;
        }
        setDeliveryItem(getTube().get(0));
    }
}
