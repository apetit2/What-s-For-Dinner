const dotenv = require('dotenv').load();
const dataURL = process.env.DATABASE_URL;
const admin = require('firebase-admin');
const serviceAccount = require('../../file.json');

var defaultAuth = admin.auth();
var defaultDatabase = admin.database();

var ref = defaultDatabase.ref();

/**
 * Authenticate a user from the Firebase Database
 * @param {Object} req -- request body
 * @param {Object} res -- response body
 */
module.exports = (req, res) => {
    var name = req.body.name;
    var email = req.body.email;
    var password = req.body.password;

    //get user information by the email entered
    admin.auth().getUserByEmail(email).then((authData) => {
        //if we found user information, we will be here
        console.log(authData);
        //check to see if the passwords match
        if(password == authData.displayName){
            //if they do, respond to the client back with the uid
            //and log the user in
            var loggedIn = {status: "success", uid: authData.uid};
            res.status(200).send(JSON.stringify(loggedIn));
        } else {
            //if they don't respond back to the client that the username or password could not be found
            var userNotFound = {status: "error", error: "Username or Password cannot be found!"};
            res.status(200).send(JSON.stringify(userNotFound));
        }
    }).catch((error) => {

        //if we get here we could not locate the email address of the user
        var errorCode = error.code;
        var errorMessage = error.message;

        console.log(errorMessage);

        //respond back to the user that there was a problem with authentication
        var toSend = {status: "error", error: errorMessage};
        res.status(200).send(JSON.stringify(toSend));
    });
};