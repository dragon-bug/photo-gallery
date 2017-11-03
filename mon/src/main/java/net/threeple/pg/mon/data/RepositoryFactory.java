package net.threeple.pg.mon.data;

public class RepositoryFactory {
	private static StorageNodeRepository sn = new StorageNodeRepositoryImpl();
	private static PlacementGroupRepository pg = new PlacementGroupRepositoryImpl();
	
	@SuppressWarnings("unchecked")
	public static <T> T getRepository(Class<?> c) {
		if(c.equals(StorageNodeRepository.class)) {
			return (T) sn;
		} else if(c.equals(PlacementGroupRepository.class)) {
			return (T) pg;
		}
		return null;
	}
	
	public static void main(String[] args) {
		StorageNodeRepository snr = RepositoryFactory.getRepository(StorageNodeRepository.class);
		PlacementGroupRepository pgr = RepositoryFactory.getRepository(PlacementGroupRepository.class);
		System.out.println(snr.getClass().getName() + ", " + pgr.getClass());
	}
}
