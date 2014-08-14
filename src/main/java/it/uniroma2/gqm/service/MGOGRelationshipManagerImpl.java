package it.uniroma2.gqm.service;

import it.uniroma2.gqm.dao.MGOGRelationshipDao;
import it.uniroma2.gqm.model.MGOGRelationship;
import it.uniroma2.gqm.model.MGOGRelationshipPK;

import org.appfuse.service.impl.GenericManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("mgogRelationshipManager")
public class MGOGRelationshipManagerImpl extends GenericManagerImpl<MGOGRelationship, MGOGRelationshipPK> implements MGOGRelationshipManager {
	MGOGRelationshipDao mgogRelationshipDao;
	
	@Autowired
	public MGOGRelationshipManagerImpl(MGOGRelationshipDao mgogRelationshipDao) {
		super(mgogRelationshipDao);
		this.mgogRelationshipDao = mgogRelationshipDao;
	}

}
