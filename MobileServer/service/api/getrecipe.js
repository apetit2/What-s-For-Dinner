const dotenv = require('dotenv').load();
const apiKey = process.env.API_KEY;
const dataURL = process.env.DATABASE_URL;
const request = require('request');
const admin = require('firebase-admin');
const serviceAccount = require('../../file.json');

var defaultAuth = admin.auth();
var defaultDatabase = admin.database();

var ref = defaultDatabase.ref();

/**
 * Initialize everything that we need for a request to the 
 * API to get a recipe based upon id
 * @method initialize
 * @param {Object} req Request Object
 * @param {Object} res Response Object
 */
function initialize(id, req, res){

    //base url for get request to find recipe information
    var baseURL = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/informationBulk?ids=" + id + "&includeNutrition=false";

    //setting headers
    var options = {
        url: baseURL,
        headers: {
            'X-Mashape-Key': apiKey,
            'Accept': 'application/json'
        }
    };

    //make the request
    return new Promise((resolve, reject) => {
        //async job
        request.get(options, (err, resp, body) => {
            if(err) {
                //this is the case when there is an internal server error with the API
                res.status(500).end()
                reject(err);
            } else {
                //if the server responded with data

                //parse the response body
                var body = JSON.parse(body);
                console.log(body);

                //get the list of analyzed instructions
                var analyzedInstructions = body[0].analyzedInstructions;
               
                //make an array of all steps for the recipe
                instructions = [];
                if(analyzedInstructions.length != 0){
                    instructions = analyzedInstructions[0].steps;
                }

                //get the time it takes to complete the recipe
                var readyInMinutes = body[0].readyInMinutes;
                var time = "Ready in " + readyInMinutes + " minutes";
                
                //get the title of the recipe, we will use this to store the recipe in Firebase
                var title = body[0].title;

                //toInsert is what will send out as the list of steps for the recipe
                var toInsert = [];

                for(var i = 0; i < instructions.length; i++){
                    toInsert.push(instructions[i]);
                }

                //what we send back to our client, includes instructions and time
                var sendOut = {"instructions" : toInsert, "time" : time};

                //add the recipe to the database
                ref.child(title).child("ingredients").set(JSON.stringify(toInsert));
                ref.child(title).child("time").set(time);
                res.status(200).send(sendOut);
                resolve(body);
            }
        });
    });

}

/**
 * Exports this post request to the index.js
 * @method ingredients 
 * @param {Object} req Request Object
 * @param {Object} res Response Object
 */

module.exports = (req, res) => {
    var id = req.body.id;

    //send out the request to the api
    var menuItem = initialize(id, req, res);
};