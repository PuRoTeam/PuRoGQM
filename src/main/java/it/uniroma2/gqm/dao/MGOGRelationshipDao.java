package it.uniroma2.gqm.dao;

import it.uniroma2.gqm.model.MGOGRelationship;

import org.appfuse.dao.GenericDao;


public interface MGOGRelationshipDao extends GenericDao<MGOGRelationship, Long>{
	public MGOGRelationship getMGOGRelationship(Long mgId, Long ogId);
}
