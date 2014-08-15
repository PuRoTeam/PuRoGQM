package it.uniroma2.gqm.dao;

import it.uniroma2.gqm.model.MGOGRelationship;
import it.uniroma2.gqm.model.MGOGRelationshipPK;

import org.appfuse.dao.GenericDao;


public interface MGOGRelationshipDao extends GenericDao<MGOGRelationship, MGOGRelationshipPK> {
	/**
	 * Salva su db un oggetto MGOGRelationship
	 * @param mgId ID del goal MG
	 * @param ogId ID del goal OG
	 * @return Un oggetto MGOGRelationship
	 */
	public MGOGRelationship get(Long mgId, Long ogId);
	
	/**
	 * Elimina dal db una relazione MGOGRelationship
	 * @param mgId ID del goal MG
	 * @param ogId ID del goal OG
	 */
	public void remove(Long mgId, Long ogId);
}
