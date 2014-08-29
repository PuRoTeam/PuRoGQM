package it.uniroma2.gqm.service;

import it.uniroma2.gqm.model.BinaryTable;
import it.uniroma2.gqm.model.Goal;
import it.uniroma2.gqm.model.GoalQuestion;
import it.uniroma2.gqm.model.Metric;
import it.uniroma2.gqm.model.Question;
import it.uniroma2.gqm.model.QuestionMetric;

import java.util.HashSet;
import java.util.Set;

import org.appfuse.service.impl.GenericManagerImpl;
import org.springframework.stereotype.Service;

@Service("graphManager")
public class GraphManagerImpl extends GenericManagerImpl<BinaryTable, Long> implements GraphManager{

	@Override
	public String getGraph(Goal g) {
				
		String tree = "{";
		tree += "\"name\":";
		tree += "\""+g.getDescription()+"\"";
		tree += ",\"parent\":";
		tree += "\"null\"";
		
		//Has Questions
		if(g.getQuestions().size() > 0) { 
			
			tree += ", \"questions\":[";
			Set<Question> questions = new HashSet<Question>();
			for (GoalQuestion gq : g.getQuestions()) {
				questions.add(gq.getQuestion());
				
				tree += "\"parent\":";
				tree += "\""+g.getDescription()+"\"";
				tree += ",\"name\":";
				int i = 0;
				for (Question q : questions) {
					if(i != 0)
						tree += ",";
					tree += "\""+q.getName()+"\"";
					
					//Has metrics
					if(q.getMetrics().size() > 0) {
						
						tree += ", \"metrics\":[";
						Set<Metric> metrics = new HashSet<Metric>();
						for(QuestionMetric qm : q.getMetrics()) {
							metrics.add(qm.getMetric());
							
							tree += "\"parent\":";
							tree += "\""+q.getName()+"\"";
							tree += ",\"name\":";
							int j = 0;
							for (Metric m : metrics) {
								if(j != 0)
									tree +=",";
								tree += "\""+m.getName()+"\"";
								j++;
							}
							tree += "]}";
							
						}			
						
					} else {
						tree += "}";
					} 
				}
				tree += "]}";
			}			
		
				
			
		} else { //non ha figli
			tree += "}";
		}
    		
		return tree;
	}

}
