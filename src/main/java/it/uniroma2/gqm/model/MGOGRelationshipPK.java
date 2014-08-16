package it.uniroma2.gqm.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

@Embeddable
public class MGOGRelationshipPK implements Serializable {

	private static final long serialVersionUID = -2870344507198161817L;
		
	private Goal mg;
	private Goal og;
	
	//associazione unidirezionale (in Goal non sono presenti riferimenti a questa entità)	
	@OneToOne
	public Goal getMg() {
		return mg;
	}

	public void setMg(Goal mg) {
		this.mg = mg;
	}

	//associazione unidirezionale (in Goal non sono presenti riferimenti a questa entità)	
	@OneToOne
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
		return true;
	}

	@Override
	public String toString() {
		return "MGOGRelationshipPK [mg=" + mg + ", og=" + og + "]";
	}
	
	
}
