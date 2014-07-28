package it.uniroma2.gqm.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.appfuse.model.BaseObject;
import org.appfuse.model.User;

@Entity
@NamedQueries({
    @NamedQuery(
            name = "findStrategyByProject",
            query = "select s from Strategy s  where s.project.id= :project_id"
    )
})
public class Strategy extends BaseObject {
	private static final long serialVersionUID = 1L;
	private Long id;
	private Project project;
	private String name;
	private String assumption;
	private List<Goal> goals;
	private User strategyOwner;
	
 	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id @Column(name = "strategy_id",nullable=false,unique=true)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "name", length = 255, nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "assumption", length = 4000, nullable = true)
	public String getAssumption() {
		return assumption;
	}
	public void setAssumption(String assumption) {
		this.assumption = assumption;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Strategy other = (Strategy) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Strategy [id=" + id + ", name=" + name + ", assumption="
				+ assumption + "]";
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	@OneToMany(mappedBy="strategy", cascade=CascadeType.ALL)
	public List<Goal> getGoals() {
		return goals;
	}
	public void setGoals(List<Goal> goals) {
		this.goals = goals;
	}
	
	@ManyToOne
	@JoinColumn(name = "so_id", nullable = false)	
	public User getStrategyOwner() {
		return strategyOwner;
	}
	public void setStrategyOwner(User strategyOwner) {
		this.strategyOwner = strategyOwner;
	}
}
