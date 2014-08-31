package it.uniroma2.gqm.webapp.controller;

import it.uniroma2.gqm.model.Measurement;
import it.uniroma2.gqm.service.MeasurementManager;

import org.appfuse.dao.SearchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/measurements*")
public class MeasurementController {
    @Autowired
    private MeasurementManager measurementManager;
	
	
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleRequest(@RequestParam(required = false, value = "q") String query) throws Exception {
        Model model = new ExtendedModelMap();
        try {
        	System.out.println("Search query start: " + query);
            model.addAttribute("measurementList", measurementManager.search(query, Measurement.class));
            System.out.println("Search query end: " + query);
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute("measurementList",measurementManager.getAll());
        }
        return new ModelAndView("measurements", model.asMap());
    }	
}
