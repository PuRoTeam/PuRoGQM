package it.uniroma2.gqm.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class MGOGRelationshipPK implements Serializable {

	private static final long serialVersionUID = -2870344507198161817L;
	private Goal mg;
	private Goal og;
	
	@ManyToOne
	public Goal getMg() {
		return mg;
	}
	public void setMg(Goal mg) {
		this.mg = mg;
	}
	
	@ManyToOne
	public Goal getOg() {
		return og;
	}
	public void setOg(Goal og) {
		this.og = og;
	}
	
	//TODO hash, equals, toString
	
}
