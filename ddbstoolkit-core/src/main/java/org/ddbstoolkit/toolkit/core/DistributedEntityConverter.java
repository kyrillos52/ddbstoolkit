package org.ddbstoolkit.toolkit.core;

import java.util.List;

/**
 * Converter which add the peer UID for the data
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class DistributedEntityConverter {

	/**
	 * Peer element
	 */
	private Peer peer;

	/**
	 * Distributed Entity converter
	 * @param peer Peer
	 */
	public DistributedEntityConverter(Peer peer) {
		super();
		this.peer = peer;
	}
	
	/**
	 * Enrich list with peer UID
	 * @param <T> IEntity extended entity
	 * @param list List of entities with peer UID
	 * @return List of entities with peer UID
	 */
	public <T extends IEntity> List<T> enrichWithPeerUID(List<T> list) {
		for(T entity : list) {
			enrichWithPeerUID(entity);
		}
		return list;
	}
	
	/**
	 * Enrich element with peer UID
	 * @param <T> IEntity extended entity
	 * @param entity Entity to enrich
	 * @return Enriched entity
	 */
	public <T extends IEntity> T enrichWithPeerUID(T entity) {
		if(entity instanceof DistributedEntity) {
			((DistributedEntity)entity).setPeerUid(peer.getUid());
		}
		return entity;
	}
}
