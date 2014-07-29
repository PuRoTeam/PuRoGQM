package it.uniroma2.gqm.dao.hibernate;

import it.uniroma2.gqm.dao.MGOGRelationshipDao;
import it.uniroma2.gqm.model.MGOGRelationship;

import java.util.List;

import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;


@Repository("mgogRelationshipDao")
public class MGOGRelationshipDaoHibernate extends GenericDaoHibernate<MGOGRelationship, Long> implements MGOGRelationshipDao{

	
	public MGOGRelationshipDaoHibernate(Class<MGOGRelationship> persistentClass) {
		super(MGOGRelationship.class);
	}
	

    @SuppressWarnings("unchecked")
	public MGOGRelationship getMGOGRelationship(Long mgId, Long ogId) {
    	List<MGOGRelationship> mgogs;
    	Query q =  getSession().getNamedQuery("findMGOGRelationship").setLong("mg_id", mgId).setLong("og_id", ogId);
    	mgogs = q.list();
    	return (mgogs!=null && mgogs.size()>0) ? mgogs.get(0) : null;
	}

}
