
const viewBoxWidth = 1500;
const viewBoxHeight = 1500;

var svg = d3.select("svg");

svg.append("rect")
    .attr("width", viewBoxWidth)
    .attr("height", viewBoxHeight)
    .attr("fill", "#222"); 


const margin = { top: 80, right: 80, bottom: 80, left: 80 };
const g = svg.append("g").attr("transform", `translate(${margin.left},${margin.top})`);
    
var width = viewBoxWidth - margin.left - margin.right;
var height = viewBoxHeight - margin.top - margin.bottom;


var originalGraph = null; 
var baseGraph = null;
var iterationData = {};
var currentIteration = 1;
var simulation = null;
var link = null;
var node = null;
var nodeLabels = null;
var linkLabels = null;
var currentOutputFile = null;
var maxPheromone = 1; 
var minPheromone = 0;


var colorScale = d3.scaleLinear()
    .domain([0, 0.5, 1])
    .range(["blue", "cyan", "red"]);


function previousIteration() {
    if (currentIteration > 1) {
        currentIteration--;
        updateGraph();
        document.getElementById("iterationInfo").textContent = `Showing iteration ${currentIteration}`;
        document.getElementById("iterationSearch").value = currentIteration;
    }
}

function nextIteration() {
    if (Object.keys(iterationData).length === 0) return;
    const maxIteration = Math.max(...Object.keys(iterationData).map(Number));
    if (currentIteration < maxIteration) {
        currentIteration++;
        updateGraph();
        document.getElementById("iterationInfo").textContent = `Showing iteration ${currentIteration}`;
        document.getElementById("iterationSearch").value = currentIteration;
    }
}


window.addEventListener("keydown", (event) => {
    const key = event.key.toLowerCase();

    if (key === "a") {
        event.preventDefault();
        previousIteration();
    } else if (key === "d") {
        event.preventDefault();
        nextIteration();
    }
});




Promise.all([
    d3.json("resources/nodegraphd3.json"),
    d3.text("resources/output.txt").catch(() => null) // Handle missing file gracefully
]).then(function([graph, iterationText]) {
    console.log("Graph loaded:", graph);
    
    originalGraph = graph; // Store the original full graph
    baseGraph = graph;
    
   
    if (iterationText) {
        parseIterationData(iterationText, graph);
        filterAndRedrawGraph();
    }
    
    createGraph(baseGraph);
    
    
    fetch("/api/output-files")
        .then(res => res.json())
        .then(files => populateFileSelect(files))
        .catch(err => console.error("Error loading files:", err));
});

function parseIterationData(text, graph) {
    const lines = text.split('\n');
    
    
    const edgesInFile = new Set();
    let maxIterationInFile = 0;
    let dataPointsFound = 0;
    
    for (let i = 1; i < lines.length; i++) {
        const line = lines[i].trim();
        if (!line) continue;
        
        const parts = line.split(',').map(p => p.trim());
        if (parts.length < 3) continue;
        
        const edgeKey = parts[0];
        const iteration = parseInt(parts[1]);
        const pheromone = parseFloat(parts[2]);
        
        if (!isNaN(iteration) && !isNaN(pheromone)) {
            edgesInFile.add(edgeKey);
            maxIterationInFile = Math.max(maxIterationInFile, iteration);
            dataPointsFound++;
        }
    }
    
    console.log("Edges in file:", Array.from(edgesInFile).sort());
    console.log("Max iteration in file:", maxIterationInFile);
    console.log("Data points found:", dataPointsFound);
    
    if (dataPointsFound === 0) {
        console.warn("WARNING: No data points found in file!");
    }
    
    
    for (let i = 1; i <= maxIterationInFile; i++) {
        iterationData[i] = {};
        edgesInFile.forEach(edgeKey => {
            iterationData[i][edgeKey] = 0; // Default to 0, not 1
        });
    }
    
    
    for (let i = 1; i < lines.length; i++) {
        const line = lines[i].trim();
        if (!line) continue;
        
        const parts = line.split(',').map(p => p.trim());
        if (parts.length < 3) continue;
        
        const edgeKey = parts[0];
        const iteration = parseInt(parts[1]);
        const pheromone = parseFloat(parts[2]);
        
        if (!isNaN(iteration) && !isNaN(pheromone)) {
            if (!iterationData[iteration]) {
                iterationData[iteration] = {};
            }
            iterationData[iteration][edgeKey] = pheromone;
        }
    }
    
    console.log("Iteration data loaded:", Object.keys(iterationData).length, "iterations");
    console.log("Sample iteration 1:", iterationData[0]);
}

