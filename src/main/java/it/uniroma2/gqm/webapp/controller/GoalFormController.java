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
		
		List<Goal> allGoals = goalManager.getAll();
		List<Goal> associableMGoals = new ArrayList<Goal>(); //mg associabili ad og
		List<Goal> associableOGoals = new ArrayList<Goal>(); //og associabili ad mg
		List<Goal> oGoalsAll = new ArrayList<Goal>();
		List<Goal> mGoalsAll = new ArrayList<Goal>();
		
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
        model.addAttribute("oGoalsAll", oGoalsAll);
        model.addAttribute("strategies",strategyManager.findByProject(ret.getProject()));        
        model.addAttribute("availableUsers",ret.getProject().getGQMTeam());
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
        	List<MGOGRelationship> rels = gDB.getMGOGRelations();
        	        	
        	for(MGOGRelationship rel : rels) {
            	mgogRelationshipManager.remove(rel); //elimino relazione da tabella relazioni
        	}
        	
        	goalManager.remove(gDB); //elimino goal     	
            saveMessage(request, getText("goal.deleted", locale));
        } else {
        	
        	goal.setGoalOwner(userManager.get(goal.getGoalOwner().getId()));
        	goal.setGoalEnactor(userManager.get(goal.getGoalEnactor().getId()));
        	if(goal.getStrategy().getId() != null)
        		goal.setStrategy(strategyManager.get(goal.getStrategy().getId()));
        	else 
        		goal.setStrategy(null);
        	
        	if(goal.getParent().getId() != null)
        		goal.setParent(goalManager.get(goal.getParent().getId()));
        	else 
        		goal.setParent(null);
        	
        	if("true".equalsIgnoreCase(request.getParameter("vote"))){
        		goal.getVotes().add(userManager.getUserByUsername(request.getRemoteUser()));
        	}
                    	
        	List<MGOGRelationship> oldRelations = !isNew ? mgogRelationshipManager.getAssociatedRelations(goal) : new ArrayList<MGOGRelationship>(); //vecchie relazioni
            List<MGOGRelationship> newRelations = goal.getMGOGRelations(); //nuove relazioni
            newRelations.remove(null); //se l'utente ha selezionato "None" (da modificare nel javascript)
            
            boolean sameRelation = oldRelations.equals(newRelations);
                        
            if(!sameRelation) { //evito di modificare le relazioni, se le nuove e la vecchie coincidono
                goal.setRelationsWithMG(new HashSet<MGOGRelationship>());
                goal.setRelationWithOG(null);
                goal = goalManager.save(goal); //cancello vecchia relazione da goal
                
                for(MGOGRelationship oldRel : oldRelations)
                	mgogRelationshipManager.remove(oldRel); //cancello vecchie relazioni da tabella relazioni

                //In initBinder3.setValue ho impostato solo un goal della relazione, devo impostare l'altro goal.
                //Riassocio le relazioni al goal
                for(MGOGRelationship newRel : newRelations) {
                	if(GoalType.isOG(goal)) {
                		newRel.getPk().setOg(goal);
                		goal.getRelationsWithMG().add(newRel);
                	} else if(GoalType.isMG(goal)) {
                		newRel.getPk().setMg(goal);
                		goal.setRelationWithOG(newRel);
                	}
                	mgogRelationshipManager.save(newRel); //salvo nuove relazioni in tabella relazioni
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
    
    @InitBinder(value="goal")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new GoalValidator());
    }
    
    @InitBinder
    protected void initBinder3(HttpServletRequest request, ServletRequestDataBinder binder) {    	
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
