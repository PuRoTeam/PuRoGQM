package it.uniroma2.gqm.service;

import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.MGOGRelationship;
import it.uniroma2.gqm.model.MGOGRelationshipPK;

import org.appfuse.service.GenericManager;

public interface MGOGRelationshipManager extends GenericManager<MGOGRelationship, MGOGRelationshipPK> {
	/**
	 * Salva su db un oggetto MGOGRelationship
	 * @param mgId ID del goal MG
	 * @param ogId ID del goal OG
	 * @return Un oggetto MGOGRelationship
	 */
	public MGOGRelationship getMGOGRelationship(Long mgId, Long ogId);
	/**
	 * Identifica quale sia il goal MG, e quale il goal OG e restituisce l'oggetto MGOGRelationship relativo
	 * @param goal1 Il primo Goal
	 * @param goal2 Il secondo Goal
	 * @return Un oggetto MGOGRelationship o null nel caso in cui non esista relazione,
	 * o la tipologia dei goal sia incoerente (due OG, due MG, goal null)
	 */
	public MGOGRelationship getMGOGRelationship(Goal goal1, Goal goal2);
	/**
	 * Identifica quale sia il goal MG, e quale il goal OG e salva su db la relazione
	 * @param goal1 Il primo Goal
	 * @param goal2 Il secondo Goal
	 * @return Il nuovo oggetto MGOGRelationship, o null in caso di incoerenza nella tipologia dei goal (due OG, due MG, goal null)
	 */
	public MGOGRelationship save(Goal goal1, Goal goal2);
	
	/**
	 * Identifica quale sia il goal MG e quale il goal OG, ed elimina la relativa relazione, se esistente
	 * @param g1 Il primo Goal
	 * @param g2 Il secondo Goal
	 */
	public void remove(Goal goal1, Goal goal2);
	
	/**
	 * Modifica una relazione già esistente.
	 * Si presuppone che il campo "associatedGoal" di goal, sia già stato modificato in newGoalToAssociate
	 * @param goal Il goal di cui stiamo modificando la relazione
	 * @param oldAssociatedGoal Il goal precedentemente associato a "goal"
	 * @param newGoalToAssociate Il nuovo goal da associare a "goal"
	 * @return Un oggetto MGOGRelationship, o null in caso di relazione inesistente o incoerenza nel tipo di goal (due OG, due MG, goal null)
	 */
	public MGOGRelationship change(Goal goal, Goal oldAssociatedGoal, Goal newGoalToAssociate);
}
