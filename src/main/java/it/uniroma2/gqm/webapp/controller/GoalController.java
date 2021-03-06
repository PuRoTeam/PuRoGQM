package it.uniroma2.gqm.webapp.controller;

import javax.servlet.http.HttpSession;

import it.uniroma2.gqm.service.GoalManager;
import it.uniroma2.gqm.service.ProjectManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/goals*")
@SessionAttributes({"currentProject","goal","currentUser"})
public class GoalController {
	
	private GoalManager goalManager;
	//Ciaociao

	@Autowired
	public void setGoalManager(@Qualifier("goalManager") GoalManager goalManager) {
		this.goalManager = goalManager;
	}

	private ProjectManager projectManager = null;
    
    @Autowired
    public void setProjectManager(@Qualifier("projectManager") ProjectManager projectManager) {
        this.projectManager = projectManager;
    }
    
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView handleRequest(HttpSession session) throws Exception {
		return new ModelAndView().addObject(goalManager.findByProject(projectManager.getCurrentProject(session)));
	}
}