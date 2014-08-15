package it.uniroma2.gqm.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.appfuse.model.BaseObject;

@Entity
@Table(name = "mg_og_relationship")
public class MGOGRelationship extends BaseObject {

	private static final long serialVersionUID = 6535760101622612386L;

	private MGOGRelationshipPK pk;
	
	private Goal mg;
	private Goal og;
	
	@EmbeddedId
	public MGOGRelationshipPK getPk() {
		return pk;
	}

	public void setPk(MGOGRelationshipPK pk) {
		this.pk = pk;
	}

	//associazione unidirezionale (in Goal non sono presenti riferimenti a questa entità)
	//@MapsId("mgID")
	@OneToOne
	@JoinColumn(name="mgID", insertable=false, updatable=false)
	public Goal getMg() {
		return mg;
	}

	public void setMg(Goal mg) {
		this.mg = mg;
	}

	//associazione unidirezionale (in Goal non sono presenti riferimenti a questa entità)
	//@MapsId("ogID")
	@OneToOne
	@JoinColumn(name="ogID", insertable=false, updatable=false)
	public Goal getOg() {
		return og;
	}

	public void setOg(Goal og) {
		this.og = og;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mg == null) ? 0 : mg.hashCode());
		result = prime * result + ((og == null) ? 0 : og.hashCode());
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
		if (mg == null) {
			if (other.mg != null)
				return false;
		} else if (!mg.equals(other.mg))
			return false;
		if (og == null) {
			if (other.og != null)
				return false;
		} else if (!og.equals(other.og))
			return false;
		if (pk == null) {
			if (other.pk != null)
				return false;
		} else if (!pk.equals(other.pk))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MGOGRelationship [pk=" + pk + ", mg=" + mg + ", og=" + og + "]";
	}
	
	

}
