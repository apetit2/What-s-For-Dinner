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

    admin.auth().createUser({
        email: email,
        password: password,
        displayName: password
    }).then((authData) => {
        ref.child("users").child(authData.uid).set(authData.displayName);
        ref.child("users").child(authData.uid).set(authData.email);
        
        var toSend = {status: "success", uid: authData.uid};
        
        res.status(200).send(JSON.stringify(toSend));
    }).catch((error)=>{
        var errorCode = error.code;
        var errorMessage = error.message;
        console.log(errorMessage);

        var toSend = {status: "error", error: errorMessage};
        res.status(200).send(JSON.stringify(toSend));
    });
};