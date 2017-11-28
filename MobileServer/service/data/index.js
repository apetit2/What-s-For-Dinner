const router = require('express').Router();
const insertNewMenu = require('./insertNewMenu');

router.post('/insertmenuitem', insertNewMenu);

module.exports = router;