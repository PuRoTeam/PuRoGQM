package it.uniroma2.gqm.webapp.controller;


import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.service.GoalManager;
import it.uniroma2.gqm.service.GridManager;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/grid*")
@SessionAttributes({"currentProject","goal","currentUser"})
public class GridController {
	private GoalManager goalManager;
    private GridManager gridManager;
    
    @Autowired
    public void setGoalManager(@Qualifier("goalManager") GoalManager goalManager) {
        this.goalManager = goalManager;
    }
    
    @Autowired
    public void setGridManager(@Qualifier("gridManager") GridManager gridManager) {
    	this.gridManager = gridManager;
    }
    
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView handleRequest(HttpSession session) throws Exception {

		Goal ret = goalManager.get(Long.parseLong("1"));
		String tree = gridManager.explorer(ret, "null");
		//System.out.println(tree);
		
		return new ModelAndView()/*.addObject("tree", (String)tree)*/;
	}
	/*
	@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Goal showForm(HttpServletRequest request,HttpSession session, Model model) throws Exception {
        
		
		
//		model.addAttribute("currentUser",currentUser);
//		model.addAttribute("visibleGESection",visibleGESection);
//		model.addAttribute("modificableHeader",modificableHeader);
//        model.addAttribute("availableStatus",availableStatus);
        
        return ret;
    }
    */
}