const dotenv = require('dotenv').load();
const dataURL = process.env.DATABASE_URL;
const admin = require('firebase-admin');
const serviceAccount = require('../../file.json');

var defaultAuth = admin.auth();
var defaultDatabase = admin.database();

var ref = defaultDatabase.ref();

module.exports = (req, res) => {
    var title = req.body.title;
    console.log(title);
    ref.once('value', (snapshot) => {
        var ingredients = snapshot.child(title + "/ingredients").val();
        var time = snapshot.child(title + "/time").val();  
        var dict = {"instructions" : ingredients, "time": time};
        res.status(200).send(dict);
    });
};