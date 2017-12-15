const dotenv = require('dotenv').load();
const dataURL = process.env.DATABASE_URL;
const admin = require('firebase-admin');
const serviceAccount = require('../../file.json');

var defaultAuth = admin.auth();
var defaultDatabase = admin.database();

var ref = defaultDatabase.ref();

/**
 * Create a new user in the firebase database if the data entered is valid
 * @param {Object} req -- request body
 * @param {Object} res -- response body
 */
module.exports = (req, res) => {
    var name = req.body.name;
    var email = req.body.email;
    var password = req.body.password;

    //check if we can add the user
    admin.auth().createUser({
        email: email,
        password: password,
        displayName: password
    }).then((authData) => {

        //if we are able to, then let's add 'em
        ref.child("users").child(authData.uid).set(authData.displayName);
        ref.child("users").child(authData.uid).set(authData.email);
        
        //let the client know that the user was added, and send back a uid for good measure
        var toSend = {status: "success", uid: authData.uid};
        res.status(200).send(JSON.stringify(toSend));
    }).catch((error)=>{
        //if we are not able to create a user, send back information on what was invalid
        var errorCode = error.code;
        var errorMessage = error.message;
        console.log(errorMessage);

        var toSend = {status: "error", error: errorMessage};
        res.status(200).send(JSON.stringify(toSend));
    });
};