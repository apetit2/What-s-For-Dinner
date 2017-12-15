//This is the router for service/api
const router = require('express').Router();
const ingredients = require('./ingredients');
const wine = require('./wine');
const recipe = require('./getrecipe');

//Attach all post/get requests
router.post('/ingredients', ingredients);
router.post('/wine', wine);
router.post('/recipe', recipe);

module.exports = router;