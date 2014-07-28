package it.uniroma2.gqm.dao;

import static org.junit.Assert.*;

import java.util.List;

import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.GoalQuestion;
import it.uniroma2.gqm.model.GoalQuestionPK;
import it.uniroma2.gqm.model.GoalQuestionStatus;
import it.uniroma2.gqm.model.Project;
import it.uniroma2.gqm.model.Question;

import org.appfuse.dao.BaseDaoTestCase;
import org.appfuse.dao.GenericDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class QuestionDaoTest extends BaseDaoTestCase {
	
    @Autowired
    private QuestionDao questionDao;
    
    @Autowired
    private GoalDao goalDao;    

    @Test
    public void testFindQuestionByProject() throws Exception {    	
        List<Question> questions = questionDao.findByProject(new Long(-1));
        for(Question q:questions){
        	System.out.println("Question: " + q.getName());
        }       
        assertTrue(!questions.contains(goalDao.get(new Long(-2))));
    }
    
    @Test
    public void testGoalQuestionAddAndDelete() throws Exception {
    	Question question = questionDao.get(new Long(-1));
    	for(GoalQuestion gq : question.getGoals()){
    		if(gq.getPk().getGoal().getId() == -5){
    			question.getGoals().remove(gq);
    		}
    	}
    	questionDao.save(question);
    	flush();
    	
    	Question questionTest = questionDao.get(new Long(-1));
    	Goal g = goalDao.get(new Long(-5));
    	assertTrue(questionTest.getGoals().size() == 1);
    	

    	
    	question = questionDao.get(new Long(-1));
    	g = goalDao.get(new Long(-5));
    	GoalQuestion gq = new GoalQuestion();
    	gq.setPk(new GoalQuestionPK(g,question));
    	gq.setStatus(GoalQuestionStatus.APPROVED);
    	question.getGoals().add(gq);
    	
    	questionDao.save(question);
    	flush();
    	
    	
    	questionTest = questionDao.get(new Long(-1));
    	g = goalDao.get(new Long(-5));
    	assertTrue(questionTest.getGoals().size() == 2);
    	
    	System.out.println("Okkkkkkkkkkkkkkkkkkkkkkkk2");
    	g = goalDao.get(new Long(-2));
    	gq = new GoalQuestion();
    	gq.setPk(new GoalQuestionPK(g,question));
    	gq.setStatus(GoalQuestionStatus.PROPOSED);
    	question.getGoals().add(gq);
    	
    	questionDao.save(question);
    	flush();
    	assertTrue(questionTest.getGoals().size() == 3);
    	
    }
}