const router = require('express').Router();
const ingredients = require('./ingredients');
const wine = require('./wine');

//Attach all post/get requests
router.post('/ingredients', ingredients);
router.post('/wine', wine);

module.exports = router;