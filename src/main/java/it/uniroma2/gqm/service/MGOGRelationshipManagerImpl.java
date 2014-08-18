package it.uniroma2.gqm.service;

import it.uniroma2.gqm.dao.MGOGRelationshipDao;
import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.GoalType;
import it.uniroma2.gqm.model.MGOGRelationship;
import it.uniroma2.gqm.model.MGOGRelationshipPK;

import org.appfuse.service.impl.GenericManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("mgogRelationshipManager")
public class MGOGRelationshipManagerImpl extends GenericManagerImpl<MGOGRelationship, MGOGRelationshipPK> implements MGOGRelationshipManager {
	MGOGRelationshipDao mgogRelationshipDao;
	
	@Autowired
	public MGOGRelationshipManagerImpl(MGOGRelationshipDao mgogRelationshipDao) {
		super(mgogRelationshipDao);
		this.mgogRelationshipDao = mgogRelationshipDao;
	}

	@Override
	public void remove(Goal goal) {
		if(goal == null)
			return;
		
		if(GoalType.isMG(goal))
			mgogRelationshipDao.remove(goal.getRelationWithOG());
		else if(GoalType.isOG(goal))
			mgogRelationshipDao.remove(goal.getRelationWithMG());
	}
	
	@Override
	public MGOGRelationship getAssociatedRelation(Goal goal) {
		return mgogRelationshipDao.getAssociatedRelation(goal);
	}

	@Override
	public MGOGRelationship change(Goal goal, MGOGRelationship newRelation) {
		if(goal == null)
			return null;
		
		try {
			if(newRelation != null) {
				Goal mg = newRelation.getPk().getMg();
				Goal og = newRelation.getPk().getOg();				
				isMGOG(mg, og);				
			}
		}
		catch(Exception e) {
			return null;
		}
		
		MGOGRelationship oldRelation = mgogRelationshipDao.getAssociatedRelation(goal);
		
		if(oldRelation == null && newRelation == null) { //nulla da fare
			return null;	
		}	
		else if(oldRelation == null && newRelation != null) { //nessuna relazione esistente, creane una nuova
			try {
				return save(newRelation);
			}
			catch(Exception e) {
				return null;
			}
		}	
		else if(oldRelation != null && newRelation == null) { //elimina la vecchia relazione
			remove(oldRelation);
		}
		else if(oldRelation != null && newRelation != null) { //elimina la vecchia relazione e creane una nuova
			remove(oldRelation);
			return save(newRelation);
		}
		
		return null;
	}

	/**
	 * Verifica il tipo di goal
	 * 
	 * @param goal1
	 * @param goal2
	 * @return true se goal1 MG, goal2MG e false in caso contrario
	 * @throws Exception Quando lo stato dei goal è inconsistente (due MG o due OG), o uno/due goal nulli
	 */
	private boolean isMGOG(Goal goal1, Goal goal2) throws Exception {
		if(goal1 == null || goal2 == null)
			throw new Exception("Goal null");
		
		if(GoalType.isMG(goal1) && GoalType.isOG(goal2)) //goal1 MG, goal2 OG
			return true;
		else if(GoalType.isOG(goal1) && GoalType.isMG(goal2)) //goal1 OG, goal2 MG  
			return false;
		else
			throw new Exception("Stato dei goal incoerenti (due OG o due MG)");
	}
}
