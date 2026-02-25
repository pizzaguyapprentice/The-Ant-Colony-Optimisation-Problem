var svg = d3.select("svg")
    .attr("width", 1000)
    .attr("height", 1000);
    
var width = +svg.attr("width");
var height = +svg.attr("height");

// Global state
var baseGraph = null;
var iterationData = {};
var currentIteration = 1;
var simulation = null;
var link = null;
var node = null;
var nodeLabels = null;
var linkLabels = null;
var currentOutputFile = null;
var maxPheromone = 1; // Track max pheromone for automatic scaling
var minPheromone = 0;

// Create color scale for pheromone levels
var colorScale = d3.scaleLinear()
    .domain([0, 0.5, 1])
    .range(["blue", "cyan", "red"]);

// Load both the base graph and iteration data
Promise.all([
    d3.json("resources/nodegraphd3.json"),
    d3.text("resources/output.txt").catch(() => null) // Handle missing file gracefully
]).then(function([graph, iterationText]) {
    console.log("Graph loaded:", graph);
    
    baseGraph = graph;
    
    // Parse iteration data if available
    if (iterationText) {
        parseIterationData(iterationText, graph);
        
        // Extract unique nodes from the parsed data
        const usedNodeNames = new Set();
        Object.values(iterationData).forEach(iteration => {
            Object.keys(iteration).forEach(edgeKey => {
                for (let i = 0; i < edgeKey.length; i++) {
                    usedNodeNames.add(edgeKey[i].toUpperCase());
                }
            });
        });
        
        console.log("Used nodes:", Array.from(usedNodeNames).sort());
        
        // Filter the graph to only include used nodes and edges between them
        const filteredGraph = {
            nodes: baseGraph.nodes.filter(n => usedNodeNames.has(n.name)),
            links: baseGraph.links.filter(link => {
                const source = link.source;
                const target = link.target;
                return usedNodeNames.has(source) && usedNodeNames.has(target);
            })
        };
        
        console.log("Filtered graph - nodes:", filteredGraph.nodes.length, "links:", filteredGraph.links.length);
        
        baseGraph = filteredGraph;
        updateMaxPheromone();
        populateEveryTenDropdown();
    }
    
    createGraph(baseGraph);
    
    // Load available output files after graph is created
    fetch("/api/output-files")
        .then(res => res.json())
        .then(files => populateFileSelect(files))
        .catch(err => console.error("Error loading files:", err));
});

function parseIterationData(text, graph) {
    const lines = text.split('\n');
    
    // First pass: collect all unique edges actually in the file
    const edgesInFile = new Set();
    for (let i = 1; i < lines.length; i++) {
        const line = lines[i].trim();
        if (!line) continue;
        
        const parts = line.split(',').map(p => p.trim());
        if (parts.length < 3) continue;
        
        const edgeKey = parts[0];
        edgesInFile.add(edgeKey);
    }
    
    console.log("Edges in file:", Array.from(edgesInFile).sort());
    
    // Initialize iteration data - only for edges that exist in the file
    for (let i = 1; i <= 1000; i++) {
        iterationData[i] = {};
        edgesInFile.forEach(edgeKey => {
            iterationData[i][edgeKey] = 1; // Default pheromone
        });
    }
    
    // Parse the output file and populate with actual values
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
}

function getEdgeKey(link) {
    const source = typeof link.source === 'object' ? link.source.name : link.source;
    const target = typeof link.target === 'object' ? link.target.name : link.target;
    // Format: "ab" (lowercase, no dash) to match CSV format
    return `${source.toLowerCase()}${target.toLowerCase()}`;
}

