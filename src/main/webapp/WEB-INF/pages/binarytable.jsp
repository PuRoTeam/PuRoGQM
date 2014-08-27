<%@ include file="/common/taglibs.jsp"%>
<head>
    <title><fmt:message key="binaryTable.title"/></title>
    <meta name="menu" content="DefinitionPhaseMenu"/>
    <style type="text/css">
	    #main {
	    	border-style: solid;
		    /*position: relative;*/
		    display: table-cell;		    
		    width: 100px;
		    text-align: center;
		    background-color: lightcoral;
		}
		
		#child {
			border-style: solid;
		    /*position: relative;*/
		    display: table-cell;		    
		    width: 100px;
		    text-align: center;
		}
    </style>
    
</head>
<div class="span10">
    <h2><fmt:message key='binaryTable.heading'/></h2>
    
	<div id="main">
   		<a href="binarytable?id=${mainGoal.goal.id}}">${mainGoal.goal.description}</a>
   		<div>${mainGoal.value}</div>
  	</div>
	<c:forEach var="childGoal" varStatus="status" items="${childGoal}" step="1" begin="0" end="${childGoal.size()}">
		<div id="child"/>
    		<a href="binarytable?id=${childGoal.goal.id}">${childGoal.goal.description}</a>
    		<div>${childGoal.value}</div>
  		</div>
	</c:forEach>
	
</div>