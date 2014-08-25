package it.uniroma2.gqm.dao.hibernate;


import it.uniroma2.gqm.dao.StrategyDao;
import it.uniroma2.gqm.model.Strategy;

import java.util.List;

import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository("strategyDao")
public class StrategyDaoHibernate extends GenericDaoHibernate<Strategy, Long> implements StrategyDao {
	
	
    public StrategyDaoHibernate() {
        super(Strategy.class);
    }
 
    @Override
    /*public Strategy save(Strategy strategy) {
    	Strategy updatedStrategy = strategy;
    	
    	updatedStrategy = (Strategy)getSession().merge(updatedStrategy);
    	return updatedStrategy;
    }*/
    
	public List<Strategy> findByProject(Long id) {
    	Query q =  getSession().getNamedQuery("findStrategyByProject").setLong("project_id", id);
    	return q.list();
    }
    
}