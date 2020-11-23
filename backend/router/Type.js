const express = require('express')
const router = express.Router()
const Type = require('../model/Type')

router.post('/', async(req,res) => {
    try{
        const type = new Type(req.body)
        await type.save()
        res.status(200).send({error : false})

    }catch(e)
    {
        console.log(e)
        res.status(400).send({error : true})
    }
})

router.get('/', async(req,res) => {
    try{
        const types = await Type.find({})
        res.status(200).send({error : false, types})
    }catch(e)
    {
        res.status(500).send({error:true, message : 'Internal server error'})
    }
})

module.exports = router
