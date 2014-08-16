package it.uniroma2.gqm.dao.hibernate;

import it.uniroma2.gqm.dao.MGOGRelationshipDao;
import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.MGOGRelationship;
import it.uniroma2.gqm.model.MGOGRelationshipPK;

import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;


@Repository("mgogRelationshipDao")
public class MGOGRelationshipDaoHibernate extends GenericDaoHibernate<MGOGRelationship, MGOGRelationshipPK> implements MGOGRelationshipDao{
	
	public MGOGRelationshipDaoHibernate() {
		super(MGOGRelationship.class);
	}
	
	public MGOGRelationship get(Goal mg, Goal og) {
    	MGOGRelationshipPK id = new MGOGRelationshipPK();
    	id.setMg(mg);
    	id.setOg(og);
    	return get(id);
	}

	@Override
	public void remove(Goal mg, Goal og) {
    	MGOGRelationshipPK id = new MGOGRelationshipPK();
    	id.setMg(mg);
    	id.setOg(og);
    	remove(id);
	}	
}
