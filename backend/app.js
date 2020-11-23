const express = require('express')
require('./db/mongoose')
const userRouter = require('./router/User')
const eventRouter = require('./router/Event')
const categoryRouter = require('./router/Category')
const typeRouter = require('./router/Type')
const Category = require('./model/Category')
const Type = require('./model/Type')
const app = express()
const port = process.env.PORT
const geocode = require('./controllers/geocode')

app.use(express.json())
app.use('/user', userRouter)
app.use('/event', eventRouter)
app.use('/category', categoryRouter)
app.use('/type', typeRouter)

app.get('/typesAndCategories', async(req,res) =>{
    try{
        const Types =  await Type.find({}, {__v:0})
        const Categories =  await Category.find({}, {__v:0})
        res.status(201).send({error:false, Types, Categories})
    }
    catch(e)
    {
        res.status(400).send({error:true})
    }
})

app.listen(port, () => {
    console.log('Server is up on port ' + port)
})