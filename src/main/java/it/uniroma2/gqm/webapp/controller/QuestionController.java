package it.uniroma2.gqm.webapp.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import it.uniroma2.gqm.model.Project;
import it.uniroma2.gqm.model.Question;
import it.uniroma2.gqm.service.GoalManager;
import it.uniroma2.gqm.service.QuestionManager;

import org.appfuse.service.GenericManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/questions*")
public class QuestionController {
    @Autowired
    private QuestionManager questionManager;

    private GenericManager<Project, Long> projectManager = null;
    
    @Autowired
    public void setProjectManager(@Qualifier("projectManager") GenericManager<Project, Long> projectManager) {
        this.projectManager = projectManager;
    }
    
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView handleRequest(HttpSession session) throws Exception {
        Project currentProject = (Project) session.getAttribute("currentProject");
		List<Question> ret = questionManager.findByProject(currentProject);
		return new ModelAndView().addObject(ret);
	}
}