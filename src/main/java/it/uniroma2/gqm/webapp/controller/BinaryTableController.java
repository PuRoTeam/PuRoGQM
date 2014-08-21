package it.uniroma2.gqm.webapp.controller;

import it.uniroma2.gqm.model.BinaryElement;
import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.GoalStatus;
import it.uniroma2.gqm.model.GoalType;
import it.uniroma2.gqm.model.MGOGRelationship;
import it.uniroma2.gqm.model.Metric;
import it.uniroma2.gqm.model.Project;
import it.uniroma2.gqm.service.BinaryTableManager;
import it.uniroma2.gqm.service.GoalManager;
import it.uniroma2.gqm.service.MGOGRelationshipManager;
import it.uniroma2.gqm.service.MetricManager;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.appfuse.model.User;
import org.appfuse.service.GenericManager;
import org.appfuse.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/binarytable*")
@SessionAttributes({"currentProject","currentUser"})
public class BinaryTableController {	
	
	@Autowired
    private GoalManager goalManager;
	private MetricManager metricManager;
	
	private UserManager userManager = null;
	private BinaryTableManager binaryManager = null;
    private GenericManager<Project, Long> projectManager = null;

	private MGOGRelationshipManager mgogRelationshipManager;
	
	@Autowired
    public void setMetricManager(@Qualifier("metricManager") MetricManager metricManager) {
        this.metricManager = metricManager;
    }
	
	@Autowired
    public void setProjectManager(@Qualifier("projectManager") GenericManager<Project, Long> projectManager) {
        this.projectManager = projectManager;
    }

	@Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
	
	@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Goal showTable(HttpServletRequest request,HttpSession session, Model model) throws Exception {
		
        String id = request.getParameter("id");
        Goal ret = null;
        MGOGRelationship retRelation = null;
        
        Project currentProject = (Project) session.getAttribute("currentProject");
        User currentUser = userManager.getUserByUsername(request.getRemoteUser());

        /*if (!StringUtils.isBlank(id)) {
        	ret = goalManager.get(new Long(id));
        	retRelation = mgogRelationshipManager.getAssociatedRelation(ret);
        	ret.setRelationWithMG(retRelation);
        }
        
        //Recupao la lista degli MG associati a questo OG
        Goal mg = ret.getRelationWithMG().getPk().getMg();
        
        //Recupero tutte le metriche associate a 'mg'
        List<Metric> metrics = goalManager.getMeasuredMetricByGoal(mg);
        
        boolean satisfy = true;
        BinaryElement mainGoal = new BinaryElement(ret, 0);
        List<BinaryElement> childGoal; 
        
        //Calcolo valore di soddisfacimento (1 o 0)
        for(Metric m: metrics){
        	satisfy &= metricManager.getSatisfaction(m);
        }
        if(satisfy)
        	mainGoal.setValue(1);
        
        //TODO recuperare la lista degli OG figli di questo OG
        List<Goal> ogs = goalManager.getOGChildren(ret);

        //Salvo un elto nell'array per ogni OG figlio
        for(Goal og: ogs){
        	
        	mg = og.getRelationWithMG().getPk().getMg();  //Ipotizzato solo 1 MG associato possibile
        	metrics = goalManager.getMeasuredMetricByGoal(mg);
        	
        	satisfy = true;
        	for(Metric m: metrics){
        		
        		satisfy &= metricManager.getSatisfaction(m);
        	}
        	if(satisfy)
    			childGoal.add(new BinaryElement(og,1));
    		else
    			childGoal.add(new BinaryElement(og,0));
        }
       
		model.addAttribute("mainGoal", mainGoal);
		model.addAttribute("childGoal", childGoal);
		model.addAttribute("currentUser",currentUser);*/

        return ret;
    }
}
