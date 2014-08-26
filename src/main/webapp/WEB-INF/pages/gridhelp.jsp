<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="goalDetail.title"/></title>
    <meta name="menu" content="DefinitionPhaseMenu"/>
</head>
<body>

<c:set var="tree" scope="page" value="${tree}"/>
<% 
	JSONObject jsonT = (JSONObject) pageContext.getAttribute("tree"); 
	out.println(jsonT.toString());
%>

</body>

