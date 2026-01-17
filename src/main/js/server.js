const express = require("express");
const path = require("path");

const app = express();
const PORT = 6767;

app.use(express.static(path.join(__dirname)));
app.use("/resources", express.static(path.join(__dirname, "../resources")));

app.listen(PORT, () => {
    console.log(`Server running at http://localhost:${PORT}`);
});
