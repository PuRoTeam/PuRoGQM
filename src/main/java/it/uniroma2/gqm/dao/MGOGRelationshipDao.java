package it.uniroma2.gqm.dao;

import java.util.List;

import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.MGOGRelationship;
import it.uniroma2.gqm.model.MGOGRelationshipPK;

import org.appfuse.dao.GenericDao;


public interface MGOGRelationshipDao extends GenericDao<MGOGRelationship, MGOGRelationshipPK> {
	
	/**
	 * Recupera le relazioni in cui compare il goal, riconoscendone il tipo
	 * @param goal Il Goal di cui recuperare gli associati
	 * @return Una lista di oggetti MGOGRelationship corrispondenti alla relazioni, eventualmente vuota in caso non esistano relazioni
	 */
	public List<MGOGRelationship> getAssociatedRelations(Goal goal);

}
