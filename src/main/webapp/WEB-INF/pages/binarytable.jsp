<%@ include file="/common/taglibs.jsp"%>
<head>
    <title><fmt:message key="binaryTable.title"/></title>
    <meta name="menu" content="DefinitionPhaseMenu"/>
    <style type="text/css">
	    #col1 {
	    	border-style: solid;
		    position: relative;
		    padding: 0px;
		    width: 100px;
		}
		
		#col2 {
			border-style: solid;
		    position: absolute;
		    left: 100px;
		    padding: 0px;
		}
		
		#col3 {
			border-style: solid;
			position: absolute;
		    left: 200px;
		    padding: 0px;
		}
    </style>
    
</head>
<div class="span10">
    <h2><fmt:message key='binaryTable.heading'/></h2>
    
    <c:set var="salary" scope="page" value="${mainGoal}"/>
	<c:out value="${salary}"/>
    <c:out value="${mainGoal}"/>
	<div id="col1">
   		<a href="#"><c:out value="${mainGoal.goal.description}"/></a>
   		<div><c:out value="${mainGoal.value}"/></div>
  	</div>
	<c:forEach var="childGoal" varStatus="status" items="${childGoal}" step="1" begin="1" end="${childGoal.size()}">
		<div id=col<c:out value="${status.index % 3 + 1}"/>>
    		<a href="#"><c:out value="${childGoal.goal.description}"/></a>
    		<div><c:out value="${childGoal.value}"/></div>
  		</div>
	</c:forEach>
	
</div>