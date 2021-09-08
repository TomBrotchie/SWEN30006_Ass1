package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import simulation.Clock;
import simulation.IMailDelivery;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The robot delivers mail!
 */
public abstract class Robot {

    private static final int INDIVIDUAL_MAX_WEIGHT = 2000;

    /** Possible states the robot can be in */
    public enum RobotState { DELIVERING, WAITING, RETURNING }
    private RobotState current_state;

    private final IMailDelivery delivery;

    private String id;
    private int current_floor;
    private int destination_floor;
    private final MailPool mailPool;
    private boolean receivedDispatch;

    private MailItem deliveryItem = null;
    private ArrayList<MailItem> tube = new ArrayList<>();
    private boolean hasHand;

    private int deliveryCounter;
    

    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     */
    public Robot(IMailDelivery delivery, MailPool mailPool, int number){

        // current_state = RobotState.WAITING;
    	current_state = RobotState.RETURNING;
        current_floor = Building.getInstance().getMailroomLocationFloor();
        this.delivery = delivery;
        this.mailPool = mailPool;
        this.receivedDispatch = false;
        this.deliveryCounter = 0;
    }


    /**
     * This is called on every time step
     * @throws ExcessiveDeliveryException if robot delivers more than the capacity of the tube without refilling
     */
    public void operate() throws ExcessiveDeliveryException {
        switch(current_state) {

    		/** This state is triggered when the robot is returning to the mailroom after a delivery */
    		case RETURNING:
    			/** If its current position is at the mailroom, then the robot should change state */
                if(current_floor == Building.getInstance().getMailroomLocationFloor()){
        			/** Tell the sorter the robot is ready */
        			mailPool.registerWaiting(this);
                	changeState(RobotState.WAITING);
                } else {
                	/** If the robot is not at the mailroom floor yet, then move towards it! */
                    moveTowards(Building.getInstance().getMailroomLocationFloor());
                	break;
                }

    		case WAITING:
                /** If the StorageTube is ready and the Robot is waiting in the mailroom then start the delivery */
                if(!isEmpty() && receivedDispatch){
                	receivedDispatch = false;
                	deliveryCounter = 0; // reset delivery counter
                	setDestination();
                	changeState(RobotState.DELIVERING);
                }
                break;

    		case DELIVERING:
                if(current_floor == destination_floor){ // If already here drop off either way
                    /** add the new feature charge fee to customer here: */
                    String additionalLog = "";
                    if (Automail.isFee_charging())  additionalLog = chargeFee(destination_floor);
                    delivery.deliver(this, deliveryItem, additionalLog);

                    /* remove the previous delivered item if the robot is a bulk robot */
                    tube.remove(deliveryItem);
                    deliveryItem = null;
                    deliveryCounter++;

                    /** Delivery complete, report this to the simulator! */
                    // Implies a simulation bug
                    if(deliveryCounter > getMaxLoadingItems()) throw new ExcessiveDeliveryException();

                    /** Check if want to return, i.e. if there is no item in the tube */
                    if(tube.isEmpty()){
                    	changeState(RobotState.RETURNING);
                    }
                    else{
                        /** If there is another item, set the robot's route to the location to deliver the item */
                        setDestination();
                        organiseNextDelivery();
                        changeState(RobotState.DELIVERING);
                    }
    			} else {
	        		/** The robot is not at the destination yet, move towards it! */
	                moveTowards(destination_floor);
    			}
                break;
    	}
        /* increment operating time for different type of robot */
        if (current_state == RobotState.RETURNING || current_state == RobotState.DELIVERING) {
            incrementOperatingTime();
        }
    }

    /**
     * This is called when a robot is assigned the mail items and ready to dispatch for the delivery
     */
    public void dispatch() { receivedDispatch = true; }

    public boolean isEmpty() { return (deliveryItem == null && tube.isEmpty()); }

    public String getIdTube() {  return String.format("%s(%1d)", this.id, (tube.isEmpty() ? 0 : tube.size())); }



    /**************************** private helper functions for the operation method above ****************************/
    /**
     * Sets the route for the robot
     */
    private void setDestination() {
        /** Set the destination floor */
        destination_floor = (deliveryItem == null) ? tube.get(0).getDestFloor() : deliveryItem.getDestFloor();
    }
    /**
     * Prints out the change in state
     * @param nextState the state to which the robot is transitioning
     */
    private void changeState(RobotState nextState){
        assert(!(deliveryItem == null && tube != null));
        if (current_state != nextState) {
            System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), current_state, nextState);
        }
        current_state = nextState;
        if(nextState == RobotState.DELIVERING){
            System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), deliveryItem.toString());
        }
    }

    private void organiseNextDelivery() {
        deliveryItem = tube.get(0);
        /* if it's a regular robot, remove the item from tube, if it's bulk, then keep it */
        if (hasHand) tube.remove(0);
    }


    /******************************TODO: To override this function in the subclass lateron****************************/


    /**
     * Generic function that moves the robot towards the destination
     * @param destination the floor towards which the robot is moving
     */
    public abstract void moveTowards(int destination);

    public abstract int getMaxLoadingItems();

    protected abstract void incrementOperatingTime();

    protected abstract String chargeFee(int nFloor);

    public abstract void addToRobot(LinkedList<MailItem> pool) throws ItemTooHeavyException;


    /********************************************* getters and setters  ***************************************/

    public void setId(String id) { this.id = id; }

    public void setHasHand(boolean hasHand) { this.hasHand = hasHand; }

    public int getCurrent_floor() { return current_floor; }
    public void setCurrent_floor(int current_floor) { this.current_floor = current_floor; }

    public void goUpFloor(int num) { current_floor += num; }
    public void goDownFloor(int num) { current_floor -= num; }

    public MailItem getDeliveryItem() { return deliveryItem; }
    public void setDeliveryItem(MailItem deliveryItem) { this.deliveryItem = deliveryItem; }

    public static int getIndividualMaxWeight() { return INDIVIDUAL_MAX_WEIGHT; }

    public ArrayList<MailItem> getTube() { return tube; }

}
