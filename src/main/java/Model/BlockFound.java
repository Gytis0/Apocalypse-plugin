package Model;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockFound {
    Block block;
    BlockFace foundFrom;

    public BlockFound(Block block, BlockFace faceTo) {
        this.block = block;
        this.foundFrom = faceTo;
    }

    public Block getBlock() {
        return block;
    }

    public BlockFace getFoundFrom() {
        return foundFrom;
    }
}
