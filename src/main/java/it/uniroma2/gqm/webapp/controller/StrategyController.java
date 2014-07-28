package it.uniroma2.gqm.webapp.controller;

import it.uniroma2.gqm.model.Project;
import it.uniroma2.gqm.model.Question;
import it.uniroma2.gqm.model.Strategy;
import it.uniroma2.gqm.service.QuestionManager;
import it.uniroma2.gqm.service.StrategyManager;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.appfuse.service.GenericManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/strategies*")
public class StrategyController {
    @Autowired
    private StrategyManager strategyManager;

    private GenericManager<Project, Long> projectManager = null;
    
    @Autowired
    public void setProjectManager(@Qualifier("projectManager") GenericManager<Project, Long> projectManager) {
        this.projectManager = projectManager;
    }
    
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView handleRequest(HttpSession session) throws Exception {
        Project currentProject = (Project) session.getAttribute("currentProject");
		List<Strategy> ret = strategyManager.findByProject(currentProject);
		return new ModelAndView().addObject(ret);
	}
}