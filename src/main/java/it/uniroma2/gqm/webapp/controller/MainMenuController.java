package it.uniroma2.gqm.webapp.controller;

import it.uniroma2.gqm.model.Metric;
import it.uniroma2.gqm.model.Project;
import it.uniroma2.gqm.service.MetricManager;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.appfuse.service.GenericManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;



@Controller
@RequestMapping("/mainMenu*")
public class MainMenuController {

    private GenericManager<Project, Long> projectManager = null;
    
    @Autowired
    public void setProjectManager(@Qualifier("projectManager") GenericManager<Project, Long> projectManager) {
        this.projectManager = projectManager;
    }
    
    @ModelAttribute
	@RequestMapping(method = RequestMethod.GET)
	public Model handleRequest(Model model,HttpSession session) throws Exception {
        // set the default if necessary...
        Project currentProject =(Project) session.getAttribute("currentProject");
        if(currentProject == null){
        	currentProject = projectManager.get(new Long(-1));
        	session.setAttribute("currentProject", currentProject);
        }
		List<Project> ret = projectManager.getAll();
		model.addAttribute("projects", ret);
		model.addAttribute("currentProject", currentProject);
		return model;
	}
}