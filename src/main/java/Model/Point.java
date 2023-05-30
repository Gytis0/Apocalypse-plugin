package Model;

import org.bukkit.block.Block;

public class Point {
    Block block;
    int length;
    int obstaclesReached;

    public Point(Block block) {
        this.block = block;
        this.length = 0;
        this.obstaclesReached = 0;
    }

    public Point(Block block, int length, int obstaclesReached) {
        this.block = block;
        this.length = length;
        this.obstaclesReached = obstaclesReached;
    }

    public Block getBlock() {
        return block;
    }

    public int getLength() {
        return length;
    }

    public int getObstaclesReached() {
        return obstaclesReached;
    }
}
