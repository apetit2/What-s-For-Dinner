const router = require('express').Router();
const ingredients = require('./ingredients');

//Attach all post/get requests
router.post('/ingredients', ingredients);

module.exports = router;