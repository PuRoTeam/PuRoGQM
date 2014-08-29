package it.uniroma2.gqm.service;

import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.Strategy;

import java.util.Set;

import org.appfuse.service.impl.GenericManagerImpl;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service("gridManager")
public class GridManagerImpl extends GenericManagerImpl<Goal, Long> implements GridManager{

	@Override
    public boolean isGrandChild(Object grandChild, Object grandParent) {
		if(grandParent == grandChild) //prima di controllare eventuali figli, verifico di non aver già trovato il nipote
			return true;		
		
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
    		}
    	}

    	return false; //se ho controllato me stesso e tutti i figli, allora non c'è più niente da fare
    }
	
	public String explorer(Object obj, String s){
		
		String tree = "{";
		
		tree += "\"name\":";
		if(obj instanceof Goal) {
    		Goal g = (Goal) obj;
    		
    		tree += "\""+g.getDescription()+"\"";
    		tree += ",\"parent\":";
    		tree += "\""+s+"\"";
    		tree += ",\"type\":";
    		tree += "\""+0+"\"";
    		tree += ",\"identifier\":";
    		tree += "\""+g.getId()+"\"";
    		
    		if(g.getAssociatedMGs().size() > 0) {
    			tree += ",\"mgs\":[";
    			int i = 0;
    			for (Goal mg : g.getAssociatedMGs()) {
    				if(i != 0)
						tree += ",";
    				//tree += "\""+mg.getDescription()+"\"";
    				tree += mg.getDescription();
    				i++;
				}
    			tree += "]";
    		}
    		
    		if(g.hasChildren()) { //ha figli
    			
    			tree += ", \"children\":[";
    			
    			if(g.areChildrenGoal()) { //i figli sono goal
    				Set<Goal> children = g.getOrgChild();
    				int i = 0;
    				for (Goal goal : children) {
    					if(i != 0)
    						tree += ",";
    					tree += explorer(goal, g.getDescription());
    					i++;
    					
    				}
    				tree += "]}";
    				
    			} else { //i figli sono strategy
    				Set<Strategy> children = g.getOstrategyChild();
    				int i = 0;
    				for (Strategy strategy : children) {
    					if(i != 0)
    						tree += ",";
    					tree += explorer(strategy, g.getDescription());
    					i++;
    				}
    				tree += "]}";
    			}
    			
    		} else { //non ha figli
    			tree += "}";
    		}
    		
    	} else if(obj instanceof Strategy) {
    		Strategy stra = (Strategy)obj;
    		
    		tree += "\""+stra.getName()+"\"";
    		tree += ",\"parent\":";
    		tree += "\""+s+"\"";
    		tree += ",\"type\":";
    		tree += "\""+2+"\"";
    		if(stra.hasChildren()) { //ha figli
    			
    			tree += ", \"children\":[";
    			
    			if(stra.areChildrenGoal()) { //i figli sono goal
    				Set<Goal> children = stra.getSorgChild();
    				int i = 0;
    				for (Goal goal : children) {
    					if(i != 0)
    						tree += ",";
    					tree += explorer(goal, stra.getName());
    					i++;
    					
    				}
    				tree += "]}";
    				
    			} else { //i figli sono strategy
    				Set<Strategy> children = stra.getStrategyChild();
    				int i = 0;
    				for (Strategy strategy : children) {
    					if(i != 0)
    						tree += ",";
    					tree += explorer(strategy, stra.getName());
    					i++;
    					
    				}
    				tree += "]}";
    			}
    			
    		} else { //non ha figli
    			tree += "}"; //oppure devo controllare gli id? In caso positivo, devo controllare solo se sono dello stesso tipo
    		}    		
    	}
		
		//tree += "}";
		
		return tree;
		
	}
	
	public JSONObject saveTreeToJSON(String s){
		return new JSONObject(s);
	}
}
