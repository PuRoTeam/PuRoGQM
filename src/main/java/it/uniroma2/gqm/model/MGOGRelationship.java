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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pk == null) ? 0 : pk.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MGOGRelationship other = (MGOGRelationship) obj;
		if (pk == null) {
			if (other.pk != null)
				return false;
		} else if (!pk.equals(other.pk))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MGOGRelationship [pk=" + pk + "]";
	}


}
