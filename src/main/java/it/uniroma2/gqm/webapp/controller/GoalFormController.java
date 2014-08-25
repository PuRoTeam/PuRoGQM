package it.uniroma2.gqm.webapp.controller;

import org.apache.commons.lang.StringUtils;
import org.appfuse.model.User;
import org.appfuse.service.GenericManager;
import org.appfuse.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import it.uniroma2.gqm.model.*;
import it.uniroma2.gqm.service.GoalManager;
import it.uniroma2.gqm.service.GridManager;
import it.uniroma2.gqm.service.MGOGRelationshipManager;
import it.uniroma2.gqm.service.StrategyManager;
import it.uniroma2.gqm.webapp.util.RequestUtil;

@Controller
@RequestMapping("/goalform*")
@SessionAttributes({"availableUsers","goal","currentUser","strategies","availableGoals"})
public class GoalFormController extends BaseFormController {
    	
    @Autowired
    private GoalManager goalManager;
    
    private UserManager userManager = null;

    private GenericManager<Project, Long> projectManager = null;
    
    @Autowired
    private StrategyManager strategyManager;

    private MGOGRelationshipManager mgogRelationshipManager;
    
    private GridManager gridManager;
    
    @Autowired
    public void setGridManager(@Qualifier("gridManager") GridManager gridManager) {
    	this.gridManager = gridManager;
    }
    
    @Autowired
	public void setMgogRelationshipManager(@Qualifier("mgogRelationshipManager") MGOGRelationshipManager mgogRelationshipManager) {
		this.mgogRelationshipManager = mgogRelationshipManager;
	}

	@Autowired
    public void setProjectManager(@Qualifier("projectManager") GenericManager<Project, Long> projectManager) {
        this.projectManager = projectManager;
    }
    
    @Autowired
    public void setGoalManager(@Qualifier("goalManager") GoalManager goalManager) {
        this.goalManager = goalManager;
    }


    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
    
    public GoalFormController() {
        setCancelView("redirect:goals");
        setSuccessView("redirect:goals");
    }
 
    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Goal showForm(HttpServletRequest request,HttpSession session, Model model)
    throws Exception {
        String id = request.getParameter("id");
        Goal ret = null;
        
        Project currentProject = (Project) session.getAttribute("currentProject");
        User currentUser = userManager.getUserByUsername(request.getRemoteUser());

        List<MGOGRelationship> retRelations = new ArrayList<MGOGRelationship>();
        
        if (!StringUtils.isBlank(id)) {
        	ret = goalManager.get(new Long(id));
        	retRelations = ret.getMGOGRelations();
        }else {
        	ret = new Goal();
        	ret.setStatus(GoalStatus.DRAFT);
        	ret.setGoalOwner(currentUser);
        	ret.setProject(projectManager.get(currentProject.getId()));
        }              
        List<String> availableStatus = goalManager.getAvailableStatus(ret,currentUser);
        
		// header data is modificable only if te current user is the GO and the
		// status is PROPOSED or we are making a new Goal...
		boolean modificableHeader = ( (ret.getStatus() == GoalStatus.DRAFT || ret.getStatus() == GoalStatus.FOR_REVIEW) &&  
									 currentUser.equals(ret.getGoalOwner()));
		
		// the detail section is visible only if the status is not DRAFT and PROPOSED
		boolean visibleGESection = !(ret.getStatus() == GoalStatus.DRAFT || 
					ret.getStatus() == GoalStatus.PROPOSED);
		
		List<Goal> allGoals = goalManager.findByProject(currentProject); //tutti i goal nel progetto
		List<Strategy> allStrategies = strategyManager.findByProject(currentProject);

		List<Goal> goalParent = new ArrayList<Goal>(); //tutti i padri Goal ammissibili
		List<Strategy> strategyParent = new ArrayList<Strategy>();
		List<Goal> goalChildren = new ArrayList<Goal>(); //tutti i figli Goal ammissibili
		List<Strategy> strategyChildren = new ArrayList<Strategy>();
		
		getGoalParentAndChildren(allGoals, ret, goalParent, goalChildren);
		getStrategyParentAndChildren(allStrategies, ret, strategyParent, strategyChildren);

		List<Goal> associableMGoals = new ArrayList<Goal>(); //mg associabili ad og
		List<Goal> associableOGoals = new ArrayList<Goal>(); //og associabili ad mg

		List<Goal> oGoalsAll = new ArrayList<Goal>(); //tutti gli og nel progetto
		List<Goal> mGoalsAll = new ArrayList<Goal>(); //tutti gli mg nel progetto
		
		for(Goal g: allGoals) {
			if(GoalType.isMG(g))
				mGoalsAll.add(g);
			else if(GoalType.isOG(g))
				oGoalsAll.add(g);
		}
		
		associableOGoals.addAll(oGoalsAll); //popolo og associabili ad mg, ossia tutti
			
		for(Goal g: mGoalsAll) { //popolo mg associabili a og, ossia quelli senza relazione o in relazione con l'og corrente
			List<MGOGRelationship> gRelations = g.getMGOGRelations(); 
			if(gRelations.size() == 0 || retRelations.contains(gRelations.get(0))) //essendo un mg, ha al massimo una relazione
				associableMGoals.add(g);
		}
				
		model.addAttribute("currentUser",currentUser);
		model.addAttribute("visibleGESection",visibleGESection);
		model.addAttribute("modificableHeader",modificableHeader);
        model.addAttribute("availableStatus",availableStatus);
        model.addAttribute("availableGoals",allGoals);
        model.addAttribute("associableMGoals", associableMGoals);
        model.addAttribute("associableOGoals", associableOGoals);
        model.addAttribute("availableUsers",ret.getProject().getGQMTeam());
        model.addAttribute("goalParent", goalParent);
        model.addAttribute("strategyParent", strategyParent);
        model.addAttribute("goalChildren", goalChildren);
        model.addAttribute("strategyChildren", strategyChildren);
        
        return ret;
    }
    
