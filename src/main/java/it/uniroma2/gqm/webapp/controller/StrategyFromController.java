package it.uniroma2.gqm.webapp.controller;

import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.Project;
import it.uniroma2.gqm.model.Strategy;
import it.uniroma2.gqm.service.GoalManager;
import it.uniroma2.gqm.service.StrategyManager;

import java.beans.PropertyEditorSupport;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.appfuse.model.User;
import org.appfuse.service.GenericManager;
import org.appfuse.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/strategyform*")
@SessionAttributes({"currentProject","strategy","currentUser"})
public class StrategyFromController  extends BaseFormController {
	
	@Autowired
	private StrategyManager strategyManager;
	@Autowired
    private GoalManager goalManager;
    private GenericManager<Project, Long> projectManager = null;
    private UserManager userManager = null;
    
    @Autowired
    public void setProjectManager(@Qualifier("projectManager") GenericManager<Project, Long> projectManager) {
        this.projectManager = projectManager;
    }

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
    public StrategyFromController() {
        setCancelView("redirect:strategies");
        setSuccessView("redirect:strategies");
    }
    
    
    @RequestMapping(method = RequestMethod.GET)
    protected Strategy showForm(HttpServletRequest request, HttpSession session, Model model)
    throws Exception {
        String id = request.getParameter("id");
        Strategy ret = null; 
        Project currentProject = (Project) session.getAttribute("currentProject");
        User currentUser = userManager.getUserByUsername(request.getRemoteUser());
        
        if (!StringUtils.isBlank(id)) {
            ret = strategyManager.get(new Long(id));
        }else {
        	ret = new Strategy();
        	ret.setStrategyOwner(currentUser);
        	ret.setProject(currentProject);
        }      
        
        List<Goal> oGoalsAll = goalManager.getOrganizationalGoal(currentProject);
		List<Strategy> allStragies = strategyManager.getAll(); //TODO cambiare in Strategy 
		
        model.addAttribute("currentUser",currentUser);
        model.addAttribute("oGoalsAll", oGoalsAll);
        model.addAttribute("strategies", allStragies);
        return ret;
    }    
    
    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Strategy strategy, BindingResult errors, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }
 
        if (validator != null) { // validator is null during testing
            validator.validate(strategy, errors);
 
            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "strategyform";
            }
        }
 
        log.debug("entering 'onSubmit' method...");
 
        boolean isNew = (strategy.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();
                
        if (request.getParameter("delete") != null) {
        	
        	Strategy sDB = strategyManager.get(strategy.getId());
        	
        	if(strategyManager.hasChildren(sDB)) {
    			//TODO Attenzione!!! potrebbe non funzionare
        		errors.rejectValue("childType", "childType", "Can't delete Strategy with children"); 
        		return "strategyform";
        	} else {
        		
        		if (sDB.getParentType() == 0) {
					
        			Goal oParent = sDB.getSorgParent(); 
					oParent.getOrgChild().remove(sDB); 	
					goalManager.save(oParent); 	
					sDB.setSorgParent(null); 
				
        		}else {
					Strategy sParent = sDB.getStrategyParent();
					sParent.getSorgChild().remove(sDB);
					strategyManager.save(sParent);
					sDB.setStrategyParent(null);
				}
        		
        		strategyManager.save(sDB); 	
        	}
        	
        	strategyManager.remove(strategy.getId());
            saveMessage(request, getText("strategy.deleted", locale));
            
        } else {

            
          //#####################INIZIO PARENT CHILDREN###########################

    		if(isNew){ //creazione
    			
    			if (strategyManager.hasParent(strategy)) { //ha padre
    				if (strategy.getParentType() == 0) { //ha padre Goal
						
    					Goal oParent = goalManager.get(strategy.getSorgParent().getId());
    					oParent.getOstrategyChild().add(strategy);
    					strategy.setSorgParent(oParent);
    					goalManager.save(oParent);
    					
					} else { //ha padre Strategy
						
						Strategy sParent = strategyManager.get(strategy.getStrategyParent().getId());
						sParent.getStrategyChild().add(strategy);
						strategy.setStrategyParent(sParent);
						strategyManager.save(sParent);
					}
					
    				strategyManager.save(strategy);
    				
				} else {
					
					errors.rejectValue("parentType", "parentType", "Strategy needs parent"); 
            		return "strategyform";
				}
    			
    			if(strategyManager.hasChildren(strategy)) { //ha figli
   	        		if (strategy.getChildType() == 0) { //ha figli Goal
    						
   	        			for (Goal g : strategy.getSorgChild()) {
   	        				g.setOstrategyParent(strategy);		//imposto il padre
   	        				strategy.getSorgChild().add(g);	//aggiungo il figlio a 'strategy'
   	        				goalManager.save(g);		//salvo il figlio
						}
    					
    	        	}else {	//ha figli Strategy
  
    	        		for (Strategy s : strategy.getStrategyChild()) {
    	        			s.setStrategyParent(strategy);
    	        			strategy.getStrategyChild().add(s);
    	        			strategyManager.save(s);
						}
    	        	}
   	        		
   	        		strategyManager.save(strategy); 			//salvo la strategy
        			
            	}//else non devo fare niente
    			
        	} else { //aggiornamento
        		
        		
        		Strategy sDB = strategyManager.get(strategy.getId());
        		
        		boolean pSameType = (sDB.getParentType() == strategy.getParentType()) ? true : false;
        		int parentType = strategy.getParentType();
        		boolean pOSame = (sDB.getSorgParent().getId() == strategy.getSorgParent().getId()) ? true : false;
        		boolean pSSame = (sDB.getStrategyParent().getId() == strategy.getStrategyParent().getId()) ? true : false;
        		
        		//stesso tipo padre
        		if (pSameType) { 

        			//stesso padre Goal
        			if (parentType == 0) { 
        			
        				//è cambiato il padre Goal
        				if(!pOSame){
        					/**
        					 * tolgo il figlio al vecchio padre
        					 * ho cambiato il padre
        					 * aggiungo il figlio al nuovo padre
        					 */
        					sDB.getSorgParent().getOrgChild().remove(strategy);
        					goalManager.save(sDB.getSorgParent());
        					
        					sDB.setSorgParent(null);
        					strategyManager.save(sDB);
        					
        					strategy.getSorgParent().getOstrategyChild().add(strategy);
        				}
        				
        			//stesso padre Strategy
					} else {
						
						//è cambiato il padre Strategy
						if(!pSSame) {
							sDB.getStrategyParent().getSorgChild().remove(strategy);
							strategyManager.save(sDB.getStrategyParent());
							
							sDB.setStrategyParent(null);
							strategyManager.save(sDB);
							
							strategy.getStrategyParent().getStrategyChild().add(strategy);
						}
					
					}// else non è cambiato niente, ma è rimasto null
        			
        			strategyManager.save(strategy);
					
				} else { //non stesso tipo padre, che tipo è?
					
						
					//se il nuovo padre è un Goal e il vecchio padre era una Strategy
					if (strategy.getParentType() == 0) {
						
						sDB.getStrategyParent().getSorgChild().remove(strategy);
						strategyManager.save(sDB.getStrategyParent());
						
						sDB.setStrategyParent(null);
						strategyManager.save(sDB);
						
						strategy.getSorgParent().getOstrategyChild().add(strategy);
						
					//se il nuovo padre è una Strategy e il vecchio padre era un Goal
					} else {
						
						sDB.getSorgParent().getOstrategyChild().remove(strategy);
						goalManager.save(sDB.getSorgParent());
						
						sDB.setSorgParent(null);
						strategyManager.save(sDB);
						
						strategy.getStrategyParent().getStrategyChild().add(strategy);
						
					}
					
					strategyManager.save(strategy);
				}
        		
        		boolean cSameType = (sDB.getChildType() == strategy.getChildType()) ? true : false;
        		boolean cOSame = (sDB.getSorgChild() == strategy.getSorgChild()) ? true : false;
        		boolean cSSame = (sDB.getStrategyChild() == strategy.getStrategyChild()) ? true : false;
        		
        		//se stesso tipo figlio
        		if (cSameType) { 
        			
        			//se stesso tipo Goal
        			if(strategy.getChildType() == 0){
            			
        				//se non stesso figlio Goal
            			if (!cOSame) {
            				if(!strategy.getSorgChild().containsAll(sDB.getSorgChild())){
            					errors.rejectValue("childType", "childType", "Can't remove children from here!"); 
    		            		return "strategyform";
            				}
						} //else non fai niente
            			
            		//se stesso tipo Strategy
        			} else if(strategy.getChildType() == 1) {
        				
        				//se non stesso figlio Stretegy
						if (!cSSame) {
							if(!strategy.getStrategyChild().containsAll(sDB.getStrategyChild())){
            					errors.rejectValue("childType", "childType", "Can't remove children from here!"); 
    		            		return "strategyform";
            				}
						} 
						
        			} //else non è cambiato niente, ma è rimasto null
        			
        			strategyManager.save(strategy);
        			
				} else { //non stesso tipo figlio, male male!!!
					
					errors.rejectValue("childType", "childType", "Can't change children type!"); 
            		return "strategyform";
				} 
        	}
        	
        	//#####################FINE PARENT CHILDREN#######################
            
        	strategyManager.save(strategy);
            String key = (isNew) ? "strategy.added" : "strategy.updated";
            saveMessage(request, getText(key, locale));
    		
            if (!isNew) {
            	success = "redirect:strategyform?id=" + strategy.getId();
        	}
        } 
        return getSuccessView();
    }
    
    /*******************Binder*******************/
    
    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
    	binder.registerCustomEditor(Strategy.class, "strategyParent", new strategyParentEditorSupport());
    	binder.registerCustomEditor(Goal.class, "sorgParent", new sorgParentEditorSupport());
    	
    	binder.registerCustomEditor(Set.class, "strategyChild", new strategyChildCollectionEditor(Set.class));
    	binder.registerCustomEditor(Set.class, "sorgChild", new sorgChildCollectionEditor(Set.class));
    }
    
    private class strategyParentEditorSupport extends PropertyEditorSupport {
    	public void setAsText(String text) throws IllegalArgumentException {
    		if(text != null && StringUtils.isNotBlank((String)text)) {
    			Long id = new Long(text);
    			
    			if(id != -1) {
    				Strategy strategy = strategyManager.get(id);
    				setValue(strategy);
    			} else {
    				setValue(null);
    			}
    		}
    	}
    }
    
    private class sorgParentEditorSupport extends PropertyEditorSupport {
    	public void setAsText(String text) throws IllegalArgumentException {
    		if(text != null && StringUtils.isNotBlank((String)text)) {
    			Long id = new Long(text);
    			
    			if(id != -1) {
    				Goal goal = goalManager.get(id);
    				setValue(goal);
    			} else {
    				setValue(null);
    			}
    		}
    	}
    }
    
    private class strategyChildCollectionEditor extends CustomCollectionEditor {
    	private strategyChildCollectionEditor(Class collectionType) {
    		super(collectionType);
    	}
    	
    	protected Object convertElement(Object element) {
    		if (element != null && StringUtils.isNotBlank((String)element)) {
    			Long id = new Long((String)element);
    			
    			if(id != -1) {
    				Strategy strategy = strategyManager.get(id);    				
    				return strategy;
    			}
    		}
    		return null;
    	}
    }
    
    private class sorgChildCollectionEditor extends CustomCollectionEditor {
    	private sorgChildCollectionEditor(Class collectionType) {
    		super(collectionType);
    	}
    	
    	protected Object convertElement(Object element) {
    		if (element != null && StringUtils.isNotBlank((String)element)) {
    			Long id = new Long((String)element);
    			
    			if(id != -1) {
    				Goal goal = goalManager.get(id);
    				return goal;
    			}
    		}
    		return null;
    	}
    }
}