function getEdgeKey(link) {
    const source = typeof link.source === 'object' ? link.source.name : link.source;
    const target = typeof link.target === 'object' ? link.target.name : link.target;
    
    return `${source.toLowerCase()}${target.toLowerCase()}`;
}

function populateFileSelect(files) {
    const select = document.getElementById("fileSelect");
    select.innerHTML = ""; 
    
    files.forEach((file, index) => {
        const option = document.createElement("option");
        option.value = file;
        option.text = file.replace("output-", "").replace(".txt", "");
        select.appendChild(option);
        
        if (index === 0) {
            option.selected = true;
            currentOutputFile = file;
        }
    });
}

function filterAndRedrawGraph() {
    
    const usedNodeNames = new Set();
    const usedEdges = new Set();
    
    Object.values(iterationData).forEach(iteration => {
        Object.keys(iteration).forEach(edgeKey => {
            usedEdges.add(edgeKey); // Store the edge key directly
            
            
            for (let i = 0; i < edgeKey.length; i++) {
                usedNodeNames.add(edgeKey[i].toUpperCase());
            }
        });
    });
    
    console.log("Used nodes:", Array.from(usedNodeNames).sort());
    console.log("Used edges from file:", Array.from(usedEdges).sort());
    
    
    const filteredNodes = originalGraph.nodes
        .filter(n => usedNodeNames.has(n.name))
        .map(n => ({ name: n.name }));
    
   
    const filteredLinks = Array.from(usedEdges).map(edgeKey => {
        
        const source = edgeKey[0].toUpperCase();
        const target = edgeKey.substring(1).toUpperCase();
        
        
        const originalLink = originalGraph.links.find(l => 
            (l.source === source && l.target === target) ||
            (l.source === target && l.target === source)
        );
        
        return {
            source: source,
            target: target,
            distance: originalLink ? originalLink.distance : 10,
            pheromone: 1
        };
    });
    
    const filteredGraph = {
        nodes: filteredNodes,
        links: filteredLinks
    };
    
    console.log("Filtered graph - nodes:", filteredGraph.nodes.length, "links:", filteredGraph.links.length);
    console.log("Sample filtered links:", filteredGraph.links.slice(0, 3));
    
    baseGraph = filteredGraph;
    updateMaxPheromone();

   
}

