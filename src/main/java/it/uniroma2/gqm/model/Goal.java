package it.uniroma2.gqm.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.appfuse.model.BaseObject;
import org.appfuse.model.User;

@Entity
@NamedQueries({
    @NamedQuery(
            name = "findGoalByProject",
            query = "select g from Goal g  where g.project.id= :project_id "
    ),
    @NamedQuery(
            name = "findMeasuredGoal",
            query = "select distinct g from Goal g inner join g.questions gq " +
            		" inner join gq.pk.question q  " +
            		" inner join q.metrics qm " +
            		" inner join qm.pk.metric m " +
            		" where g.project.id= :project_id "
    ),
    @NamedQuery(
            name = "findOrganizationalGoal",
            query = "select distinct g from Goal g" +
            		" where g.type = 0 and g.project.id= :project_id "
    )
})
public class Goal extends BaseObject {

	private static final long serialVersionUID = -5289775436595676632L;
	
	//Common fields
	private String description;	
	private Integer type;
	private String scope;
	private String focus;
	private Goal parent;
	private User goalOwner;
	private User goalEnactor;
	private GoalStatus status;
	private Long id;
	private Project project;		
	private Set<User> QSMembers = new HashSet<User>();
	private Set<User> MMDMMembers = new HashSet<User>();
	private Set<GoalQuestion> questions = new HashSet<GoalQuestion>();	
	private Set<User> votes = new HashSet<User>();
	//private Set<Goal> children = new HashSet<Goal>();
	private String refinement;
	
	
	
	//private Goal associatedGoal; //necessario per visualizzare il goal associato nella lista di goal
	private MGOGRelationship relationWithMG;
	private MGOGRelationship relationWithOG;
	
	//OG fields
	private String activity;
	private String object;
	private String magnitude;
	private String timeframe;
	private String constraints;
	//private Strategy strategy;
	
	//OG hierarchy fields
	private int childType  = -1;
	private int parentType = -1;
	
	private Goal orgParent;
	private Strategy ostrategyParent;
	
	private Set<Goal> orgChild = new HashSet<Goal>();
	private Set<Strategy> ostrategyChild = new HashSet<Strategy>();
	
	//MG fields
	private String subject;
	private String context;
	private String viewpoint;
	private String impactOfVariation;
	
	public Goal() {
		
	}

