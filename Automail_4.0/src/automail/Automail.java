package automail;

import simulation.IMailDelivery;

public class Automail {

    private Robot[] robots;
    private MailPool mailPool;

    private static int numRegRobots;
    private static int numFastRobots;
    private static int numBulkRobots;

    private static boolean fee_charging;
    
    public Automail(MailPool mailPool, IMailDelivery delivery, boolean fee_charging,
                    int numRegRobots, int numFastRobots, int numBulkRobots) {
    	/** Initialize the MailPool */
    	
    	this.mailPool = mailPool;
    	Automail.numRegRobots = numRegRobots;
    	Automail.numFastRobots = numFastRobots;
    	Automail.numBulkRobots = numBulkRobots;

    	Automail.fee_charging = fee_charging;
    	
    	/** Initialize robots of different types, their IDs are globally incrementing */
    	int i, j, k;
    	robots = new Robot[numRegRobots+numBulkRobots+numFastRobots];
    	for (i = 0; i < numRegRobots; i++) robots[i] = new RegularRobot(delivery, mailPool, i);
    	for (j = i; j < numRegRobots+numFastRobots; j++) robots[j] = new FastRobot(delivery, mailPool, j);
    	for (k = j; k < numRegRobots+numFastRobots+numBulkRobots; k++) robots[k] = new BulkRobot(delivery, mailPool, k);
    }

    public Robot[] getRobots() {
        return robots;
    }
    public MailPool getMailPool() { return mailPool; }

    public static int getNumRegRobots() { return numRegRobots; }
    public static int getNumFastRobots() {
        return numFastRobots;
    }
    public static int getNumBulkRobots() {
        return numBulkRobots;
    }

    public static boolean isFee_charging() { return fee_charging; }
}
