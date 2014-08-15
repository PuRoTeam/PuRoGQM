package it.uniroma2.gqm.dao.hibernate;

import it.uniroma2.gqm.dao.MGOGRelationshipDao;
import it.uniroma2.gqm.model.MGOGRelationship;
import it.uniroma2.gqm.model.MGOGRelationshipPK;

import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;


@Repository("mgogRelationshipDao")
public class MGOGRelationshipDaoHibernate extends GenericDaoHibernate<MGOGRelationship, MGOGRelationshipPK> implements MGOGRelationshipDao{
	
	public MGOGRelationshipDaoHibernate() {
		super(MGOGRelationship.class);
	}
	
	public MGOGRelationship get(Long mgId, Long ogId) {
    	MGOGRelationshipPK id = new MGOGRelationshipPK();
    	id.setMgID(mgId);
    	id.setOgID(ogId);
    	return get(id);
	}

	@Override
	public void remove(Long mgId, Long ogId) {
    	MGOGRelationshipPK id = new MGOGRelationshipPK();
    	id.setMgID(mgId);
    	id.setOgID(ogId);
    	remove(id);
	}	
}
