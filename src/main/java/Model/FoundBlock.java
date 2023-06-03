package Model;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class FoundBlock {
    Block block;
    BlockFace faceTo;

    public FoundBlock(Block block, BlockFace faceTo) {
        this.block = block;
        this.faceTo = faceTo;
    }

    public Block getBlock() {
        return block;
    }

    public BlockFace getFaceTo() {
        return faceTo;
    }
}
