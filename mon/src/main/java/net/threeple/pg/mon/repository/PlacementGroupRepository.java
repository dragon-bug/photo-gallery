package net.threeple.pg.mon.repository;

import java.io.IOException;

public interface PlacementGroupRepository {
	public int[] getPlacementGroupMap() throws IOException;
	public int[] getPlacementGroupsByPsd(int psdId) throws IOException;
}
