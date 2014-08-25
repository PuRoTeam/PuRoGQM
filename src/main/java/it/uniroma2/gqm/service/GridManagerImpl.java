package it.uniroma2.gqm.service;

import it.uniroma2.gqm.model.Goal;

import org.appfuse.service.impl.GenericManagerImpl;
import org.springframework.stereotype.Service;

@Service("gridManager")
public class GridManagerImpl extends GenericManagerImpl<Goal, Long> implements GridManager{

}
