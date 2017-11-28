package net.threeple.pg.mon.repository;

import java.io.IOException;
import java.util.List;

import net.threeple.pg.mon.node.StorageNode;

public interface StorageNodeRepository {
	public List<StorageNode> getAllNode() throws IOException;
	public StorageNode getNodeById(int id) throws IOException;
}
