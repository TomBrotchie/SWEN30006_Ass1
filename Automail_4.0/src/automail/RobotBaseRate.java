package automail;

/**
 * New Class: Team 03
 * Record the base charging rate of different types of robot
 */
public enum RobotBaseRate {

    REGULAR(0.025),
    FAST(0.05),
    BULK(0.01);


    private final double baseRate;

    private RobotBaseRate(double price) {
        this.baseRate = price;
    }

    public double getBaseRate() { return baseRate; }
}
