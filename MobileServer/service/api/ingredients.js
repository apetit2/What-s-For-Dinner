const apiKey = process.env.API_KEY;
const request = require('request');

/**
 * 
 */

function initialize(ingredients, res, req) {
    var i = ingredients;

    console.log(apiKey);

    //send request to API
    var options = {
        url: 'https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/'
        + 'recipes/findByIngredients?fillIngredients=false&ingredients=apples%' +
        '2Cflour%2Csugar&limitLicense=false&number=5&ranking=1',
        headers: {
            'X-Mashape-Key': apiKey,
            'Accept': 'application/json'
        }
    }

    return new Promise((resolve, reject) => {
        //async job
        request.get(options, (err, resp, body) => {
            if(err) {
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
    var ingredients = req.body.ingredients;

    //send out a request to the api
    var recipe = initialize(ingredients, res, req);

};