function populateFileSelect(files) {
    const select = document.getElementById("fileSelect");
    select.innerHTML = ""; // Clear existing options
    
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

function selectOutputFile() {
    const select = document.getElementById("fileSelect");
    const fileName = select.value;
    
    if (!fileName) return;
    
    currentOutputFile = fileName;
    document.getElementById("fileInfo").textContent = "Loading...";
    
    // Load the selected file using fetch
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
            
            // Clear and reset iteration data
            iterationData = {};
            currentIteration = 1;
            
            // Parse the new file
            parseIterationData(iterationText, baseGraph);
            console.log("File loaded successfully, iterations:", Object.keys(iterationData).length);
            console.log("Sample iteration data:", iterationData[1]);
            
            // Extract unique nodes from the parsed data
            const usedNodeNames = new Set();
            Object.values(iterationData).forEach(iteration => {
                Object.keys(iteration).forEach(edgeKey => {
                    // edgeKey is like "ab" so split it
                    for (let i = 0; i < edgeKey.length; i++) {
                        usedNodeNames.add(edgeKey[i].toUpperCase());
                    }
                });
            });
            
            console.log("Used nodes:", Array.from(usedNodeNames).sort());
            
            // Filter the graph to only include used nodes and edges between them
            const filteredGraph = {
                nodes: baseGraph.nodes.filter(n => usedNodeNames.has(n.name)),
                links: baseGraph.links.filter(link => {
                    const source = link.source;
                    const target = link.target;
                    return usedNodeNames.has(source) && usedNodeNames.has(target);
                })
            };
            
            console.log("Filtered graph - nodes:", filteredGraph.nodes.length, "links:", filteredGraph.links.length);
            
            // Update the max pheromone for color scaling based on actual data
            updateMaxPheromone();
            
            // Reset UI
            populateEveryTenDropdown();
            document.getElementById("everyTenSelect").value = "";
            document.getElementById("iterationSearch").value = "";
            document.getElementById("fileInfo").textContent = `Loaded: ${fileName}`;
            document.getElementById("iterationInfo").textContent = "Showing iteration 1";
            
            // Completely redraw the graph with filtered data
            svg.selectAll("*").remove(); // Clear the entire SVG
            createGraph(filteredGraph);
        })
        .catch(err => {
            console.error("Error loading file:", err);
            document.getElementById("fileInfo").textContent = "Error loading file: " + err.message;
        });
}

function populateEveryTenDropdown() {
    const select = document.getElementById("everyTenSelect");
    select.innerHTML = '<option value="">Custom</option>'; // Reset
    const iterations = Object.keys(iterationData).map(Number).filter(i => i % 10 === 0).sort((a, b) => a - b);
    
    iterations.forEach(iter => {
        const option = document.createElement("option");
        option.value = iter;
        option.text = `Iteration ${iter}`;
        select.appendChild(option);
    });
}

function selectEveryTen() {
    const select = document.getElementById("everyTenSelect");
    if (select.value) {
        currentIteration = parseInt(select.value);
        updateGraph();
        document.getElementById("iterationInfo").textContent = `Showing iteration ${currentIteration}`;
    }
}

function searchIteration() {
    const input = document.getElementById("iterationSearch");
    const iteration = parseInt(input.value);
    
    if (!isNaN(iteration) && iteration > 0) {
        currentIteration = iteration;
        document.getElementById("everyTenSelect").value = "";
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
    simulation = d3
        .forceSimulation(graph.nodes)
        .force(
            "link",
            d3.forceLink(graph.links)
                .id(d => d.name)
                .distance(d => d.distance * 10)
        )
        .force("charge", d3.forceManyBody().strength(-400))
        .force("center", d3.forceCenter(width / 2, height / 2))
        .on("tick", ticked);

    link = svg
        .append("g")
        .selectAll("line")
        .data(graph.links)
        .enter()
        .append("line")
        .attr("stroke-width", d => calculateLineWidth(d))
        .style("stroke", d => calculateLineColor(d));

    node = svg
        .append("g")
        .selectAll("circle")
        .data(graph.nodes)
        .enter()
        .append("circle")
        .attr("r", 16)
        .attr("fill", "steelblue");

    nodeLabels = svg
        .append("g")
        .selectAll("text")
        .data(graph.nodes)
        .enter()
        .append("text")
        .attr("font-size", 12)
        .attr("fill", "white")
        .attr("text-anchor", "middle")
        .attr("font-weight", "bold")
        .attr("dy", 4)
        .text(d => d.name);

    linkLabels = svg
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
        .attr("class", "pheromone-label")
        .text(d => getPheromoneForIteration(d).toFixed(2));

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

function getPheromoneForIteration(linkData) {
    if (!iterationData[currentIteration]) {
        return linkData.pheromone || 0;
    }
    
    const edgeKey = getEdgeKey(linkData);
    return iterationData[currentIteration][edgeKey] || linkData.pheromone || 1;
}

function calculateLineWidth(linkData) {
    const pheromone = getPheromoneForIteration(linkData);
    // Auto-scale width based on max pheromone
    if (maxPheromone === 0) return 1;
    const normalizedWidth = (pheromone / maxPheromone) * 15 + 1;
    return Math.max(1, normalizedWidth);
}

function calculateLineColor(linkData) {
    const pheromone = getPheromoneForIteration(linkData);
    if (pheromone === 1) return "#cccccc";
    // Normalize pheromone to 0-1 scale
    if (maxPheromone === 0) return "blue";
    const normalized = pheromone / maxPheromone;
    return colorScale(normalized);
}


function updateMaxPheromone() {
    // Calculate min and max pheromone across all iterations
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