var svg = d3.select("svg");
var width = svg.attr("width");
var height = svg.attr("height");

var graph = {

    nodes: [
        {name: "A"},
        {name: "B"},
        {name: "C"},
        {name: "D"},
        {name: "E"},
        {name: "F"},
        {name: "G"},
    ],
    links: [
        {source:"A", target: "B"},
        {source:"A", target: "C"},
        {source:"A", target: "D"},
        {source:"B", target: "C"},
        {source:"B", target: "F"},
        {source:"B", target: "A"},
        {source:"C", target: "A"},
        {source:"C", target: "B"},
        {source:"C", target: "F"},
        {source:"C", target: "E"},
        {source:"D", target: "A"},
        {source:"D", target: "F"},
        {source:"E", target: "C"},
        {source:"E", target: "F"},
        {source:"E", target: "G"},
        {source:"F", target: "D"},
        {source:"F", target: "C"},
        {source:"F", target: "B"},
        {source:"F", target: "E"},
        {source:"F", target: "G"},
        {source:"G", target: "F"},
        {source:"G", target: "E"},
    ]
};

function drawNodeGraph(data){
    document.getElementsByTagName

    const width = 600;
    const height = 600;
    const margin = {top: 10, right: 10, bottom: 10, left:10};

    const plot_height = height - margin-top - margin.bottom;
    const plot_width = width - margin.left - margin.right;

    const canvas = d3.select("#canvas")
    .append("svg")
    .style("background", "aliceblue")
    .attr("height", height)
    .attr("width", width);

    const plot = canvas.append("g")
    .attr ("transform",'translate(${margin.left},$')
}

var simulation = d3
    .forceSimulation(graph.nodes)
    .force(
        "link",
        d3.forceLink(graph.links).id(function(d){
            return d.name;
        })
    )
    .force("charge", d3.forceManyBody().strength(-30))
    .force("center", d3.forceCenter(width/2, height /2))
    .on("tick",ticked);

var link = svg
    .append("g")
    .selectAll("line")
    .data(graph.links)
    .enter()
    .append("line")
    .attr("stroke-width", function(d){
        return 3;
    })
    .style("stroke","pink");