function selectOutputFile() {
    const select = document.getElementById("fileSelect");
    const fileName = select.value;
    
    if (!fileName) return;
    
    currentOutputFile = fileName;
    document.getElementById("fileInfo").textContent = "Loading...";
    
    
    fetch(`/api/output-files/${fileName}`)
        .then(response => {
            if (!response.ok) throw new Error("Failed to fetch file");
            return response.text();
        })
        .then(iterationText => {
            if (!iterationText) {
                document.getElementById("fileInfo").textContent = "Error: Empty file";
                return;
            }
            
            console.log("Raw file content:", iterationText.substring(0, 200));
            
           
            if (simulation) {
                simulation.stop();
            }
            
           
            iterationData = {};
            currentIteration = 1;
            window.debugLookups = 0; // Reset debug counter
            
            console.log("Cleared iteration data, currentIteration set to 1");
            
            
            parseIterationData(iterationText, originalGraph);
            console.log("File loaded successfully, iterations:", Object.keys(iterationData).length);
            console.log("Sample iteration data:", iterationData[0]);
            console.log("Available iterations:", Object.keys(iterationData).slice(0, 10));
            
            
            if (!iterationData[0] || Object.keys(iterationData[0]).length === 0) {
                console.warn("WARNING: Iteration 1 has no pheromone data in new file!");
                
                const availableIterations = Object.keys(iterationData).map(Number).sort((a, b) => a - b);
                if (availableIterations.length > 0) {
                    currentIteration = availableIterations[0];
                    console.log("Setting currentIteration to first available:", currentIteration);
                }
            }
          
            filterAndRedrawGraph();
            
          
            document.getElementById("iterationSearch").value = "";
            
            const edgeCount = baseGraph.links.length;
            const iterCount = Object.keys(iterationData).length;
            const firstIterWithData = Object.keys(iterationData).map(Number).sort((a,b) => a-b)[0];
            document.getElementById("fileInfo").textContent = `Loaded: ${fileName} | Iterations: ${iterCount} | Edges: ${edgeCount} | First iter with data: ${firstIterWithData}`;
            document.getElementById("iterationInfo").textContent = `Showing iteration ${currentIteration}`;
            
            // Completely redraw the graph with filtered data
            g.selectAll("*").remove(); 
            createGraph(baseGraph);
            
            // Force update the visualization with the new data
            setTimeout(() => {
                console.log("Updating visualization, iteration data:", iterationData);
                console.log("Current iteration:", currentIteration);
                
                // Show first few edges and their data directly on page
                if (baseGraph.links.length > 0) {
                    let edgeSample = "";
                    for (let i = 0; i < Math.min(3, baseGraph.links.length); i++) {
                        const link = baseGraph.links[i];
                        const key = getEdgeKey(link);
                        const value = iterationData[currentIteration] ? iterationData[currentIteration][key] : "NO_KEY";
                        edgeSample += `${key}=${value} `;
                    }
                    const iterData = iterationData[currentIteration] || {};
                    const info = document.getElementById("fileInfo").textContent + ` | SAMPLE_EDGES: ${edgeSample} | DATA_KEYS: ${Object.keys(iterData).slice(0,5).join(',')}`;
                    document.getElementById("fileInfo").textContent = info;
                }
                
                window.debugLookups = 0; // Reset for next update
                updateGraphWidths();
                updateGraphColors();
                updatePheromoneLabels();
            }, 500);
        })
        .catch(err => {
            console.error("Error loading file:", err);
            document.getElementById("fileInfo").textContent = "Error loading file: " + err.message;
        });
}



function searchIteration() {
    const input = document.getElementById("iterationSearch");
    const iteration = parseInt(input.value);
    
    if (!isNaN(iteration) && iteration > 0) {
        currentIteration = iteration;
        updateGraph();
        document.getElementById("iterationInfo").textContent = `Showing iteration ${currentIteration}`;
    } else {
        alert("Please enter a valid iteration number");
    }
}

function updateScale() {
    const input = document.getElementById("scaleInput");
    scaleFactorPheromone = parseFloat(input.value) || 10;
    updateGraphWidths();
    updateGraphColors();
}