    /**
     * Recupera le liste di Goal ammissibili come parenti o figli
     * @param oGoalsAll La lista di Goal in cui cercare
     * @param current Il Goal di cui recuperare possibili parenti e figli
     * @param goalParent La lista da popolare con i parenti Goal ammissibili
     * @param goalChildren La lista da popolare con i figli Goal ammissibili
     */
    private void getGoalParentAndChildren(List<Goal> oGoalsAll, Goal current, List<Goal> goalParent, List<Goal> goalChildren) {
    	boolean isNew = (current.getId() == null);
    	
    	if(isNew) { //se nuovo, current non ha figli
			for(Goal g : oGoalsAll) {
				if(!g.hasChildren() || g.areChildrenGoal()) //goal g senza figli o con figli goal
					goalParent.add(g);
			}
    	} else {
			for(Goal g : oGoalsAll) {
				if((!g.hasChildren() || g.areChildrenGoal()) && !gridManager.isGrandChild(g, current)) //goal g senza figli o con figli goal, e g non è nipote di current
					goalParent.add(g);
				else if(current.areChildrenGoal() && current.getOrgChild().contains(g)) //il goal g è figlio del goal current
					goalChildren.add(g);
			}
    	}
    }
    
    /**
     * Recupera le liste di Strategy ammissibili come parenti o figli
     * @param allStrategies La lista di Strategy in cui cercare
     * @param current Il Goal di cui recuperare possibili parenti e figli
     * @param strategyParent La lista da popolare con i parenti Strategy ammissibili
     * @param strategyChildren La lista da popolare con i figli Strategy ammissibili
     */
    private void getStrategyParentAndChildren(List<Strategy> allStrategies, Goal current, List<Strategy> strategyParent, List<Strategy> strategyChildren) {
    	boolean isNew = (current.getId() == null);
    	
    	if(isNew) { //se nuovo, current non ha figli
			for(Strategy s: allStrategies) {
				if(!s.hasChildren() || s.areChildrenGoal()) //strategy s senza figli o con figli goal
					strategyParent.add(s);
			}
    	} else {
			for(Strategy s: allStrategies) {
				if((!s.hasChildren() || s.areChildrenGoal()) && !gridManager.isGrandChild(s, current)) //strategy s senza figli o con figli goal, e s non è nipote di current
					strategyParent.add(s);
				else if(current.areChildrenStrategy() && current.getOstrategyChild().contains(s)) //la strategy s è figlia del goal current
					strategyChildren.add(s);
			}
    	}
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(@Valid Goal goal, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
    	
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }
 
        if (validator != null) { // validator is null during testing
            validator.validate(goal, errors);
            
            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
            	System.out.println("errors: " + errors);
                return "goalform";
            }
        }
 
