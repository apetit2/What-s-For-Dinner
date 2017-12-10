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

    var baseURL = 'https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/' + id +'/information';

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

                //replace the below code with any data transaction we deem necessary
                var instructions = JSON.parse(body).analyzedInstructions;
                var title = JSON.parse(body).title;
                var toInsert = [];
                for(var i = 0; i < instructions.length; i++){
                    toInsert.push(instructions[i]);
                }

                ref.child(title).child("ingredients").set(JSON.stringify(toInsert));
                res.status(200).send(instructions);
                resolve(JSON.parse(body));
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