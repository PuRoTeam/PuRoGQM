package it.uniroma2.gqm.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class MGOGRelationshipPK implements Serializable {

	private static final long serialVersionUID = -2870344507198161817L;
	
	//chiavi dei due Goal puntati dalle due foreign key
	private Long mgID;
	private Long ogID;	
	
	public Long getMgID() {
		return mgID;
	}
	public void setMgID(Long mgID) {
		this.mgID = mgID;
	}
	public Long getOgID() {
		return ogID;
	}
	public void setOgID(Long ogID) {
		this.ogID = ogID;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mgID == null) ? 0 : mgID.hashCode());
		result = prime * result + ((ogID == null) ? 0 : ogID.hashCode());
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
		MGOGRelationshipPK other = (MGOGRelationshipPK) obj;
		if (mgID == null) {
			if (other.mgID != null)
				return false;
		} else if (!mgID.equals(other.mgID))
			return false;
		if (ogID == null) {
			if (other.ogID != null)
				return false;
		} else if (!ogID.equals(other.ogID))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "MGOGRelationshipPK [mgID=" + mgID + ", ogID=" + ogID + "]";
	}
	
	
}
