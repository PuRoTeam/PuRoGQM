package it.uniroma2.gqm.dao;

import it.uniroma2.gqm.model.MGOGRelationship;
import it.uniroma2.gqm.model.MGOGRelationshipPK;

import org.appfuse.dao.GenericDao;


public interface MGOGRelationshipDao extends GenericDao<MGOGRelationship, MGOGRelationshipPK>{
	public MGOGRelationship getMGOGRelationship(Long mgId, Long ogId);
}