        log.debug("entering 'onSubmit' method...");
 
        boolean isNew = (goal.getId() == null);
        	Locale locale = request.getLocale();
 
        if (request.getParameter("delete") != null) {
        	Goal gDB = goalManager.get(goal.getId());
        	
        	if(GoalType.isOG(gDB)){
        		
        		if(gDB.hasChildren()) {
        			//TODO Attenzione!!! potrebbe non funzionare
            		errors.rejectValue("childType", "childType", "Can't delete OG with children"); 
            		return "goalform";
            	} else {
            		
            		//TODO si potrebbe mettere dentro una funzione
            		//Prendo il padre, accedo al campo figli tipo goal, cerco il figlio e lo elimino
    	        	if(gDB.getParentType() != -1) {
    	        		if (gDB.getParentType() == 0) {
    						
    	        			Goal oParent = gDB.getOrgParent(); 	//recupero il padre
    						oParent.getOrgChild().remove(gDB); 	//aggiorno il padre
    						goalManager.save(oParent); 			//salvo il padre
    						gDB.setOrgParent(null); 			//aggiorno il goal
    					
    	        		}else {
    						Strategy sParent = gDB.getOstrategyParent();
    						sParent.getSorgChild().remove(gDB);
    						strategyManager.save(sParent);
    						gDB.setOstrategyParent(null);
    					}
    	        		goalManager.save(gDB); 					//salvo il goal
    	        	}
            	}
        	}
   	
        	deleteGoal(gDB);
        	
            saveMessage(request, getText("goal.deleted", locale));
            
        } else { //Caso di aggiornamento o creazione di un Goal
        	
        	//###########################################################
        	
        	
        	if(GoalType.isOG(goal)){
        		
        		goal.getOrgChild().remove(null);
            	goal.getOstrategyChild().remove(null);
            	//goal.setOrgParent(null);
            	//goal.setOstrategyParent(null);
            	
        		if(isNew){ //creazione
        			
        			if (goal.hasParent()) { //ha padre
        				if (goal.getParentType() == 0) { //ha padre Goal
							
        					Goal oParent = goalManager.get(goal.getOrgParent().getId());
        					if(oParent.getChildType() == 0 || oParent.getChildType() == -1) {
        						oParent.getOrgChild().add(goal);
            					goal.setOrgParent(oParent);
            					goalManager.save(oParent);
        					} else {
        						errors.rejectValue("parentType", "parentType", "Can't add Goal child to parent already having Strategy children");
        						return "goalform";
        					}
        					
						} else { //ha padre Strategy
							
							Strategy sParent = strategyManager.get(goal.getOstrategyParent().getId());
							if(sParent.getChildType() == 0 || sParent.getChildType() == -1) {
								sParent.getSorgChild().add(goal);
								goal.setOstrategyParent(sParent);
								strategyManager.save(sParent);
	    					} else {
	    						errors.rejectValue("parentType", "parentType", "Can't add Strategy child to parent already having Goal children");
	    						return "goalform";
	    					}
						}
        				
					} else {
						
						//else controllare che sia il primo ROOT, altrimenti errore
						if(goalManager.rootExists(goal.getProject())){
							errors.rejectValue("parentType", "parentType", "OG Root exists!!!"); 
		            		return "goalform";
						}
					}
        			
        			if(goal.hasChildren()) { //ha figli
       	        		if (goal.getChildType() == 0) { //ha figli Goal
        						
       	        			for (Goal g : goal.getOrgChild()) {
       	        				g.setOrgParent(goal);		//imposto il padre
       	        				goal.getOrgChild().add(g);	//aggiungo il figlio a 'goal'
       	        				goalManager.save(g);		//salvo il figlio
    						}
        					
        	        	}else {	//ha figli Strategy
      
        	        		for (Strategy s : goal.getOstrategyChild()) {
        	        			s.setSorgParent(goal);
        	        			goal.getOstrategyChild().add(s);
        	        			strategyManager.save(s);
    						}
        	        	}
            			
                	}//else non devo fare niente
        			
        			//goal = goalManager.save(goal);
        			
            	} else { //aggiornamento
            		
            		
            		Goal gDB = goalManager.get(goal.getId());
            		
            		boolean pSameType = (gDB.getParentType() == goal.getParentType()) ? true : false;
            		int parentType = goal.getParentType();
            		
            		//stesso tipo padre
            		if (pSameType) { 

            			//stesso padre Goal
            			if (parentType == 0) { 
            				
            				boolean pOSame = (gDB.getOrgParent().getId() == goal.getOrgParent().getId()) ? true : false;
                    		//è cambiato il padre Goal
            				if(!pOSame){
            					/**
            					 * tolgo il figlio al vecchio padre
            					 * ho cambiato il padre
            					 * aggiungo il figlio al nuovo padre
            					 */
            					gDB.getOrgParent().getOrgChild().remove(goal);
            					goalManager.save(gDB.getOrgParent());
            					
            					gDB.setOrgParent(null);
            					goalManager.save(gDB);
            					
            					goal.getOrgParent().getOrgChild().add(goal);
            				}
            				
            			//stesso padre Strategy
						} else if(parentType == 1){
							
							boolean pSSame = (gDB.getOstrategyParent().getId() == goal.getOstrategyParent().getId()) ? true : false;
							//è cambiato il padre Strategy
							if(!pSSame) {
								gDB.getOstrategyParent().getSorgChild().remove(goal);
								strategyManager.save(gDB.getOstrategyParent());
								
								gDB.setOstrategyParent(null);
								goalManager.save(gDB);
								
								goal.getOstrategyParent().getSorgChild().add(goal);
							}
						
						}// else non è cambiato niente, ma è rimasto null
            			
            			//goal = goalManager.save(goal);
						
					} else { //non stesso tipo padre, che tipo è?
						
						//se il nuovo padre è null, quindi non ho più il padre
						if (parentType == -1) { //il nuovo parent è null
							
							//se il vecchio padre era un Goal
							if (gDB.getParentType() == 0 ) { 
								
								/*
								 * mi tolgo dalla lista dei figli del mio vecchio padre
								 */
								gDB.getOrgParent().getOrgChild().remove(goal);
								goalManager.save(gDB.getOrgParent());
								
								gDB.setOrgParent(null);
            					goalManager.save(gDB);
					
            				//Se il vecchio padre era una Strategy
							} else if(gDB.getParentType() == 1){
								
								gDB.getOstrategyParent().getSorgChild().remove(goal);
								strategyManager.save(gDB.getOstrategyParent());
								
								gDB.setOstrategyParent(null);
								goalManager.save(gDB);
							}

						} else { //il nuovo parent è Goal/Strategy ed il vecchio è Strategy/Goal
							
							//se il nuovo padre è un Goal e il vecchio padre era una Strategy
							if (goal.getParentType() == 0) {
								
								gDB.getOstrategyParent().getSorgChild().remove(goal);
								strategyManager.save(gDB.getOstrategyParent());
								
								gDB.setOstrategyParent(null);
								goalManager.save(gDB);
								
								goal.getOrgParent().getOrgChild().add(goal);
								
							//se il nuovo padre è una Strategy e il vecchio padre era un Goal
							} else {
								
								gDB.getOrgParent().getOrgChild().remove(goal);
								goalManager.save(gDB.getOrgParent());
								
								gDB.setOrgParent(null);
								goalManager.save(gDB);
								
								goal.getOstrategyParent().getSorgChild().add(goal);
								
							}
						}
						
						//goal = goalManager.save(goal);
					}
            		
            		boolean cSameType = (gDB.getChildType() == goal.getChildType()) ? true : false;
            		
            		//se stesso tipo figlio
            		if (cSameType) { 
            			
            			//se stesso tipo Goal
            			if(goal.getChildType() == 0){
	            			
            				boolean cOSame = (gDB.getOrgChild() == goal.getOrgChild()) ? true : false;
            				//se non stesso figlio Goal
	            			if (!cOSame) {
	            				if(!goal.getOrgChild().containsAll(gDB.getOrgChild())){
	            					errors.rejectValue("childType", "childType", "Can't remove children from here!"); 
	    		            		return "goalform";
	            				}
							} 
	            			
	            		//se stesso tipo Strategy
            			} else if(goal.getChildType() == 1) {
            				
            				boolean cSSame = (gDB.getOstrategyChild() == goal.getOstrategyChild()) ? true : false;
            				//se non stesso figlio Stretegy
							if (!cSSame) {
								if(!goal.getOstrategyChild().containsAll(gDB.getOstrategyChild())){
	            					errors.rejectValue("childType", "childType", "Can't remove children from here!"); 
	    		            		return "goalform";
	            				}
							} 
							
            			} //else non è cambiato niente, ma è rimasto null
            			
            			//goal = goalManager.save(goal);
            			
					} else { //non stesso tipo figlio, male male!!!
						
						errors.rejectValue("childType", "childType", "Can't change children type!"); 
	            		return "goalform";
						/*
						//se il nuovo figlio è null, quindi non ho più figli
						if (goal.getChildType() == -1) { //il nuovo figlio è null
							
							//se il vecchio figlio era un Goal
							if (gDB.getChildType() == 0 ) { 
								
								//elimino tutti i figli Goal
								gDB.getOrgChild().clear();
								goalManager.save(gDB);
								
							//se il vecchio figlio era una Strategy
							} else if(gDB.getChildType() == 1) { 
								
								//elimino tutti i figli Strategy
								gDB.getOstrategyChild().clear();
								goalManager.save(gDB);
							}
							
						} else { //il nuovo figlio è Goal/Strategy ed il vecchio era Strategy/Goal
							
							//se il nuovo figlio è un Goal e il vecchio figlio era una Strategy
							if (goal.getChildType() == 0) {
								
								gDB.getOrgChild().clear();
								goalManager.save(gDB);
							
							//se il nuovo figlio è una Strategy e il vecchio figlio era un Goal
							} else {
							
								gDB.getOstrategyChild().clear();
								goalManager.save(gDB);
							}
						}
						
						goal = goalManager.save(goal);
						*/
					} 
            	}
        		
        	}
        	
        	//###########################################################
        	
        	goal.setGoalOwner(userManager.get(goal.getGoalOwner().getId()));
        	goal.setGoalEnactor(userManager.get(goal.getGoalEnactor().getId()));
        	
        	if("true".equalsIgnoreCase(request.getParameter("vote"))) {
        		goal.getVotes().add(userManager.getUserByUsername(request.getRemoteUser()));
        	}
            
        	List<MGOGRelationship> oldMGOGRelations = new ArrayList<MGOGRelationship>();
        	List<MGOGRelationship> newMGOGRelations = new ArrayList<MGOGRelationship>();
        	
        	boolean sameMGOGRelation = getNewAndOldMGOGRelationship(goal, oldMGOGRelations, newMGOGRelations);
        	
        	if(!sameMGOGRelation)
        		goal = deleteOldAddNewRelationship(goal, oldMGOGRelations, newMGOGRelations);
        	
        	//altro codice legato di confronto relazioni
        	
        	goal = goalManager.save(goal);
        	
        	//salva altre relazioni
        	
        	if(!sameMGOGRelation)
        		saveNewRelationship(newMGOGRelations);
        	
            String key = (isNew) ? "goal.added" : "goal.updated";
            saveMessage(request, getText(key, locale));
        	
            if(goal.getId() == null){
		        try {
		        	User ge =  userManager.getUserByUsername(goal.getGoalEnactor().getFullName());
		        	message.setSubject(getText("goal.email.subject", locale));
		            sendUserMessage(ge, getText("goal.email.message", locale), RequestUtil.getAppURL(request));		            
		        } catch (MailException me) {
		            saveError(request, me.getMostSpecificCause().getMessage());
		        }
            }else {
            	Project cp = projectManager.get(goal.getProject().getId());
            	List<User> users = new ArrayList<User>();
            	for(User u:cp.getGQMTeam())
            		users.add(u);
            	for(User u:cp.getProjectTeam())
            		users.add(u);            	
            	for(User u:users){
    		        try {
    		        	User user =  userManager.getUserByUsername(u.getFullName());
    		        	message.setSubject(getText("goal.changed.email.subject", locale));
    		            sendUserMessage(user, getText("goal.changed.email.message", locale), RequestUtil.getAppURL(request));		            
    		        } catch (MailException me) {
    		            saveError(request, me.getMostSpecificCause().getMessage());
    		        }            		
            	}
            }
            
            /*if (!isNew) {
                success = "redirect:goalform?id=" + goal.getId();
            }*/
        }
        
