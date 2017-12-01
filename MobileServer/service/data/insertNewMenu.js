const dotenv = require('dotenv').load();
const dataURL = process.env.DATABASE_URL;
const admin = require('firebase-admin');
const serviceAccount = require('../../file.json');

var defaultAuth = admin.auth();
var defaultDatabase = admin.database();

var ref = defaultDatabase.ref();

module.exports = (req, res) => {
    var body = req.body;

    //replace the below code with any data transaction we deem necessary
    var timestamp = new Date().getMinutes();
    ref.child('timestamp').set(timestamp);

    res.status(200).send("success");
};