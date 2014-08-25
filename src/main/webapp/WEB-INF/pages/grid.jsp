<%@ include file="/common/taglibs.jsp"%>
<head>
    <title><fmt:message key="goalDetail.title"/></title>
    <meta name="menu" content="DefinitionPhaseMenu"/>
    <script type="text/javascript" src="../../scripts/d3/d3.min.js"></script>
    <script type="text/javascript" src="../../scripts/d3/d3.layout.js"></script>
    <script src="http://d3js.org/d3.v3.min.js"></script>
     <style>
 
		.node circle {
		fill: #fff;
		stroke: steelblue;
		stroke-width: 3px;
		}
		 
		.node text { font: 12px sans-serif; }
		 
		.link {
		fill: none;
		stroke: #ccc;
		stroke-width: 2px;
		}
	</style>
</head>
 <!-- 
<div class="span2">
    <h2><fmt:message key='goal.heading'/></h2>
</div>
-->
<body>


<script>
<c:set var="tree" scope="page" value="${tree}"/>

<% String s = (String) request.getAttribute("tree"); %>
<% 
	String tree = (String) pageContext.getAttribute("tree");
	out.println(tree);
%>

 /*
d3.json("tree.json", function(json) {
	  var nodes = tree.nodes(json);
} 
*/
/*
var treeData = 	[
					{
					"name": "Top Level",
					"parent": "null",
					"children": [
									{
									"name": "Level 2: A",
									"parent": "Top Level",
									"children": [
									{
									"name": "Son of A",
									"parent": "Level 2: A"
									},
									{
									"name": "Daughter of A",
									"parent": "Level 2: A"
									}
								]
					},
					{
					"name": "Level 2: B",
					"parent": "Top Level"
					},
					{
					"name": "Level 2: B",
					"parent": "Top Level"
					}
				]
	}
];
var treeData = [{"name":"G1","parent":"null", "children":[{"name":"S3","parent":"G1", "children":[{"name":"G14","parent":"S3", "children":[{"name":"S7","parent":"G14", "children":[{"name":"G18","parent":"S7"}]}]}]},{"name":"S4","parent":"G1", "children":[{"name":"S5","parent":"S4", "children":[{"name":"G15","parent":"S5", "children":[{"name":"G19","parent":"G15"},{"name":"G20","parent":"G15"}]}]},{"name":"S6","parent":"S4", "children":[{"name":"G17","parent":"S6"},{"name":"G16","parent":"S6", "children":[{"name":"S8","parent":"G16", "children":[{"name":"G21","parent":"S8"}]}]}]}]}]}];
*/

var obj = jQuery.parseJSON( '${tree}' );

var treeData = [""];

// ************** Generate the tree diagram *****************
var margin = {top: 40, right: 120, bottom: 20, left: 120},
width = 960 - margin.right - margin.left,
height = 500 - margin.top - margin.bottom;
var i = 0;
 
var tree = d3.layout.tree()
.size([height, width]);
 
var diagonal = d3.svg.diagonal()
.projection(function(d) { return [d.x, d.y]; });
 
var svg = d3.select("body").append("svg")
.attr("width", width + margin.right + margin.left)
.attr("height", height + margin.top + margin.bottom)
.append("g")
.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
 
root = treeData[0];
update(root);
 
function update(source) {
 
// Compute the new tree layout.
var nodes = tree.nodes(root).reverse(),
links = tree.links(nodes);
 
// Normalize for fixed-depth.
nodes.forEach(function(d) { d.y = d.depth * 60; });
 
// Declare the nodes
var node = svg.selectAll("g.node")
.data(nodes, function(d) { return d.id || (d.id = ++i); });
 
// Enter the nodes.
var nodeEnter = node.enter().append("g")
.attr("class", "node")
.attr("transform", function(d) {
return "translate(" + d.x + "," + d.y + ")"; });
 
nodeEnter.append("circle")
.attr("r", 10)
.style("fill", "#fff");
 
nodeEnter.append("text")
.attr("y", function(d) {
return d.children || d._children ? -18 : 18; })
.attr("dy", ".35em")
.attr("text-anchor", "middle")
.text(function(d) { return d.name; })
.style("fill-opacity", 1);
 
// Declare the links
var link = svg.selectAll("path.link")
.data(links, function(d) { return d.target.id; });
 
// Enter the links.
link.enter().insert("path", "g")
.attr("class", "link")
.attr("d", diagonal);
 
}
 
</script>
    

</body>

