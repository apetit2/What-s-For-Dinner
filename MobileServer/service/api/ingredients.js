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
 * Essentially looks up a list of recipes from the rest api given the list of ingredients
 * @method initialize
 * @param {String} ingredients JSON string of ingredients we want to look up
 * @param {Object} res Response Object
 * @param {Object} req Request Object
 */

function initialize(ingredients, res, req) {
    //check to make sure some ingredients were actually entered, otherwise we don't need to look up

    var urlBaseString = 'https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients?fillIngredients=false&ingredients=';

    //go through the list of ingredients and add them to the url
    for (var i = 0; i < ingredients.length; i++){
        if(i < ingredients.length - 1) {
            urlBaseString = urlBaseString + ingredients[i] + '%2C'
        } else {
            urlBaseString = urlBaseString + ingredients[i]
        }
    }

    urlBaseString = urlBaseString + '&limitLicense=false&number=10&ranking=1';

    console.log(urlBaseString);
    //send request to API
    var options = {
        url: urlBaseString,
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
                var menu = JSON.parse(body);
                for(var i = 0; i < menu.length; i++){
                    ref.child(menu[i].title).child("ingredients").set(JSON.stringify({}));
                    ref.child(menu[i].title).child("id").set(menu[i].id.toString());
                }

                res.status(200).send(JSON.parse(body));
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
    var ingredients = req.body.ingredients;

    //send out a request to the api
    var recipe = initialize(ingredients, res, req);

};