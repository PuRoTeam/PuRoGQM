package it.uniroma2.gqm.webapp.controller;

import it.uniroma2.gqm.model.Project;
import it.uniroma2.gqm.service.MetricManager;

import javax.servlet.http.HttpSession;

import org.appfuse.service.GenericManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/projectselection*")
public class ProjectSelectionController {
    @Autowired
    private MetricManager metricManager;

    private GenericManager<Project, Long> projectManager = null;
    

    @Autowired
    public void setProjectManager(@Qualifier("projectManager") GenericManager<Project, Long> projectManager) {
        this.projectManager = projectManager;
    }
    
	@RequestMapping(method = RequestMethod.POST)
	public String handleRequest(Project currentProject, HttpSession session) throws Exception {
		if(currentProject.getId() != null)
			currentProject = projectManager.get(currentProject.getId());
		session.setAttribute("currentProject", currentProject);
		return "redirect:mainMenu";
	}
}
