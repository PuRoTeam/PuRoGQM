package it.uniroma2.gqm.webapp.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.uniroma2.gqm.model.Project;
import it.uniroma2.gqm.model.Strategy;
import it.uniroma2.gqm.service.StrategyManager;

import org.apache.commons.lang.StringUtils;
import org.appfuse.model.User;
import org.appfuse.service.GenericManager;
import org.appfuse.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/strategyform*")
@SessionAttributes({"currentProject","strategy","currentUser"})
public class StrategyFromController  extends BaseFormController {
	
	@Autowired
	private StrategyManager strategyManager;
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
        model.addAttribute("currentUser",currentUser);
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
        	strategyManager.remove(strategy.getId());
            saveMessage(request, getText("strategy.deleted", locale));
        } else {
        	strategyManager.save(strategy);
            String key = (isNew) ? "strategy.added" : "strategy.updated";
            saveMessage(request, getText(key, locale));
            
            if (!isNew) {
            	success = "redirect:strategyform?id=" + strategy.getId();
        	}
        } 
        return getSuccessView();
    }
    
    
}
