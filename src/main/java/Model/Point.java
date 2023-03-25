package Model;

import org.bukkit.block.Block;

public class Point {
    Block block;
    int length;

    public Point(Block block) {
        this.block = block;
        this.length = 0;
    }

    public Point(Block block, int length) {
        this.block = block;
        this.length = length;
    }

    public Block getBlock() {
        return block;
    }

    public int getLength() {
        return length;
    }
}