        return getSuccessView();
    }

    /**
     * Elimina un Goal e tutte le sue relazioni
     * @param g Il Goal da eliminare
     */
    private void deleteGoal(Goal g) {    	        	
    	mgogRelationshipManager.removeRelations(g);
    	//rimozione di altre relazioni
    	goalManager.remove(g); //elimino goal da tabella Goal 
    }
    
    /**
     * Restituisce le relazioni di tipo MGOG vecchie e nuove del goal passato come parametro
     * @param g Il Goal di cui recuperare le relazioni MGOG
     * @param oldMGOGRelations Una lista a cui aggiungere le vecchie relazioni MGOG
     * @param newMGOGRelations Una lista a cui aggiungere le nuove relazioni MGOG
     * @return true in caso le relazioni non siano state modificate, false altrimenti
     */
    private boolean getNewAndOldMGOGRelationship(Goal g, List<MGOGRelationship> oldMGOGRelations, List<MGOGRelationship> newMGOGRelations) {
    	boolean isNew = (g.getId() == null);
    	
    	List<MGOGRelationship> oldRelations = !isNew ? mgogRelationshipManager.getAssociatedRelations(g) : new ArrayList<MGOGRelationship>(); //vecchie relazioni
        List<MGOGRelationship> newRelations = g.getMGOGRelations(); //nuove relazioni
        newRelations.remove(null); //se l'utente ha selezionato "None" (da modificare nel javascript)
            
        //In initBinder3.setValue ho impostato solo un goal della relazione, devo impostare l'altro goal (ossia g)
        //Devo eseguire questa operazione prima di confrontare oldRelations e newRelations
        for(MGOGRelationship newRel : newRelations) {
        	if(GoalType.isOG(g))
        		newRel.getPk().setOg(g);
        	else if(GoalType.isMG(g))
        		newRel.getPk().setMg(g);
        }
        
        oldMGOGRelations.addAll(oldRelations);
        newMGOGRelations.addAll(newRelations);
        
        return oldMGOGRelations.equals(newMGOGRelations);
    }
    
    /**
     * Modifica l'oggetto Goal passato come parametro, eliminando le vecchie relazioni MGOG ed aggiungendo le nuove.
     * Per confermare le modifiche del goal, è successivamente necessario salvarlo
     * @param g Il Goal da modificare
     * @param oldMGOGRelations La lista delle vecchie relazioni MGOG
     * @param newMGOGRelations La lista delle nuove relazioni MGOG
     * @return Il Goal modificato
     */
    public Goal deleteOldAddNewRelationship(Goal g, List<MGOGRelationship> oldMGOGRelations, List<MGOGRelationship> newMGOGRelations) {
        //g.setRelationsWithMG(new HashSet<MGOGRelationship>());
    	g.getRelationsWithMG().clear();
        g.setRelationWithOG(null);
        
        if(g.getId() == null) //se il goal è nuovo, non ha un id, quindi quando salvo le nuove relazioni, lancia un'eccezione
        	g = goalManager.save(g);
        
        for(MGOGRelationship oldRel : oldMGOGRelations)
        	mgogRelationshipManager.remove(oldRel);
        
        //In initBinder3.setValue ho impostato solo un goal della relazione, devo impostare l'altro goal
        for(MGOGRelationship newRel : newMGOGRelations) {
        	if(GoalType.isOG(g)) {
        		newRel.getPk().setOg(g);
        		g.getRelationsWithMG().add(newRel);
        	}
        	else if(GoalType.isMG(g)) {
        		newRel.getPk().setMg(g);
        		g.setRelationWithOG(newRel);
        	}
        }
        
        return g;
    }
    
    /**
     * Salva le nuove relazioni MGOG nella tabella MGOGRelationship
     * @param newMGOGRelations La lista delle nuove relazioni MGOG
     */
    public void saveNewRelationship(List<MGOGRelationship> newMGOGRelations) {
        for(MGOGRelationship newRel : newMGOGRelations)
        	mgogRelationshipManager.save(newRel); //salvo nuove relazioni in tabella relazioni	
    }

    /*
     * InitBinders
     */
    
    @InitBinder(value="goal")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new GoalValidator());
    }
    
    @InitBinder
    protected void initBinder1(HttpServletRequest request, ServletRequestDataBinder binder) {
        binder.registerCustomEditor(Set.class, "QSMembers", new CustomCollectionEditor(Set.class) {
            protected Object convertElement(Object element) {
                if (element != null) {
                    Long id = new Long((String)element);
                    User u = userManager.get(id);
                    return u;
                }
                return null;
            }
        });
    }
    
    @InitBinder
    protected void initBinder2(HttpServletRequest request, ServletRequestDataBinder binder) {
        binder.registerCustomEditor(Set.class, "MMDMMembers", new CustomCollectionEditor(Set.class) {
            protected Object convertElement(Object element) {
                if (element != null) {
                    Long id = new Long((String)element);
                    User u = userManager.get(id);
                    return u;
                }
                return null;
            }
        });
    }
    
    @InitBinder
    protected void initBinder3(HttpServletRequest request, ServletRequestDataBinder binder) {
    	binder.registerCustomEditor(Set.class, "orgChild", new CustomCollectionEditor(Set.class) {
            protected Object convertElement(Object element) {
                if (element != null) {
                    Long id = new Long((String)element);
                    if(id != -1){
                    	Goal g = goalManager.get(id);
                    	return g;
                    }
                }
                return null;
            }
        });
    }
    
    @InitBinder
    protected void initBinder4(HttpServletRequest request, ServletRequestDataBinder binder) {
    	binder.registerCustomEditor(Set.class, "ostrategyChild", new CustomCollectionEditor(Set.class) {
            protected Object convertElement(Object element) {
                if (element != null ) {
                    Long id = new Long((String)element);
                    if(id != -1){
                    	Strategy s = strategyManager.get(id);
                    	return s;
                    }
                }
                return null;
            }
        });
    }
    
    @InitBinder
    protected void initBinder5(HttpServletRequest request, ServletRequestDataBinder binder) {
    	binder.registerCustomEditor(Goal.class, "orgParent", new OrgParentEditorSupport());
    	binder.registerCustomEditor(Strategy.class, "ostrategyParent", new OstrategyParentEditorSupport());
    }

    private class OrgParentEditorSupport extends PropertyEditorSupport {
		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			if(text != null) {			
				Long id = new Long(text);

				if(id != -1) {
					Goal g = goalManager.get(id);
					setValue(g);	
				} else {
					setValue(null);
				}
			}	
		}
    }
    
    private class OstrategyParentEditorSupport extends PropertyEditorSupport {
		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			if(text != null) {			
				Long id = new Long(text);

				if(id != -1) {
					Strategy s = strategyManager.get(id);
					setValue(s);	
				} else {
					setValue(null);
				}
			}	
		}
    }
    
    @InitBinder
    protected void initBinder6(HttpServletRequest request, ServletRequestDataBinder binder) {    	
    	binder.registerCustomEditor(Set.class, "relationsWithMG", new AssociatedMGCollectionEditor(Set.class));
    	binder.registerCustomEditor(MGOGRelationship.class, "relationWithOG", new AssociatedOGEditorSupport());
    }

    private class AssociatedOGEditorSupport extends PropertyEditorSupport {
		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			if(text != null) {			
				Long id = new Long(text);

				if(id != -1) {
					MGOGRelationship rel = new MGOGRelationship();
					MGOGRelationshipPK pk = new MGOGRelationshipPK();
					pk.setOg(goalManager.get(id)); //il goal mg lo setto in onSubmit
					rel.setPk(pk);
					setValue(rel);	
				} else {
					setValue(null);
				}
			}	
		}
    }
    
    private class AssociatedMGCollectionEditor extends CustomCollectionEditor {
    	private AssociatedMGCollectionEditor(Class collectionType) {
    		super(collectionType);
    	}
    	
    	protected Object convertElement(Object element) {
    		if (element != null && StringUtils.isNotBlank((String)element)) {
	    		Long id = new Long((String)element);
	    		
	    		if(id != -1) {
		    		Goal mg = goalManager.get(id);
		    		
		    		MGOGRelationship rel = new MGOGRelationship();
		    		MGOGRelationshipPK pk = new MGOGRelationshipPK();
		    		pk.setMg(mg);
		    		rel.setPk(pk);
		    		
		    		return rel;	
	    		}
    		}
    		return null;
    	}
    }
}
