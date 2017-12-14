const dotenv = require('dotenv').load();
const dataURL = process.env.DATABASE_URL;
const admin = require('firebase-admin');
const serviceAccount = require('../../file.json');

var defaultAuth = admin.auth();
var defaultDatabase = admin.database();

var ref = defaultDatabase.ref();

module.exports = (req, res) => {
    var name = req.body.name;
    var email = req.body.email;
    var password = req.body.password;

    admin.auth().createCustomToken

    admin.auth().getUserByEmail(email).then((authData) => {
        console.log(authData);
        if(password == authData.displayName){
            var loggedIn = {status: "success", uid: authData.uid};
            res.status(200).send(JSON.stringify(loggedIn));
        } else {
            var userNotFound = {status: "error", error: "Username or Password cannot be found!"};
            res.status(200).send(JSON.stringify(userNotFound));
        }
    }).catch((error) => {
        var errorCode = error.code;
        var errorMessage = error.message;

        console.log(errorMessage);
        var toSend = {status: "error", error: errorMessage};
        res.status(200).send(JSON.stringify(toSend));
    });
};