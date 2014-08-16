package it.uniroma2.gqm.model;

import it.uniroma2.gqm.model.MGOGRelationshipPK;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.appfuse.model.BaseObject;

@Entity
@Table(name = "mg_og_relationship")
@NamedQueries({
    @NamedQuery(
            name = "findAssociatedOG",
            query = "select mgog from MGOGRelationship mgog where mgog.pk.mg = :mg_id "
    ),
    @NamedQuery(
            name = "findAssociatedMG",
            query = "select mgog from MGOGRelationship mgog where mgog.pk.og = :og_id "
    ),   
})
public class MGOGRelationship extends BaseObject {

	private static final long serialVersionUID = 6535760101622612386L;

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
