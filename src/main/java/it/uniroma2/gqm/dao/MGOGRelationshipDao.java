package it.uniroma2.gqm.dao;

import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.MGOGRelationship;
import it.uniroma2.gqm.model.MGOGRelationshipPK;

import org.appfuse.dao.GenericDao;


public interface MGOGRelationshipDao extends GenericDao<MGOGRelationship, MGOGRelationshipPK> {
	/**
	 * Salva su db un oggetto MGOGRelationship
	 * @param mg Goal MG
	 * @param og Goal OG
	 * @return Un oggetto MGOGRelationship
	 */
	public MGOGRelationship get(Goal mg, Goal og);
	/**
	 * Elimina dal db una relazione MGOGRelationship
	 * @param mgId Goal MG
	 * @param ogId Goal OG
	 */
	public void remove(Goal mg, Goal og);
}
