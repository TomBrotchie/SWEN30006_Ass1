package automail;

import exceptions.ItemTooHeavyException;
import simulation.IMailDelivery;

import java.util.LinkedList;

/**
 * New Class: Team 03
 * Regular robot, carry one in hand, one in tube
 */
public class RegularRobot extends Robot{

    private static final int MAX_LOADING_ITEMS = 2;
    private static int total_operating_time;
    private static final int MOVING_SPEED = 1;


    /**
     * Initiates a Regular robot type. Use superclass constructor.
     * Set its robot ID globally, and set hasHand as true
     *
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     * @param number an unique ID among all robot
     */
    public RegularRobot(IMailDelivery delivery, MailPool mailPool, int number) {
        super(delivery, mailPool);
        setId("R" + number);
        setHasHand(true);
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
    public int getMaxLoadingCapacity() { return RegularRobot.MAX_LOADING_ITEMS; }

    /** @see #getAverageTime() */
    @Override
    public double getAverageTime() { return (double)total_operating_time / Automail.getNumRegRobots(); }

    /** @see #getBaseRate() */
    @Override
    public double getBaseRate() { return RobotBaseRate.REGULAR.getBaseRate(); }


    /** @see #addToRobot(LinkedList) */
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
        if (mailItem.WEIGHT > getIndividualMaxWeight()) throw new ItemTooHeavyException();
        setDeliveryItem(mailItem);
    }

    private void addToTube(MailItem mailItem) throws ItemTooHeavyException {
        assert(getTube().isEmpty());
        if (mailItem.WEIGHT > getIndividualMaxWeight()) throw new ItemTooHeavyException();
        getTube().add(mailItem);
    }
}
