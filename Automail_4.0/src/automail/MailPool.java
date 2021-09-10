package automail;

import java.util.LinkedList;
import java.util.Comparator;
import java.util.ListIterator;

import exceptions.ItemTooHeavyException;

/**
 * addToPool is called when there are mail items newly arrived at the building to add to the MailPool or
 * if a robot returns with some undelivered items - these are added back to the MailPool.
 * The data structure and algorithms used in the MailPool is your choice.
 * 
 */
public class MailPool {

	private LinkedList<MailItem> pool;
	private LinkedList<Robot> robots;

	public MailPool(){
		// Start empty
		pool = new LinkedList<>();
		robots = new LinkedList<>();
	}

	/**
     * Adds an item to the mail pool
     * @param mailItem the mail item being added.
     */
	public void addToPool(MailItem mailItem) {
		pool.add(mailItem);
		pool.sort(new MailItemComparator());
	}

	/**
     * load up any waiting robots with mailItems, if any.
     */
	public void loadItemsToRobot() throws ItemTooHeavyException {
		//List available robots
		ListIterator<Robot> i = robots.listIterator();
		while (i.hasNext()) loadItem(i);
	}
	
	//load items to the robot
	private void loadItem(ListIterator<Robot> i) throws ItemTooHeavyException {
		Robot robot = i.next();
		assert(robot.isEmpty());
		// System.out.printf("P: %3d%n", pool.size());
		if (pool.size() > 0) {
			try {
				robot.addToRobot(pool);
				robot.dispatch(); // send the robot off if it has any items to deliver
				i.remove();       // remove from mailPool queue
			} catch (Exception e) {
	            throw e;
	        }
		}
	}

	/**
     * @param robot refers to a robot which has arrived back ready for more mailItems to deliver
     */	
	public void registerWaiting(Robot robot) { // assumes won't be there already
		robots.add(robot);
	}


	private static class MailItemComparator implements Comparator<MailItem> {
		@Override
		public int compare(MailItem i1, MailItem i2) {
			int order = 0;
			if (i1.destination_floor < i2.destination_floor) {
				order = 1;
			} else if (i1.destination_floor > i2.destination_floor) {
				order = -1;
			}
			return order;
		}
	}
}
