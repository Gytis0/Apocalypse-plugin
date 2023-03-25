package logic;

public class Stats {
    private static int zombieCount = 0;

    public static int getZombieCount(){
        return zombieCount;
    }

    public static void addZombieCount() {
        zombieCount++;
    }

    public static void reduceZombieCount(){
        zombieCount--;
    }
}
