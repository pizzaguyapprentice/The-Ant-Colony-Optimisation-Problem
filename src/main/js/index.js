var svg = d3.select("svg")
    .attr("width",1000)
    .attr("height",1000);
    
var width = +svg.attr("width");
var height = +svg.attr("height");

d3.json("resources/nodegraphd3.json").then(function(graph) {
    console.log(graph);
    createGraph(graph);
});


function createGraph(graph){


    var simulation = d3
        .forceSimulation(graph.nodes)
        .force(
            "link",
            d3.forceLink(graph.links)
                .id(d => d.name)
                .distance(d => d.distance *10)
        )
        .force("charge", d3.forceManyBody().strength(-400))
        .force("center", d3.forceCenter(width / 2, height / 2))
        .on("tick", ticked);

    var link = svg
        .append("g")
        .selectAll("line")
        .data(graph.links)
        .enter()
        .append("line")
        .attr("stroke-width", 2)
        .style("stroke","pink");

    var node = svg
        .append("g")
        .selectAll("circle")
        .data(graph.nodes)
        .enter()
        .append("circle")
        .attr("r", 16)
        .attr("fill", "steelblue");

    var nodeLabels = svg
    .append("g")
    .selectAll("text")
    .data(graph.nodes)
    .enter()
    .append("text")
    .attr("font-size", 12)
    .attr("fill", "white")
    .attr("text-anchor", "middle")
    .attr("font-weight","bold")
    .attr("dy", 4)
    .text(d => d.name)

    var linkLabels = svg
    .append("g")
    .selectAll("g")
    .data(graph.links)
    .enter()
    .append("g");

    var pheromoneLabel = linkLabels
    .append("text")
    .attr("font-size", 11)
    .attr("fill", "purple")
    .attr("text-anchor", "middle")
    .attr("dy", "-5")
    .text(d => d.pheromone);

    var distanceLabel = linkLabels
    .append("text")
    .attr("font-size", 11)
    .attr("fill", "green")
    .attr("text-anchor", "middle")
    .attr("dy", "10")
    .text(d => d.distance);

    function ticked() {
        link
            .attr("x1", d => d.source.x)
            .attr("y1", d => d.source.y)
            .attr("x2", d => d.target.x)
            .attr("y2", d => d.target.y);

        node
            .attr("cx", d => d.x = Math.max(8, Math.min(width - 8, d.x)))
            .attr("cy", d => d.y = Math.max(8, Math.min(height - 8, d.y)));

        linkLabels
        .attr("transform", d => {
            var x = (d.source.x + d.target.x) / 2;
            var y = (d.source.y + d.target.y) / 2;
            return `translate(${x},${y})`;
        });
        nodeLabels
            .attr("x", d => d.x)
            .attr("y", d => d.y);

    }
}