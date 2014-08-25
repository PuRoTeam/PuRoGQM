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
import java.util.List;
import java.util.Locale;
import java.util.Set;

import it.uniroma2.gqm.model.*;
import it.uniroma2.gqm.service.GoalManager;
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
    
    @Autowired
	public void setMgogRelationshipManager(@Qualifier("mgogRelationshipManager") MGOGRelationshipManager  mgogRelationshipManager) {
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

        MGOGRelationship retRelation = null;        
        
        if (!StringUtils.isBlank(id)) {
        	ret = goalManager.get(new Long(id));
        	retRelation = ret.getMGOGRelation();
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
		
		List<Goal> allGoals = goalManager.findByProject(currentProject); //goalManager.getAll();
		List<Goal> mGoals = new ArrayList<Goal>(); //elenco goal MG non ancora associati ad alcun og
		List<Goal> oGoals = new ArrayList<Goal>();
		List<Goal> oGoalsAll = new ArrayList<Goal>();
		List<Strategy> allStragies = strategyManager.getAll(); //TODO cambiare in Strategy 
				
		for(Goal g: allGoals) {
			/*MGOGRelationship rel = mgogRelationshipManager.getAssociatedRelation(g);
			*/
			MGOGRelationship rel = g.getMGOGRelation();				
			
			//mostro solo i goal non associati, più il goal già associato con quello correntemente visualizzato
			if(rel == null || rel.equals(retRelation)) {
				if(GoalType.isMG(g))
					mGoals.add(g);
				else if(GoalType.isOG(g))
					oGoals.add(g);
			}

			if(GoalType.isOG(g))
				oGoalsAll.add(g);
		}
		
		model.addAttribute("currentUser",currentUser);
		model.addAttribute("visibleGESection",visibleGESection);
		model.addAttribute("modificableHeader",modificableHeader);
        model.addAttribute("availableStatus",availableStatus);
        model.addAttribute("availableGoals",allGoals);
        model.addAttribute("mGoals", mGoals);
        model.addAttribute("oGoals", oGoals);
        model.addAttribute("oGoalsAll", oGoalsAll);
        model.addAttribute("strategies",strategyManager.findByProject(ret.getProject()));        
        model.addAttribute("availableUsers",ret.getProject().getGQMTeam());
        model.addAttribute("strategies", allStragies);
        
        return ret;
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
        	MGOGRelationship rel = gDB.getMGOGRelation();
        	
        	if(GoalType.isOG(gDB)){
        		
        		if(goalManager.hasChildren(gDB)) {
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
        	
        	if(rel != null) {
        		gDB.setRelationWithMG(null);
            	gDB.setRelationWithOG(null);
            	goalManager.save(gDB); //cancello relazione da goal
            	mgogRelationshipManager.remove(rel); //elimino relazione da tabella relazioni
        	}
        	
        	goalManager.remove(gDB); //elimino goal     	
            saveMessage(request, getText("goal.deleted", locale));
            
        } else { //Caso di aggiornamento o creazione di un Goal
        	
        	//###########################################################
        	
        	
        	if(GoalType.isOG(goal)){
        		
        		goal.getOrgChild().remove(null);
            	goal.getOstrategyChild().remove(null);
            	//goal.setOrgParent(null);
            	//goal.setOstrategyParent(null);
            	
        		if(isNew){ //creazione
        			
        			if (goalManager.hasParent(goal)) { //ha padre
        				if (goal.getParentType() == 0) { //ha padre Goal
							
        					Goal oParent = goalManager.get(goal.getOrgParent().getId());
        					oParent.getOrgChild().add(goal);
        					goal.setOrgParent(oParent);
        					goalManager.save(oParent);
        					
						} else { //ha padre Strategy
							
							Strategy sParent = strategyManager.get(goal.getOstrategyParent().getId());
							sParent.getSorgChild().add(goal);
							goal.setOstrategyParent(sParent);
							strategyManager.save(sParent);
						}
        				
					} else {
						
						//else controllare che sia il primo ROOT, altrimenti errore
						if(goalManager.rootExists(goal.getProject())){
							errors.rejectValue("parentType", "parentType", "OG Root exists!!!"); 
		            		return "goalform";
						}
					}
        			
        			if(goalManager.hasChildren(goal)) { //ha figli
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
        	
        	if("true".equalsIgnoreCase(request.getParameter("vote"))){
        		goal.getVotes().add(userManager.getUserByUsername(request.getRemoteUser()));
        	}
            
        	MGOGRelationship oldRelation = !isNew ? mgogRelationshipManager.getAssociatedRelation(goal) : null; //vecchia relazione
        	MGOGRelationship newRelation = goal.getMGOGRelation(); //nuova relazione
            
            //In initBinder3.setValue ho impostato solo un goal della relazione, devo impostare l'altro goal
            if(newRelation != null) {
            	if(GoalType.isMG(goal))
            		newRelation.getPk().setMg(goal);
            	else if(GoalType.isOG(goal))
            		newRelation.getPk().setOg(goal);	
            }
            
            boolean sameRelation = (!(oldRelation == null || !oldRelation.equals(newRelation)));
                        
            if(!sameRelation) { //evito di modificare la relazione, se la nuova e la vecchia coincidono
                goal.setRelationWithMG(null);
                goal.setRelationWithOG(null);
                goal = goalManager.save(goal); //cancello vecchia relazione da goal
                
                if(oldRelation != null)
                	mgogRelationshipManager.remove(oldRelation); //cancello vecchia relazione da tabella relazioni
                
                if(newRelation != null) {
                	mgogRelationshipManager.save(newRelation); //salvo nuova relazione in tabella relazioni
                	
                	if(GoalType.isMG(goal))
                		goal.setRelationWithOG(newRelation);
                	else if(GoalType.isOG(goal))
                		goal.setRelationWithMG(newRelation);
                }       
            }
                 	
            goal = goalManager.save(goal); //aggiungo nuova relazione
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

    /*
     * InitBinders
     */
    
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
    	binder.registerCustomEditor(MGOGRelationship.class, "relationWithMG", new AssociatedMGEditorSupport());
    	binder.registerCustomEditor(MGOGRelationship.class, "relationWithOG", new AssociatedOGEditorSupport());
    }
    
    @InitBinder
    protected void initBinder4(HttpServletRequest request, ServletRequestDataBinder binder) {
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
    protected void initBinder5(HttpServletRequest request, ServletRequestDataBinder binder) {
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
    protected void initBinder6(HttpServletRequest request, ServletRequestDataBinder binder) {
    	binder.registerCustomEditor(Goal.class, "orgParent", new OrgParentEditorSupport());
    	binder.registerCustomEditor(Strategy.class, "ostrategyParent", new OstrategyParentEditorSupport());
    }
    
    
    @InitBinder(value="goal")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new GoalValidator());
    }
    
    /*
     * Editor support classes
     */
    private class AssociatedOGEditorSupport extends PropertyEditorSupport {
		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			if(text != null) {			
				Long id = new Long(text);

				if(id != -1) {
					MGOGRelationship rel = new MGOGRelationship();
					MGOGRelationshipPK pk = new MGOGRelationshipPK();
					pk.setOg(goalManager.get(id));
					rel.setPk(pk);
					setValue(rel);	
				} else {
					setValue(null);
				}
			}	
		}
    }
    
    private class AssociatedMGEditorSupport extends PropertyEditorSupport {
		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			if(text != null) {
				Long id = new Long(text);

				if(id != -1) {
					MGOGRelationship rel = new MGOGRelationship();
					MGOGRelationshipPK pk = new MGOGRelationshipPK();
					pk.setMg(goalManager.get(id));
					rel.setPk(pk);
					setValue(rel);	
				} else {
					setValue(null);
				}
			}	
		}
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
    
    /*
    private class OrgChildEditorSupport extends PropertyEditorSupport {
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
    
    private class OstrategyChildEditorSupport extends PropertyEditorSupport {
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
    */
}
