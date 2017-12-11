const admin = require('firebase-admin');
const serviceAccount = require('./file.json');
const dotenv = require('dotenv').load();
const ip = require('ip');
const dataURL = process.env.DATABASE_URL;

//initialize firebase
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: dataURL
});

const express = require('express');
const bodyParser = require('body-parser');
const service = require('./service/index');

//initialize express
const app = express();

//Get the port number
const PORT = process.env.PORT

//Support json encoded bodies and support encoded bodies
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true}));

//serve all static files
app.use(express.static('public'));

//check health of the server
app.get('/serverstatus', (req, res) => {
    res.end('Server is alive!');
});

//Routers to all post and get requests
app.use('/service', service);

//START SERVER
app.listen(PORT, ()=>{
    console.log("Server listening at %s on Port: %s", ip.address(), PORT);
});
