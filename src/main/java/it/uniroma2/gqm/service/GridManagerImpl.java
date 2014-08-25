package it.uniroma2.gqm.service;

import java.util.Set;

import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.Strategy;

import org.appfuse.service.impl.GenericManagerImpl;
import org.springframework.stereotype.Service;

@Service("gridManager")
public class GridManagerImpl extends GenericManagerImpl<Goal, Long> implements GridManager{

	@Override
    public boolean isGrandChild(Object grandChild, Object grandParent) {
    	if(grandParent instanceof Goal) {
    		Goal goalGrandParent = (Goal)grandParent;
    		
    		if(goalGrandParent.hasChildren()) { //ha figli
    			if(goalGrandParent.areChildrenGoal()) { //i figli sono goal
    				Set<Goal> children = goalGrandParent.getOrgChild();
    				
    				for(Goal child : children) {
    					boolean found = isGrandChild(grandChild, child);
    					if(found)
    						return true;
    				}
    				
    			} else { //i figli sono strategy
    				Set<Strategy> children = goalGrandParent.getOstrategyChild();
    				
    				for(Strategy child : children) {
    					boolean found = isGrandChild(grandChild, child);
    					if(found)
    						return true;
    				}
    			}
    		} else { //non ha figli
    			return grandParent == grandChild; //oppure devo controllare gli id? In caso positivo, devo controllare solo se sono dello stesso tipo
    		}
    		
    	} else if(grandParent instanceof Strategy) {
    		Strategy strategyGrandParent = (Strategy)grandParent;
    		
    		if(strategyGrandParent.hasChildren()) { //ha figli
    			if(strategyGrandParent.areChildrenGoal()) { //i figli sono goal
    				Set<Goal> children = strategyGrandParent.getSorgChild();
    				
    				for(Goal child : children) {
    					boolean found = isGrandChild(grandChild, child);
    					if(found)
    						return true;
    				}
    				
    			} else { //i figli sono strategy
    				Set<Strategy> children = strategyGrandParent.getStrategyChild();
    				
    				for(Strategy child : children) {
    					boolean found = isGrandChild(grandChild, child);
    					if(found)
    						return true;
    				}
    			}
    		} else { //non ha figli
    			return grandParent == grandChild; //oppure devo controllare gli id? In caso positivo, devo controllare solo se sono dello stesso tipo
    		}    		
    	}

    	return false;
    }
}
