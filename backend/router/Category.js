const express = require('express')
const router = express.Router()
const Category = require('../model/Category')

router.post('/', async(req,res) => {
    try{
        const category = new Category(req.body)
        await category.save()
        res.status(200).send({error : false})

    }catch(e)
    {
        console.log(e)
        res.status(400).send({error : true})
    }
})

router.get('/', async(req,res) => {
    try{
        const categories = await Category.find({})
        res.status(200).send({error : false, categories})
    }catch(e)
    {
        res.status(500).send({error:true, message : 'Internal server error'})
    }
})

module.exports = router
