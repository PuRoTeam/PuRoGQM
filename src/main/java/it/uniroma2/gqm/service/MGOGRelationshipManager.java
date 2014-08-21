package it.uniroma2.gqm.service;

import java.util.List;

import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.MGOGRelationship;
import it.uniroma2.gqm.model.MGOGRelationshipPK;

import org.appfuse.service.GenericManager;

public interface MGOGRelationshipManager extends GenericManager<MGOGRelationship, MGOGRelationshipPK> {  
	
	/**
	 * Elimina la (unica) relazione in cui compare il goal
	 * @param goal Il Goal di cui eliminare la relazione
	 */
	public void remove(Goal goal);
	
	/**
	 * Recupera le relazioni in cui compare il goal, riconoscendone il tipo
	 * @param goal Il Goal di cui recuperare gli associati
	 * @return Un lista di oggetti MGOGRelationship corrispondenti alle relazioni, eventualmente vuota in caso non esistano relazioni
	 */
	public List<MGOGRelationship> getAssociatedRelations(Goal goal);
}