function createGraph(graph) {
   
    graph.nodes.forEach(node => {
        node.x = Math.random() * width;
        node.y = Math.random() * height;
    });
    
    simulation = d3
        .forceSimulation(graph.nodes)
        .force(
            "link",
            d3.forceLink(graph.links)
                .id(d => d.name)
                .distance(d => d.distance * 30)
        )
        .force("charge", d3.forceManyBody().strength(-600))
        .force("center", d3.forceCenter(width / 2, height / 2))
        .on("tick", ticked);
    
    
    simulation.alpha(1).restart();

    link = g
        .append("g")
        .selectAll("line")
        .data(graph.links)
        .enter()
        .append("line")
        .attr("stroke-width", d => calculateLineWidth(d))
        .style("stroke", d => calculateLineColor(d));

    node = g
        .append("g")
        .selectAll("circle")
        .data(graph.nodes)
        .enter()
        .append("circle")
        .attr("r", 30)
        .attr("fill", d => getNodeColor(d.name));

    nodeLabels = g
        .append("g")
        .selectAll("text")
        .data(graph.nodes)
        .enter()
        .append("text")
        .attr("font-size", 20)
        .attr("fill", "white")
        .attr("text-anchor", "middle")
        .attr("font-weight", "bold")
        .attr("dy", 6)
        .text(d => d.name);

    linkLabels = g
        .append("g")
        .selectAll("g")
        .data(graph.links)
        .enter()
        .append("g");

    var pheromoneLabel = linkLabels
        .append("text")
        .attr("font-size",20)
        .attr("fill", "yellow")
        .attr("text-anchor", "middle")
        .attr("dy", "-7")
        .attr("class", "pheromone-label")
        .text(d => getPheromoneForIteration(d).toFixed(2));

    var distanceLabel = linkLabels
        .append("text")
        .attr("font-size",20)
        .attr("fill", "cyan")
        .attr("text-anchor", "middle")
        .attr("dy", "14")
        .text(d => d.distance);

    function ticked() {
        link
            .attr("x1", d => d.source.x)
            .attr("y1", d => d.source.y)
            .attr("x2", d => d.target.x)
            .attr("y2", d => d.target.y);

        node
            .attr("cx", d => d.x = Math.max(24, Math.min(width - 24, d.x)))
            .attr("cy", d => d.y = Math.max(24, Math.min(height - 24, d.y)));

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

function getPheromoneForIteration(linkData) {
    if (!iterationData[currentIteration]) {
        return 0;
    }
    
    const edgeKey = getEdgeKey(linkData);
    const pheromone = iterationData[currentIteration][edgeKey];
    
    
    if (!window.debugLookups) window.debugLookups = 0;
    if (window.debugLookups < 3) {
        const availableKeys = Object.keys(iterationData[currentIteration] || {});
        const edgeExists = edgeKey in (iterationData[currentIteration] || {});
        console.log(`[Lookup ${window.debugLookups}] Key: "${edgeKey}", Exists: ${edgeExists}, Value: ${pheromone}, Total keys: ${availableKeys.length}, Sample keys: ${availableKeys.slice(0,3).join(',')}`);
        window.debugLookups++;
    }
    
    return pheromone || 0;
}

function calculateLineWidth(linkData) {
    const pheromone = getPheromoneForIteration(linkData);
    
    if (maxPheromone === 0) return 1;
    const normalizedWidth = (pheromone / maxPheromone) * 15 + 1;
    return Math.max(1, normalizedWidth);
}

function calculateLineColor(linkData) {
    const pheromone = getPheromoneForIteration(linkData);
    if (pheromone === 1) return "#cccccc";
    
    if (maxPheromone === 0) return "blue";
    const normalized = pheromone / maxPheromone;
    return colorScale(normalized);
}


function updateMaxPheromone() {
   
    let max = 0;
    let min = Infinity;
    Object.values(iterationData).forEach(iteration => {
        Object.values(iteration).forEach(pheromone => {
            if (pheromone > max) max = pheromone;
            if (pheromone < min) min = pheromone;
        });
    });
    maxPheromone = max > 0 ? max : 1;
    minPheromone = min;
    colorScale.domain([minPheromone, (minPheromone + maxPheromone) / 2, maxPheromone]);
    console.log("Data range: min=", minPheromone, "max=", maxPheromone);
}

function getNodeColor(nodeName) {
    if (nodeName === "A") {
        return "red"; // Home node (start)
    } else if (nodeName === "Z") {
        return "green"; // Food node (end)
    }
    return "steelblue"; // Regular node
}

function updateGraph() {
    if (!link) return;
    
    updateGraphWidths();
    updateGraphColors();
    updatePheromoneLabels();
}

function updateGraphWidths() {
    if (!link) return;
    
    link.attr("stroke-width", d => calculateLineWidth(d));
}

function updateGraphColors() {
    if (!link) return;
    
    link.style("stroke", d => calculateLineColor(d));
}

function updatePheromoneLabels() {
    if (!linkLabels) return;
    
    linkLabels.select("text.pheromone-label")
        .text(d => getPheromoneForIteration(d).toFixed(2));
}