package it.uniroma2.gqm.dao.hibernate;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.appfuse.dao.UserDao;
import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.appfuse.model.User;

import it.uniroma2.gqm.dao.*;
import it.uniroma2.gqm.model.*;
 
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
@Repository("goalDao")
public class GoalDaoHibernate extends GenericDaoHibernate<Goal, Long> implements GoalDao {
	
	@Autowired
	private UserDao userDao;
	
	
    public GoalDaoHibernate() {
        super(Goal.class);
    }
 
    public List<Goal> findByDescription(String description) {
        return getSession().createCriteria(Goal.class).add(Restrictions.eq("description", description)).list();
    }

    
    
    
	@Override
	public void remove(Goal object) {
		super.remove(object);
	}

	@Override
	public Goal save(Goal object)  {
		if(object.getId() != null){	
			if(object.getStatus() == GoalStatus.PROPOSED){				
				Session old= getSessionFactory().openSession();			
				Goal gTemp = (Goal) old.get(Goal.class, object.getId());
				System.out.println("*** OLD STATUS = " +  gTemp.getStatus());	
				if(gTemp.getStatus() == GoalStatus.FOR_REVIEW){
					System.out.println("*** clean old votes....");	
					// clean previous vote....
					object.getVotes().clear();
				}
				old.close();
			}
		}
		// if the quorum was reached, change status to Accepted
		if(object.getStatus() == GoalStatus.PROPOSED && object.getNumberOfVote() == object.getQuorum())
			object.setStatus(GoalStatus.ACCEPTED);
		
		try{
			System.out.println("numero di voti: " + object.getVotes().size());
			if(object.getVotes() != null && object.getVotes().size() > 0){
				List<Long> ids = new ArrayList<Long>();
				for(User u:object.getVotes())
					ids.add(u.getId());
								
				object.getVotes().clear();
				getSession().saveOrUpdate(object);
				getSession().flush();
				for(Long uId:ids){
					object.getVotes().add(userDao.get(uId));
					getSession().saveOrUpdate(object);
					getSession().flush();
				}
			}else {
				System.out.println("A");
		        getSession().saveOrUpdate(object);
		        System.out.println("B");
		        getSession().flush();
		        System.out.println("C");
			}
		} catch (Exception e){	
			System.out.println("1");
			getSession().saveOrUpdate(object);
			System.out.println("2");
			getSession().flush();
			System.out.println("3");
		}

		return object;
	}

	@Override
	public List<Goal> findByProject(Long id) {
		     	Query q =  getSession().getNamedQuery("findGoalByProject").setLong("project_id", id);
    	return q.list();
	}
    
    
}