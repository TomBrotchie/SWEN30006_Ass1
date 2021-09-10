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
     * also set it state to be returning.
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     */
    public Robot(IMailDelivery delivery, MailPool mailPool){

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

        /** increment operating time when robot in returning or delivering state */
        if (current_state == RobotState.RETURNING || current_state == RobotState.DELIVERING) {
            incrementOperatingTime();
        }

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
    		    /** If already arrived at destination, drop off either way */
                if(current_floor == destination_floor){
                    /** if it is a bulk robot, remove the current item from tube, ready for delivering  */
                    if (!hasHand) tube.remove(deliveryItem);

                    /** New feature: robot charge fee to customer while delivering */
                    String additionalLog = "";
                    if (Automail.isFee_charging())  additionalLog = chargeFee(destination_floor);
                    delivery.deliver(this, deliveryItem, additionalLog);

                    deliveryItem = null;
                    deliveryCounter++;

                    /** Delivery complete, report this to the simulator! */
                    // Implies a simulation bug
                    if(deliveryCounter > getMaxLoadingCapacity()) throw new ExcessiveDeliveryException();

                    /** Check if want to return, i.e. if there is no item in the tube and hand */
                    if(tube.isEmpty()){
                    	changeState(RobotState.RETURNING);
                    }
                    else{
                        /** If there is another item, set the robot's route to the location to deliver the item */
                        organiseNextDelivery();
                        changeState(RobotState.DELIVERING);
                    }
    			} else {
	        		/** The robot is not at the destination yet, move towards it! */
	                moveTowards(destination_floor);
    			}
                break;
    	}
    }

    /********************************* Other useful Public method for Robot class ************************************/

    /**
     * This is called when a robot is assigned the mail items and ready to dispatch for the delivery
     */
    public void dispatch() { receivedDispatch = true; }

    /**
     * This method returns true if the robot is not carrying any items in hand or tube
     */
    public boolean isEmpty() { return (deliveryItem == null && tube.isEmpty()); }

    /**
     * Get robot Id and number of current items in tube for printing data log
     */
    public String getIdTube() {  return String.format("%s(%1d)", this.id, (tube.isEmpty() ? 0 : tube.size())); }



    /**************************** Private helper functions for operation() method above ****************************/

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

    /**
     * Calculate the fee of this delivery to be charged to customer
     * @param nFloor the destination floor robot is going to, correspond to different service fee
     * @return total cost of this delivery trip
     */
    private String chargeFee(int nFloor) {
        double serviceFee = BMS.getInstance().lookupServiceFee(nFloor);
        double averageTime = getAverageTime();
        double maintenanceCost = getBaseRate() * averageTime;
        double totalCost = serviceFee + maintenanceCost;

        return String.format(" | Service Fee: %.2f | Maintenance: %.2f | Avg. Operating Time: %.2f | Total Charge: %.2f",
                serviceFee, maintenanceCost, averageTime, totalCost);
    }

    /**
     * Start next delivery without returning, get an items from tube and go to destination floor
     */
    private void organiseNextDelivery() {
        deliveryItem = tube.get(0);
        /* if it's a regular robot, remove the item from tube, if it's bulk, then keep it in tube */
        if (hasHand) tube.remove(0);
        setDestination();
    }

    /**
     * Sets the route for the robot
     */
    private void setDestination() {
        /** Set the destination floor */
        destination_floor = (deliveryItem == null) ? tube.get(0).getDestFloor() : deliveryItem.getDestFloor();
    }


    /******************************** Abstract method to be override by the subclass ********************************/

    /**
     * Generic function that moves the robot towards the destination
     * @param destination the floor towards which the robot is moving
     */
    protected abstract void moveTowards(int destination);

    /**
     * Increment operating time of the same robot type by 1
     */
    protected abstract void incrementOperatingTime();

    /**
     * Get a specific robot types' maximum items carry capacity
     * @return max item amounts an robot can carry
     */
    public abstract int getMaxLoadingCapacity();

    /**
     * Calculate the average operating time among all the same type robot
     * @return average operating time of certain type robot
     */
    public abstract double getAverageTime();

    /**
     * Get the corresponding base rate for that type of robot
     * @return Fixed base rate of a certain robot type
     */
    public abstract double getBaseRate();

    /**
     * Add mails to robot for its max loading capacity on each tick of time
     * @param pool all mailItems in the current mail pool
     * @throws ItemTooHeavyException
     */
    public abstract void addToRobot(LinkedList<MailItem> pool) throws ItemTooHeavyException;


    /********************************************* Getters and Setters **********************************************/

    public void setId(String id) { this.id = id; }

    public void setHasHand(boolean hasHand) { this.hasHand = hasHand; }

    public int getCurrent_floor() { return current_floor; }
    public void setCurrent_floor(int current_floor) { this.current_floor = current_floor; }

    /** Shift the current floor by the value of input argument */
    public void goUpFloor(int num) { current_floor += num; }
    public void goDownFloor(int num) { current_floor -= num; }

    public MailItem getDeliveryItem() { return deliveryItem; }
    public void setDeliveryItem(MailItem deliveryItem) { this.deliveryItem = deliveryItem; }

    public ArrayList<MailItem> getTube() { return tube; }

    public static int getIndividualMaxWeight() { return INDIVIDUAL_MAX_WEIGHT; }
}
