const dotenv = require('dotenv').load();
const apiKey = process.env.API_KEY;
const request = require('request');


/**
 * Get good possible recipes by wine type
 * @param {*} wine 
 * @param {*} req 
 * @param {*} res 
 */
function initialize(wine, req, res) {

    //base url to send get request to
    var urlBaseString = 'https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/food/wine/dishes';

    urlBaseString = urlBaseString + '?wine=' + wine;

    console.log(urlBaseString);
    
    //set the headers for the get request
    var options = {
        url: urlBaseString,
        headers: {
            'X-Mashape-Key': apiKey,
            'Accept': 'application/json'
        }
    }

    //send out the get request
    return new Promise((resolve, reject) => {
        //async job
        request.get(options, (err, resp, body) => {
            if (err) {
                //if here, the api is down, so we return internal server api
                res.status(500).end()
                reject(err);
            } else {
                //this returns a list of good recipes that pair with certain wines to client
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