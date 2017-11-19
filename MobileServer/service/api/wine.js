const dotenv = require('dotenv').load();
const apiKey = process.env.API_KEY;
const request = require('request');

/**
 * 
 */

function initialize(wine, req, res) {
    //check to make sure some ingredients were actually entered, otherwise we don't need to look up

    var urlBaseString = 'https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/food/wine/dishes';

    urlBaseString = urlBaseString + '?wine=' + wine;

    console.log(urlBaseString);
    //send request to API
    var options = {
        url: urlBaseString,
        headers: {
            'X-Mashape-Key': apiKey,
            'Accept': 'application/json'
        }
    }

    return new Promise((resolve, reject) => {
        //async job
        request.get(options, (err, resp, body) => {
            if (err) {
                res.status(500).end()
                reject(err);
            } else {
                res.status(200).send(JSON.parse(body));
                resolve(JSON.parse(body));
            }
        });
    });
}

/**
 * 
 */
module.exports = (req, res) => {
    var wine = req.body.wine;
    console.log(wine);

    //send out request
    var recipe = initialize(wine, req, res);
};