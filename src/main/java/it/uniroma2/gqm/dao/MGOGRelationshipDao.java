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
	 * Recupera La relazione in cui compare il goal, riconoscendone il tipo
	 * @param goal Il Goal di cui recuperare l'associato
	 * @return Un oggetto MGOGRelationship corrispondente alla relazione, o null in caso non esistano relazioni
	 */
	public MGOGRelationship getAssociatedRelation(Goal goal);
	
	/**
	 * Elimina dal db una relazione MGOGRelationship
	 * @param mgId Goal MG
	 * @param ogId Goal OG
	 */
	public void remove(Goal mg, Goal og);
}
