<%@ include file="/common/taglibs.jsp"%>
<head>
    <title><fmt:message key="strategyDetail.title"/></title>
    <meta name="menu" content="DefinitionPhaseMenu"/>
</head>
 
<div class="span2">
    <h2><fmt:message key='strategyDetail.heading'/></h2>
    <c:choose>
    	<c:when test="${strategy.strategyOwner eq currentUser}">You are <b>Owner</b> for this Strategy.</c:when>
    </c:choose>
</div>

<div class="span7">
    <form:errors path="*" cssClass="alert alert-error fade in" element="div"/>
    <form:form commandName="strategy" method="post" action="strategyform" id="strategyForm"
               cssClass="well form-horizontal">
    <form:hidden path="id"/>

    <spring:bind path="strategy.project">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
    </spring:bind>
        <appfuse:label styleClass="control-label" key="strategy.project"/>
        <div class="controls">
        	<form:select path="project.id" onchange=""  >
					<form:option value="${strategy.project.id}" label="${strategy.project.name}"/>
			</form:select>  
            <form:errors path="project" cssClass="help-inline"/>
        </div>
    </div>
    <spring:bind path="strategy.name">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
    </spring:bind>
        <appfuse:label styleClass="control-label" key="strategy.name"/>
        <div class="controls">
            <form:input path="name" id="description" maxlength="255"  />
            <form:errors path="name" cssClass="help-inline"/>
        </div>
    </div>

    <spring:bind path="strategy.assumption">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
    </spring:bind>
        <appfuse:label styleClass="control-label" key="strategy.assumption"/>
        <div class="controls">
        	<form:textarea path="assumption" id="assumption" cols="200" rows="6" />
            <form:errors path="assumption" cssClass="help-inline"/>
        </div>
    </div>            
    <div class="form-actions">
        <button type="submit" class="btn btn-primary" name="save">
            <i class="icon-ok icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty strategy.id && strategy.goals.size() eq 0 && strategy.strategyOwner eq currentUser }">
          <button type="submit" class="btn" name="delete">
              <i class="icon-trash"></i> <fmt:message key="button.delete"/>
          </button>
        </c:if>
        <button type="submit" class="btn" name="cancel">
            <i class="icon-remove"></i> <fmt:message key="button.cancel"/>
        </button>
    </div>
    </form:form>
</div>
<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['strategyForm']).focus();
    });
</script>