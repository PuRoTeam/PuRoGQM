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
	public MGOGRelationship getMGOGRelationship(Goal goal1, Goal goal2) {		
		if(goal1 == null || goal2 == null)
			return null;
		
		boolean g1MG_g2OG = true;		
		try {
			g1MG_g2OG = isMGOG(goal1, goal2);
		}
		catch(Exception e) {
			return null;
		}
		
		MGOGRelationship rel = g1MG_g2OG ? 
				mgogRelationshipDao.get(goal1, goal2) : 
					mgogRelationshipDao.get(goal2, goal1);  
    	
		return rel;
	}

	@Override
	public MGOGRelationship save(Goal goal1, Goal goal2) {
		if(goal1 == null || goal2 == null)
			return null;
		
		boolean g1MG_g2OG = true;		
		try {
			g1MG_g2OG = isMGOG(goal1, goal2);
		}
		catch(Exception e) {
			return null;
		}
		
		Goal mg = g1MG_g2OG ? goal1 : goal2;
		Goal og = g1MG_g2OG ? goal2 : goal1;
		
		//MGOGRelationshipPK relPK = new MGOGRelationshipPK();
		//relPK.setMgID(mg.getId());
		//relPK.setOgID(og.getId());
		MGOGRelationshipPK relPK = new MGOGRelationshipPK();
		relPK.setMg(mg);
		relPK.setOg(og);
		
		MGOGRelationship rel = new MGOGRelationship();
		rel.setPk(relPK);
		//rel.setMg(mg);
		//rel.setOg(og);
		return save(rel);
	}
	
	@Override
	public void remove(Goal goal1, Goal goal2) {
		if(goal1 == null || goal2 == null)
			return;
		
		boolean g1MG_g2OG = true;		
		try {
			g1MG_g2OG = isMGOG(goal1, goal2);
		}
		catch(Exception e) {
			return;
		}
		
		if(g1MG_g2OG)
			mgogRelationshipDao.remove(goal1, goal2);
		else
			mgogRelationshipDao.remove(goal2, goal1);
	}
	
	@Override
	public MGOGRelationship change(Goal goal, Goal oldAssociatedGoal, Goal newGoalToAssociate) {

		if(goal == null)
			return null;
		
		if(oldAssociatedGoal == null && newGoalToAssociate == null) { //nulla da fare
			return null;	
		}	
		else if(oldAssociatedGoal == null && newGoalToAssociate != null) { //nessuna relazione, creane una nuova
			try {
				isMGOG(goal, newGoalToAssociate); //tipo di goal coerente?
				return save(goal, newGoalToAssociate);
			}
			catch(Exception e) {
				return null;
			}
		}	
		else if(oldAssociatedGoal != null && newGoalToAssociate == null) { //elimina la vecchia relazione
			remove(goal, oldAssociatedGoal);
		}
		else if(oldAssociatedGoal != null && newGoalToAssociate != null) { //elimina la vecchia relazione e creane una nuova
			try {
				isMGOG(goal, newGoalToAssociate); //tipo di goal coerente?
				remove(goal, oldAssociatedGoal);
				return save(goal, newGoalToAssociate);
			}
			catch(Exception e) {
				return null;
			}
		}
		
		return null;
	}
	
	@Override
	public MGOGRelationship getAssociatedRelation(Goal goal) {
		return mgogRelationshipDao.getAssociatedRelation(goal);
	}
	
	@Override
	public Goal getAssociatedGoal(Goal goal) {
		if(goal == null)
			return null;
		
		MGOGRelationship relation = getAssociatedRelation(goal);
		if(relation == null)
			return null;
		
		return GoalType.isMG(goal)? relation.getPk().getOg() : relation.getPk().getMg();
	}
	
	/**
	 * Verifica il tipo di goal
	 * 
	 * @param goal1
	 * @param goal2
	 * @return true se goal1 MG, goal2MG e false in caso contrario
	 * @throws Exception Quando lo stato dei goal è inconsistente (due MG o due OG)
	 */
	private boolean isMGOG(Goal goal1, Goal goal2) throws Exception {
		if(GoalType.isMG(goal1) && GoalType.isOG(goal2)) //goal1 MG, goal2 OG
			return true;
		else if(GoalType.isOG(goal1) && GoalType.isMG(goal2)) //goal1 OG, goal2 MG  
			return false;
		else
			throw new Exception("Stato dei goal incoerenti (due OG o due MG)");
	}
}
