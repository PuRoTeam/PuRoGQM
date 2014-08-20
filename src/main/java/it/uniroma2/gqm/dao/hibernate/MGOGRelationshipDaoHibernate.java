package it.uniroma2.gqm.dao.hibernate;

import java.util.List;

import it.uniroma2.gqm.dao.MGOGRelationshipDao;
import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.GoalType;
import it.uniroma2.gqm.model.MGOGRelationship;
import it.uniroma2.gqm.model.MGOGRelationshipPK;

import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;


@Repository("mgogRelationshipDao")
public class MGOGRelationshipDaoHibernate extends GenericDaoHibernate<MGOGRelationship, MGOGRelationshipPK> implements MGOGRelationshipDao{
	
	public MGOGRelationshipDaoHibernate() {
		super(MGOGRelationship.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public MGOGRelationship getAssociatedRelation(Goal goal) {
		Query q = null;
		if(GoalType.isMG(goal)) {
			q = getSession().getNamedQuery("findAssociatedOG").setLong("mg_id", goal.getId());
			
		}
		else if(GoalType.isOG(goal)) {
			q = getSession().getNamedQuery("findAssociatedMG").setLong("og_id", goal.getId());
		}
		else
			return null;
		
    	List<MGOGRelationship>relations = q.list();
    	return (relations != null && relations.size() > 0) ? relations.get(0) : null;
	}
}
