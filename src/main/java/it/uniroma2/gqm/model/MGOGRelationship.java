package it.uniroma2.gqm.model;

import javax.persistence.EmbeddedId;

import org.appfuse.model.BaseObject;

public class MGOGRelationship extends BaseObject {

	private MGOGRelationshipPK pk;
	
	@EmbeddedId
	public MGOGRelationshipPK getPk() {
		return pk;
	}

	public void setPk(MGOGRelationshipPK pk) {
		this.pk = pk;
	}

	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
