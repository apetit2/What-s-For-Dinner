const router = require('express').Router();
const api = require('./api/index');

//=============================================
//=============CORS MIDDLEWARE=================
//=============================================

router.use((req,res,next)=>{
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    res.header("Access-Control-Allow-Headers", "Origin, x-Requested-With, Content-Type, Accept");
    next();
});

//Attach all routes that under / here (excluding static files)

router.use('/api', api)

module.exports = router;