<%@ include file="/common/taglibs.jsp"%>
<head>
    <title><fmt:message key="goalDetail.title"/></title>
    <meta name="menu" content="DefinitionPhaseMenu"/>
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

<body>

<div id="json_tree"></div>

<script>

var url = "gridhelp";
$.post(url, 
		function(responseText) {
			var treeData = [];
			var html = "";
			var json_tree = "";
			
			html = $.parseHTML(responseText);
			json_tree = $(html).find("#json_tree").text().trim();
			treeData.push(JSON.parse(json_tree));
			
			// ************** Generate the tree diagram *****************
			var margin = {top: 40, right: 120, bottom: 20, left: 120},
			width = 960 - margin.right - margin.left,
			height = 500 - margin.top - margin.bottom;
			var i = 0;
			 
			var tree = d3.layout.tree()
			.size([height, width]);
			 
			var diagonal = d3.svg.diagonal()
			.projection(function(d) { return [d.x, d.y]; });
			 
			var svg = d3.select("#json_tree").append("svg")
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
			
		},
"html");
 
</script>
    

</body>

