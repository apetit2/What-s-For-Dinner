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
 * Initialize everything that we need
 * @method initialize
 * @param {Object} req Request Object
 * @param {Object} res Response Object
 */
function initialize(id, req, res){

    var baseURL = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/informationBulk?ids=" + id + "&includeNutrition=false";

    //send request to API
    var options = {
        url: baseURL,
        headers: {
            'X-Mashape-Key': apiKey,
            'Accept': 'application/json'
        }
    };

    return new Promise((resolve, reject) => {
        //async job
        request.get(options, (err, resp, body) => {
            if(err) {
                res.status(500).end()
                reject(err);
            } else {
                var body = JSON.parse(body);
                console.log(body);
                var analyzedInstructions = body[0].analyzedInstructions;
                instructions = [];
                if(analyzedInstructions.length != 0){
                    instructions = analyzedInstructions[0].steps;
                }
                var readyInMinutes = body[0].readyInMinutes;
                var time = "Ready in " + readyInMinutes + " minutes";
                var title = body[0].title;
                var toInsert = [];

                for(var i = 0; i < instructions.length; i++){
                    toInsert.push(instructions[i]);
                }

                var sendOut = {"instructions" : toInsert, "time" : time};

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