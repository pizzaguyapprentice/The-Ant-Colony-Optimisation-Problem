const express = require("express");
const path = require("path");
const fs = require("fs");

const app = express();
const PORT = 6767;

app.use(express.static(path.join(__dirname)));
app.use("/resources", express.static(path.join(__dirname, "../resources")));

// Endpoint to get the latest output file
app.get("/resources/output.txt", (req, res) => {
    const resultsDir = path.join(__dirname, "../resources/results");
    
    // If a specific file is requested as query parameter
    if (req.query.file) {
        const filePath = path.join(resultsDir, req.query.file);
        return res.sendFile(filePath);
    }
    
    // Otherwise, get the most recent file
    fs.readdir(resultsDir, (err, files) => {
        if (err) {
            return res.status(404).send("No output files found");
        }
        
        files = files.filter(f => f.startsWith("output-") && f.endsWith(".txt"));
        
        if (files.length === 0) {
            return res.status(404).send("No output files found");
        }
        
        // Sort by modification time, most recent first
        files.sort((a, b) => {
            const statA = fs.statSync(path.join(resultsDir, a));
            const statB = fs.statSync(path.join(resultsDir, b));
            return statB.mtime - statA.mtime;
        });
        
        res.sendFile(path.join(resultsDir, files[0]));
    });
});

// Endpoint to get a specific output file
app.get("/api/output-files/:filename", (req, res) => {
    const resultsDir = path.join(__dirname, "../resources/results");
    const filePath = path.join(resultsDir, req.params.filename);
    
    // Security: make sure the file is in the results directory
    if (!filePath.startsWith(resultsDir)) {
        return res.status(403).send("Forbidden");
    }
    
    res.sendFile(filePath);
});

// Endpoint to get list of available output files
app.get("/api/output-files", (req, res) => {
    const resultsDir = path.join(__dirname, "../resources/results");
    
    fs.readdir(resultsDir, (err, files) => {
        if (err) {
            return res.json([]);
        }
        
        files = files.filter(f => f.startsWith("output-") && f.endsWith(".txt"));
        
        // Sort by modification time, most recent first
        files.sort((a, b) => {
            const statA = fs.statSync(path.join(resultsDir, a));
            const statB = fs.statSync(path.join(resultsDir, b));
            return statB.mtime - statA.mtime;
        });
        
        res.json(files);
    });
});

app.listen(PORT, () => {
    console.log(`Server running at http://localhost:${PORT}`);
});
