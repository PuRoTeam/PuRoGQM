package it.uniroma2.gqm.webapp.controller;

import it.uniroma2.gqm.model.BinaryElement;
import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.Metric;
import it.uniroma2.gqm.model.Project;
import it.uniroma2.gqm.service.BinaryTableManager;
import it.uniroma2.gqm.service.GoalManager;
import it.uniroma2.gqm.service.MetricManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.appfuse.model.User;
import org.appfuse.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/binarytable*")
@SessionAttributes({"currentProject","currentUser"})
public class BinaryTableController {	
	
	@Autowired
    private GoalManager goalManager;
	private MetricManager metricManager;
	private UserManager userManager;
	
	@Autowired
	private BinaryTableManager binaryManager;
	
	@Autowired
    public void setMetricManager(@Qualifier("metricManager") MetricManager metricManager) {
        this.metricManager = metricManager;
    }

	@Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
	
	@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Goal showTable(HttpServletRequest request,HttpSession session, Model model) throws Exception {
		
        String id = request.getParameter("id");
        Goal ret = null;
        Set<MGOGRelationship> retRelation = new HashSet<MGOGRelationship>();
        Set<Goal> mgs = new HashSet<Goal>();
        
        Project currentProject = (Project) session.getAttribute("currentProject");
        User currentUser = userManager.getUserByUsername(request.getRemoteUser());

        if (!StringUtils.isBlank(id)) {
        	
        	//Recupera goal selezionato
        	ret = goalManager.get(new Long(id));
        	
        	//Recupera associazioni con MG
        	retRelation = ret.getRelationsWithMG();
        	
            boolean satisfyAll = true;
            BinaryElement mainGoal = new BinaryElement(ret, 0);
        	
        	//Recupero l'MG da ogni MGOGRelationship  
        	for (MGOGRelationship mgog : retRelation) {
        		
				mgs.add(mgog.getPk().getMg());
				
				//Recupero tutte le metriche associate ad ogni MG
	            for (Goal mg : mgs) {
	            	List<Metric> metrics = goalManager.getMeasuredMetricByGoal(mg);
	            	
	            	if (metrics.size() > 0) {
	            		boolean satisfy = true;
		            	//Calcolo valore di soddisfacimento (1 o 0)
		                for(Metric m: metrics){
		                	satisfy &= metricManager.getSatisfaction(m);
		                	satisfyAll &= satisfy;
		                }
		                if(satisfyAll)
		                	mainGoal.setValue(1);
					} else {
						mainGoal.setValue(0);
					}
	            	
	    		}
			}
        	
            Set<BinaryElement> childGoal = new HashSet<BinaryElement>();
 
            //Recupera la lista degli OG figli di questo OG
            Set<Goal> set = binaryManager.findOGChildren(goalManager.get(Long.parseLong(id)));
            
            for (Goal g : set) {
            	
            	mgs.clear();
            	retRelation.clear();
            	
            	//Recupera associazioni con MG
            	retRelation = g.getRelationsWithMG();
            	
                satisfyAll = true;
                BinaryElement gGoal = new BinaryElement(g, 0);
            	
            	//Recupero l'MG da ogni MGOGRelationship  
            	for (MGOGRelationship mgog : retRelation) {
            		
    				mgs.add(mgog.getPk().getMg());
    				
    				//Recupero tutte le metriche associate ad ogni MG
    	            for (Goal mg : mgs) {
    	            	List<Metric> metrics = goalManager.getMeasuredMetricByGoal(mg);
    	            	
    	            	boolean satisfy = true;
    	            	if(metrics.size() > 0){
	  	            		//Calcolo valore di soddisfacimento (1 o 0)
	    	                for(Metric m: metrics){
	    	                	satisfy &= metricManager.getSatisfaction(m);
	    	                	satisfyAll &= satisfy;
	    	                }
	    	                if(satisfyAll) {
	    	                	gGoal.setValue(1);
	    	                }
    	            	} else {
    	            		gGoal.setValue(0);
    	            	}
    	    		}
    			}
            	childGoal.add(gGoal);
			}
            
            //Stampa di debug
            System.out.println("Figli OG di: "+ret.getDescription());
    		for (Goal s : set) {
    			System.out.println(s.getDescription());
    		}
    		
    		Set<String> suggestions = getSuggestion(mainGoal, childGoal);
           
    		model.addAttribute("suggestions", suggestions);
    		model.addAttribute("mainGoal", mainGoal);
    		model.addAttribute("childGoal", childGoal);
    		model.addAttribute("currentUser",currentUser);
    		model.addAttribute("currentProject",currentProject);
    		
        }
        
        return ret;
    }
	
	public Set<String> getSuggestion(BinaryElement b, Set<BinaryElement> setB){
		
		String s1 = "Enforce strategies";
		String s2 = "Strategies not sufficient or not effective";
		String s2b = "Assumptions wrong";
		String s3 = "Check magnitudes (less was sufficient)";
		String s3b = "Check root causes for achieving "; //TODO aggiungere sempre ID
		String s4 = "Good work!!!";
		String s5 = "Refinements wrong";
		String s6 = "Strategies may help";
		
		boolean valueSetB = true;
		Set<String> suggestions = new HashSet<String>();
		
		for (BinaryElement elto : setB)
			valueSetB &= (elto.getValue() == 0);
		
		//If parent not achieved
		if (b.getValue() == 0) {
			//If all children were achieve
			if(valueSetB){
				//If parent's first children was Strategy
				if(b.getGoal().getChildType() == 1) {
					suggestions.add(s2);
					suggestions.add(s2b);
				//If parent's first children was Goal
				} else {
					suggestions.add(s5);
				}
			//If at least was not achieve
			} else {
				//If parent's first children was Strategy
				if(b.getGoal().getChildType() == 1) {
					suggestions.add(s1);
				//If parent's first children was Goal
				} else {
					suggestions.add(s6);
				}
			}
		//If parent achieved
		} else {
			//If all children were achieve
			if(valueSetB){
				//If parent's first children was Strategy
				if(b.getGoal().getChildType() == 1) {
					suggestions.add(s4);
				//If parent's first children was Goal
				} else {
					suggestions.add(s4);
				}
			//If at least was not achieve	
			} else {
				//If parent's first children was Strategy
				if(b.getGoal().getChildType() == 1) {
					suggestions.add(s3);
					suggestions.add(s3b+b.getGoal().getDescription());
				//If parent's first children was Goal
				} else {
					suggestions.add(s5);
				}
			}
		}
		
		
		
		return suggestions;
	}
	
}
