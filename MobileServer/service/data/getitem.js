const dotenv = require('dotenv').load();
const dataURL = process.env.DATABASE_URL;
const admin = require('firebase-admin');
const serviceAccount = require('../../file.json');

var defaultAuth = admin.auth();
var defaultDatabase = admin.database();

var ref = defaultDatabase.ref();

/**
 * This method is used to look in the firebase database and see if a recipe with a given title exists
 * if it does exist we return that recipe to the client if it doesn't exist we return nulls
 * @param {Object} req -- request body
 * @param {Object} res -- response body
 */
module.exports = (req, res) => {
    //get the title of the recipe
    var title = req.body.title;
    console.log(title);
    
    //make a request to firebase
    ref.once('value', (snapshot) => {
        //get the list of instructions for a recipe with given title
        var ingredients = snapshot.child(title + "/ingredients").val();
        //get the time to prepare for a recipe with given title
        var time = snapshot.child(title + "/time").val();  
        //make a json object that we will send out to the client
        var dict = {"instructions" : ingredients, "time": time};
        //return the dictionary to the client 
        res.status(200).send(dict);
    });
};