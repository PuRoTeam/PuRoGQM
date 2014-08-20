<%@ include file="/common/taglibs.jsp"%>
<head>
    <title><fmt:message key="binaryTable.title"/></title>
    <meta name="menu" content="DefinitionPhaseMenu"/>
    <style type="text/css">
	    #col1 {
		    position: absolute;
		    left: 5px;
		    padding: 0px;  
		}
		
		#col2 {
		    position: absolute;
		    margin-left: 100px;
		    padding: 0px;
		}
		
		#col3 {
		    margin-left: 200px;
		    padding: 0px;
		}
    </style>
    
</head>
<div class="span10">
    <h2><fmt:message key='binaryTable.heading'/></h2>
    

	<div id="col$1}">
    		<a href="#">${mainGoal.goal.description}</a>
    		<div>${mainGoal.value}</div>
  	</div>
	<c:forEach var="childGoal" varStatus="status" items="${userList}" step="1" begin="1" end="${childGoal.size()}">
		<div id="col${status.index % 3 + 1}">
    		<a href="#">${childGoal.goal.description}</a>
    		<div>${childGoal.value}</div>
  		</div>
	</c:forEach>
	
</div>