package it.uniroma2.gqm.service;

import it.uniroma2.gqm.dao.StrategyDao;
import it.uniroma2.gqm.model.Project;
import it.uniroma2.gqm.model.Strategy;

import java.util.List;

import org.appfuse.service.impl.GenericManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("strategyManager")
public class StrategyManagerImpl  extends GenericManagerImpl<Strategy, Long> implements StrategyManager {
    @Autowired
	private StrategyDao strategyDao;
	
    @Autowired
    public StrategyManagerImpl(StrategyDao strategyDao) {
        super(strategyDao);
    }
 
    public List<Strategy> findByProject(Project project) {
    	if(project !=null)
    		return strategyDao.findByProject(project.getId());
    	else
    		return null;
    }
}
