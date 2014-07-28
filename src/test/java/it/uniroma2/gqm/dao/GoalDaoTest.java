package it.uniroma2.gqm.dao;

import static org.junit.Assert.*;

import java.util.List;

import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.GoalQuestion;
import it.uniroma2.gqm.model.GoalStatus;
import it.uniroma2.gqm.model.Project;
import it.uniroma2.gqm.model.Question;

import org.appfuse.dao.BaseDaoTestCase;
import org.appfuse.dao.GenericDao;
import org.appfuse.dao.UserDao;
import org.appfuse.service.UserManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GoalDaoTest extends BaseDaoTestCase {
	@Autowired
	private UserDao userDao;

    
    @Autowired
    private GoalDao goalDao;    

    public void testGoalQuestion() throws Exception {    	
    	Goal goal = goalDao.get(new Long(-1));
    	assertTrue("The Question associated with goal -1 must be zero. ",goal.getQuestions().size() == 0);
    }

    public void testGoalAddVotes() throws Exception {  
    	System.out.println("******************** INIZIO" + this.getClass().getName());    	
    	Goal goal = goalDao.get(new Long(-1));  	
    	goal.getVotes().add(userDao.get(new Long(-1))); // first vote
    	goalDao.save(goal);
    	flush();    	
    	assertTrue(goalDao.get(new Long(-1)).getVotes().size() == 1);
    	
    	goal = goalDao.get(new Long(-1));
    	goal.getVotes().add(userDao.get(new Long(-2))); // second vote
    	goalDao.save(goal);
    	flush();    	
    	assertTrue(goalDao.get(new Long(-1)).getVotes().size() == 2);
    	assertTrue("The status should be APPROVED", goalDao.get(new Long(-1)).getStatus().equals(GoalStatus.APPROVED));
    	
    	goal = goalDao.get(new Long(-1));
    	goal.setStatus(GoalStatus.FOR_REVIEW);
    	goalDao.save(goal);
    	flush();  
    	assertTrue("The status should be FOR_REVIEW", goalDao.get(new Long(-1)).getStatus().equals(GoalStatus.FOR_REVIEW));
    	
    	goal = goalDao.get(new Long(-1));
    	goal.setDescription(goal.getDescription().concat("XXX")); // change something for propose the goal...
    	goal.setStatus(GoalStatus.PROPOSED);
    	goalDao.save(goal);
    	flush();  
    	assertTrue("The status should be PROPOSED", goalDao.get(new Long(-1)).getStatus().equals(GoalStatus.PROPOSED));    	
    	System.out.println("******************** FINE" + this.getClass().getName());
    }
    
    
    @Test
    public void testDeleteDraftGoal() throws Exception {  
    	
    	Goal goal = goalDao.get(new Long(-7));  	
    	goalDao.remove(goal);
    	flush();    	    	
    	assertFalse("The goal (-7) should be deleted!", goalDao.exists(new Long(-7)));
    }
}