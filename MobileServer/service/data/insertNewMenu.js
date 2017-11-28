const dotenv = require('dotenv').load();
const dataURL = process.env.DATABASE_URL;
const admin = require('firebase-admin');
const serviceAccount = require('../../file.json');

//initialize firebase
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: dataURL
});

var defaultAuth = admin.auth();
var defaultDatabase = admin.database();

var ref = defaultDatabase.ref();

module.exports = (req, res) => {
    var body = req.body;

    var timestamp = new Date().getMinutes();
    ref.child('timestamp').set(timestamp);

    res.status(200).send("success");
};