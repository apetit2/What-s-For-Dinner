const router = require('express').Router();
const getItem = require('./getitem');

router.post('/item', getItem);

module.exports = router;