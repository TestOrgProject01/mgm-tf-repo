const express = require('express');
const bodyParser = require('body-parser');
const sqlite3 = require('sqlite3').verbose();
const crypto = require('crypto');
const fs = require('fs');
const path = require('path');
const child_process = require('child_process');

const app = express();
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

// Hardcoded secrets (Secrets in Code)
const API_KEY = "12345-abcdef-67890-ghijkl";
const DATABASE_PASSWORD = "password123";

// Vulnerable SQLite database for demonstration
const DATABASE_FILE = "vulnerable.db";

// Default credentials
const DEFAULT_USERNAME = "admin";
const DEFAULT_PASSWORD = "admin";

// Initialize a vulnerable SQLite database
function initializeDatabase() {
    const db = new sqlite3.Database(DATABASE_FILE);
    db.serialize(() => {
        db.run("CREATE TABLE IF NOT EXISTS users (username TEXT, password TEXT)");
        db.run("INSERT INTO users (username, password) VALUES ('admin', 'password123')");
    });
    db.close();
}

// // Home route displaying API key
// app.get('/', (req, res) => {
//     res.send(`<h1>Welcome! Your API Key is ${API_KEY}</h1>`);
// });

// // Vulnerable login endpoint (SQL Injection)
// app.post('/login', (req, res) => {
//     const username = req.body.username;
//     const password = req.body.password;
//     const query = `SELECT * FROM users WHERE username = '${username}' AND password = '${password}';`; // Vulnerable to SQL Injection
//     console.log(`Executing Query: ${query}`);
//     const db = new sqlite3.Database(DATABASE_FILE);
//     db.get(query, (err, row) => {
//         if (err) {
//             res.status(500).send("Database error");
//         } else if (row) {
//             res.send(`<h1>Welcome, ${username}!</h1>`);
//         } else {
//             res.send("<h1>Invalid credentials</h1>");
//         }
//     });
//     db.close();
// });

// // Vulnerable to XSS
// app.get('/xss', (req, res) => {
//     const query = req.query.query || '';
//     res.send(`You searched for: ${query}`); // Vulnerable to XSS
// });

// // Vulnerable to insecure deserialization
// app.post('/deserialize', (req, res) => {
//     const data = req.body.data;
//     const obj = JSON.parse(data); // Unsafe deserialization
//     res.send(`Deserialized object: ${JSON.stringify(obj)}`);
// });

// Vulnerable to weak hashing algorithm
app.post('/hash', (req, res) => {
    const password = req.body.password || '';
    const hashed = crypto.createHash('md5').update(password).digest('hex'); // Using weak hashing algorithm (MD5)
    res.send(`Hashed password: ${hashed}`);
});

// // Vulnerable to command injection
// app.post('/delete', (req, res) => {
//     const filename = req.body.filename || '';
//     child_process.exec(`rm -rf ${filename}`, (err) => { // Vulnerable to command injection
//         if (err) {
//             res.status(500).send("Error deleting file");
//         } else {
//             res.send("File deleted successfully!");
//         }
//     });
// });

// // Vulnerable to open redirect
// app.get('/redirect', (req, res) => {
//     const target = req.query.url || '/';
//     res.redirect(target); // Unvalidated redirect
// });

// // Improper input validation
// app.post('/discount', (req, res) => {
//     const price = parseFloat(req.body.price || 0);
//     const discount_percentage = parseFloat(req.body.discount || 0);
//     const total = price - (price * (discount_percentage / 100)); // No validation for negative inputs
//     res.send(`Total after discount: ${total}`);
// });

// // Vulnerable to unrestricted file upload
// app.post('/upload', (req, res) => {
//     const file = req.files.file;
//     const uploadPath = path.join(__dirname, 'uploads', file.name);
//     file.mv(uploadPath, (err) => { // No validation for file type
//         if (err) {
//             res.status(500).send("Error uploading file");
//         } else {
//             res.send("File uploaded successfully!");
//         }
//     });
// });

// // Vulnerable to default credentials
// app.post('/auth', (req, res) => {
//     const username = req.body.username || '';
//     const password = req.body.password || '';
//     if (username === DEFAULT_USERNAME && password === DEFAULT_PASSWORD) {
//         res.send("Authenticated as admin");
//     } else {
//         res.send("Authentication failed");
//     }
// });

// Vulnerable to path traversal
app.get('/read', (req, res) => {
    const filename = req.query.filename || '';
    const filePath = path.join(__dirname, 'uploads', filename);
    fs.readFile(filePath, 'utf8', (err, data) => { // No validation for file path
        if (err) {
            res.status(500).send("Error reading file");
        } else {
            res.send(data);
        }
    });
});

// Vulnerable to Regular Expression Denial of Service (ReDoS)
app.post('/regex', (req, res) => {
    const input = req.body.input || '';
    const pattern = /(a+)+$/; // Vulnerable regex pattern
    if (pattern.test(input)) {
        res.send("Input matched!");
    } else {
        res.send("No match");
    }
});

// Initialize database and start the Express app
initializeDatabase();
app.listen(3000, () => {
    console.log("Server is running on http://localhost:3000");
});