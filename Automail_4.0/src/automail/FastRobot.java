package automail;

import exceptions.ItemTooHeavyException;
import simulation.IMailDelivery;

import java.util.LinkedList;

public class FastRobot extends Robot{

    private static final int MAX_LOADING_ITEMS = 1;
    private static final int MOVING_SPEED = 3;
    private static int total_operating_time;

    /**
     * Initiates a fast robot type. Use superclass constructor.
     * Set its robot ID globally, and set hasHand as true
     *
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     * @param number an unique ID among all robot
     */
    public FastRobot(IMailDelivery delivery, MailPool mailPool, int number) {
        super(delivery, mailPool);
        setId("F" + number);
        setHasHand(true);
    }

    /** @see #moveTowards(int) */
    @Override
    protected void moveTowards(int destination) {
        int floorToGo = Math.abs(destination - getCurrent_floor());
        if(floorToGo <= MOVING_SPEED){
            setCurrent_floor(destination);
        } else {
            if (getCurrent_floor() < destination) {
                goUpFloor(MOVING_SPEED);
            } else {
                goDownFloor(MOVING_SPEED);
            }
        }
    }

    /** @see #incrementOperatingTime()  */
    @Override
    protected void incrementOperatingTime() { total_operating_time++; }

    /** @see #getMaxLoadingCapacity() */
    @Override
    public int getMaxLoadingCapacity() { return FastRobot.MAX_LOADING_ITEMS; }

    /** @see #getAverageTime() */
    @Override
    public double getAverageTime() { return (double)total_operating_time / Automail.getNumFastRobots(); }

    /** @see #getBaseRate() */
    @Override
    public double getBaseRate() { return RobotBaseRate.FAST.getBaseRate(); }


    /** @see #addToRobot(LinkedList) */
    @Override
    public void addToRobot(LinkedList<MailItem> pool) throws ItemTooHeavyException {
        assert(getDeliveryItem() == null);
        MailItem mail = pool.getFirst();
        if (mail.weight > getIndividualMaxWeight()) throw new ItemTooHeavyException();
        setDeliveryItem(mail);
        pool.remove(0);
    }
}
