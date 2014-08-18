package it.uniroma2.gqm.service;

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
	 * Modifica una relazione già esistente
	 * @param goal Il goal di cui modificare la relazione
	 * @param newRelation La nuova relazione da salvare
	 * @return Un oggetto MGOGRelationship, o null in caso di relazione inesistente
	 */
	public MGOGRelationship change(Goal goal, MGOGRelationship newRelation);
	
	/**
	 * Recupera La relazione in cui compare il goal, riconoscendone il tipo
	 * @param goal Il Goal di cui recuperare l'associato
	 * @return Un oggetto MGOGRelationship corrispondente alla relazione, o null in caso non esistano relazioni
	 */
	public MGOGRelationship getAssociatedRelation(Goal goal);
}
