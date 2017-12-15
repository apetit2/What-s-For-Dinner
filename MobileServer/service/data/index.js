///service/data router
const router = require('express').Router();
const getItem = require('./getitem');
const signup = require('./signup');
const login = require('./login');

router.post('/item', getItem);
router.post('/signup', signup);
router.post('/login', login);

module.exports = router;