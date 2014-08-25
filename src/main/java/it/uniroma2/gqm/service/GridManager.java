package it.uniroma2.gqm.service;

import it.uniroma2.gqm.model.Goal;

import org.appfuse.service.GenericManager;

public interface GridManager extends GenericManager<Goal, Long> {

    /**
     * Verificare che grandChild sia nipote (o figlio) di grandParent 
     * @param grandChild Il possibile nipote
     * @param grandParent Il possibile nonno
     * @return true nel caso in cui grandChild sia nipote di grandParent
     */
	public boolean isGrandChild(Object grandChild, Object grandParent);
}
