package net.threeple.pg.mon.data;

import java.io.IOException;
import java.util.List;

import net.threeple.pg.mon.model.StorageNode;

public interface StorageNodeRepository {
	public List<StorageNode> getAllStorageNode() throws IOException;
}