	public Goal(Long id) {
		this.id = id;
	}
	
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id @Column(name = "goal_id",nullable=false,unique=true)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "description", length = 255, nullable = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "type", length = 255, nullable = false)
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "subject", length = 255)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "scope", length = 255)
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Column(name = "focus", length = 255)
	public String getFocus() {
		return focus;
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}

	@Column(name = "context", length = 255)
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	@Column(name = "viewpoint", length = 255)
	public String getViewpoint() {
		return viewpoint;
	}

	public void setViewpoint(String viewpoint) {
		this.viewpoint = viewpoint;
	}

	@Column(name = "impact_of_variation", length = 255)
	public String getImpactOfVariation() {
		return impactOfVariation;
	}

	public void setImpactOfVariation(String impactOfVariation) {
		this.impactOfVariation = impactOfVariation;
	}

	@Transient
	public String getTypeAsString() {
		if(GoalType.isMG(this))
			return GoalType.MG.getString();
		else
			return GoalType.OG.getString();
	}
	
	/*
	@Transient
	public String getInterpretationModelAsString() {
		if(this.interpretationModel == null)
			return "Not specified";
		if (this.interpretationModel == 1) {
			return "GQM";
		} else if (this.interpretationModel == 2) {
			return "GQM+Strategies";
		} else {
			return "Not specified";
		}
	}*/

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
		Goal other = (Goal) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Goal [id=" + id + ", description=" + description + "]";
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 50)
	public GoalStatus getStatus() {
		return status;
	}
	
	public void setStatus(GoalStatus status) {
		this.status = status;
	}
	
	/*
	@ManyToOne
	@JoinColumn(name = "strategy_id", nullable = true)
	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
	*/
	
	@ManyToOne
	@JoinColumn(name = "go_id", nullable = false)	
	public User getGoalOwner() {
		return goalOwner;
	}
	
	public void setGoalOwner(User goalOwner) {
		this.goalOwner = goalOwner;
	}

    @ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(name = "goal_qs", joinColumns = { @JoinColumn(name = "goal_id") }, inverseJoinColumns = @JoinColumn(name = "user_id"))
	public Set<User> getQSMembers() {
		return QSMembers;
	}
    
	public void setQSMembers(Set<User> qSMembers) {
		QSMembers = qSMembers;
	}

	@ManyToOne
	@JoinColumn(name = "ge_id", nullable = true)	
	public User getGoalEnactor() {
		return goalEnactor;
	}
	
	public void setGoalEnactor(User goalEnactor) {
		this.goalEnactor = goalEnactor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
    @ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(name = "goal_mmdm",joinColumns = { @JoinColumn(name = "goal_id") },inverseJoinColumns = @JoinColumn(name = "user_id"))	
	public Set<User> getMMDMMembers() {
		return MMDMMembers;
	}
    
	public void setMMDMMembers(Set<User> mMDMMembers) {
		this.MMDMMembers = mMDMMembers;
	}
	
	@Column(name = "refinement", length = 255)
	public String getRefinement() {
		return refinement;
	}

	public void setRefinement(String refinement) {
		this.refinement = refinement;
	}
	
    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(name = "goal_vote",joinColumns = { @JoinColumn(name = "goal_id") },inverseJoinColumns = @JoinColumn(name = "user_id"))	
	public Set<User> getVotes() {
		return votes;
	}
    
	public void setVotes(Set<User> votes) {
		this.votes = votes;
	}
	
	@Transient
	public int getNumberOfVote(){
		return getVotes().size();
	}
	
	@Transient
	public int getQuorum(){
		return this.project.getProjectManagers().size();
	}
	
	/*
	@ManyToOne
	@JoinColumn(name = "parent_id", nullable = true)	
	public Goal getParent() {
		return parent;
	}

	public void setParent(Goal parent) {
		this.parent = parent;
	}
	*/
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.goal")
	public Set<GoalQuestion>  getQuestions() {
		return questions;
	}

	public void setQuestions(Set<GoalQuestion> questions) {
		this.questions = questions;
	}

	@Column(name = "activity", length = 255)
	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	@Column(name = "object", length = 255)
	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	@Column(name = "magnitude", length = 255)
	public String getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(String magnitude) {
		this.magnitude = magnitude;
	}

	@Column(name = "timeframe", length = 255)
	public String getTimeframe() {
		return timeframe;
	}

	public void setTimeframe(String timeframe) {
		this.timeframe = timeframe;
	}
	
	@Column(name = "constraints", length = 255)
	public String getConstraints() {
		return constraints;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}
	
	@Transient
	public int getChildType() {
		
		if(this.orgChild.size() > 0){
			return 0;
		}else if (this.ostrategyChild.size() > 0) {
			return 1;
		}else {
			return -1;
		}
	}

	public void setChildType(int childType) {
		this.childType = childType;
	}
	
	@Transient
	public int getParentType() {
		
		if(this.orgParent != null){
			return 0;
		}else if (this.ostrategyParent != null) {
			return 1;
		}else {
			return -1;
		}
	}

	public void setParentType(int parentType) {
		this.parentType = parentType;
	}

	@ManyToOne
	//@JoinColumn(name="oparent_id")
	public Goal getOrgParent() {
		return orgParent;
	}

	public void setOrgParent(Goal orgParent) {
		this.orgParent = orgParent;
	}
	
	@OneToMany(mappedBy="orgParent")
	public Set<Goal> getOrgChild() {
		return orgChild;
	}
	
	public void setOrgChild(Set<Goal> orgChild) {
		this.orgChild = orgChild;
	}
	
	@ManyToOne
	//@JoinColumn(name="sparent_id")
	public Strategy getOstrategyParent() {
		return ostrategyParent;
	}

	public void setOstrategyParent(Strategy strategyParent) {
		this.ostrategyParent = strategyParent;
	}
	
	@OneToMany(mappedBy="sorgParent")	
	public Set<Strategy> getOstrategyChild() {
		return ostrategyChild;
	}

	public void setOstrategyChild(Set<Strategy> strategyChild) {
		this.ostrategyChild = strategyChild;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "pk.og") //this ha ruolo OG nella relazione
	public MGOGRelationship getRelationWithMG() {
		return relationWithMG;
	}

	public void setRelationWithMG(MGOGRelationship relationWithMG) {
		this.relationWithMG = relationWithMG;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "pk.mg") //this ha ruolo MG nella relazione
	public MGOGRelationship getRelationWithOG() {
		return relationWithOG;
	}

	public void setRelationWithOG(MGOGRelationship relationWithOG) {
		this.relationWithOG = relationWithOG;
	}
	
	/**
	 * Se il goal è un MG, viene restituita la relazione con OG, se è un OG, la relazione con MG
	 * @return Un oggetto MGOGRelationship rappresentante la relazione, o null in caso non esista
	 */
	@Transient
	public MGOGRelationship getMGOGRelation() {
		if(GoalType.isMG(this))
			return getRelationWithOG();
		else if(GoalType.isOG(this))
			return getRelationWithMG();
		
		return null;
	}
	
}
