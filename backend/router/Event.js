const express = require('express')
const router = express.Router()
const multer = require('multer')
const sharp = require('sharp')
const Event = require('../model/Event')
const authenticate = require('../controllers/Authentication')
const {ObjectId} = require('mongodb');
const geocode = require('../controllers/geocode')

router.post('/add', authenticate, async (req, res) => {
    try {
        req.body.organizer = { ...{ orgUser: req.user._id }, ...req.body.organizer }
        const event = new Event(req.body)
        event.availableSeats = req.body.noOfSeats
        if(!event.location.latitude || !event.location.longitude){
          console.log(req.body.address)
            geocode( req.body.address, (error, {latitude, longitude, location} = {}) => {
              if(error){
                  console.log(error)
              }else{
                event.location.latitude = latitude
                event.location.longitude = longitude
                console.log(latitude, longitude)
                console.log(event.location)
                event.save()
              }
          })
        }
        await event.save()
        res.status(200).send({ error: false, message: 'Event Added', eventId : event._id })
    } catch (e) {
        console.log(e)
        res.status(500).send({ error: true, message: 'Error in adding event'})
    }
})

router.get('/', async (req, res) => {
    try {
        const events = await Event.find({}, { createdAt: 0, updatedAt: 0, __v: 0 })
        res.status(200).send({ error: false, events })
    } catch (e) {
        res.status(500).send({ error: true, message: 'Events not found' })
    }
})

router.post('/sorted', authenticate, async (req, res) => {
    try {
        const now = new Date()
        
        const upcoming = await Event.aggregate([
            {
                $match: {
                    $expr: {
                        $gt : ["$endTime", now]
                    }
                }
            },
            {
                $lookup: {
                  from: 'likedevents',
                  'let': { event: '$_id' },
                  pipeline: [
                    {
                      $match: {
                        $expr: {
                            $and : 
                            [{
                          $eq: ['$eventId', '$$event']
                            },
                            {
                                $eq: ['$userId', req.user._id]
                                  }
                                ]
                    }
                      }
                    }
                  ],
                  as: 'likedusers'
                }
              },
              {
                $lookup : {
                  from : 'events',
                  localField : '_id',
                  foreignField : '_id',
                  as : 'eventDetails'
                }
              },
              {
                $unwind : '$eventDetails'
              },
              {
                $lookup : {
                    from: 'bookings',
                    let: {event: '$eventDetails._id', user : req.user._id},
                    pipeline: [
                      {
                        $match:{
                          $expr : {
                            $and : [
                              {
                                $eq : ['$eventId', '$$event']
                              },
                              {
                                $eq : ['$userId', '$$user']
                              }
                            ]
                          }
                        }
                      }
                    ],
                    as: 'booking'
                },
              },
              {
                $addFields : {
                    liked : {
                        $cond : {
                            if: { $gt: [ {$size: "$likedusers"}, 0 ] }, then: true, else: false
                        }
                    },
                    started : {
                        $cond: { if: { $lte: [ '$eventDetails.startTime', new Date()] }, then: true, else: false }
                      },
                      end : {
                        $cond: { if: { $lt: [ '$eventDetails.endTime', new Date()] }, then: true, else: false }
                      },
                      booked : {
                        $cond: { if: { $gt: [{ $size: '$booking' }, 0 ]}, then: true, else: false}
                      } 
                      
                }
            },
            // {
            //   $set : {
            //     'eventDetails.image': {
            //       $cond: { if: { $eq: [ '$eventDetails.image', null ]}, then: false, else: true}
            //     } ,
            //   }
            // },
            { $sort : { 'startTime' : 1 } },
            {
                $lookup : {
                    from: 'reviews',
                    localField: 'eventDetails._id',
                    foreignField: 'event',
                    as: 'eventDetails.reviews'
                }
            },
            {
                $project : {
                    eventDetails : 1,
                    started : 1,
                    liked : 1,
                    _id:0,
                    end:1,
                    booked:1,
                }
            }
        ])

        const sortedbyCat = upcoming

        const sortedbyDate = sortedbyCat.filter((c) => {
            if(req.body.date && c.eventDetails.startTime)
            {
                if(new Date(c.eventDetails.startTime).setHours(0,0,0,0) === new Date(req.body.date).setHours(0,0,0,0))
                return c
            }
            else{
                return c
            }
        })

        const sortedbyCity = sortedbyDate.filter((c) => {
            if(req.body.city && c.eventDetails.location.city)
            {
                if(c.eventDetails.location.city === req.body.city)
                return c
            }
            else{
                return c
            }
        })

        console.log(sortedbyCity.length)
        await res.status(200).send({ error: false, sorted:sortedbyCity})
    }
    catch (e) {
        console.log(e)
        res.status(400).send({ error: true, message: 'Events not found' })
    }
})


const upload = multer({
  fileFilter(req, file, cb) {
      if (!file.originalname.match(/\.(jpg|jpeg|png)$/)) {
          return cb(new Error('Please upload an image'))
      }

      cb(undefined, true)
  }
})

router.post('/:id/image', upload.single('image'), async (req, res) => {
  const buffer = await sharp(req.file.buffer).resize({ width: 333, height: 250 }).png().toBuffer()
  const event = await Event.findOne({_id:req.params.id})
  event.image = buffer
  await event.save()
  res.send({error:false})
}, (error, req, res, next) => {
  res.status(400).send({ error: true, message:error.message })
})

router.delete('/:id/image', async (req, res) => {
  const event = await Event.findOne({_id:req.params.id})
  event.image = undefined
  await event.save()
  res.send({error:false})
})

router.get('/image/:id', async (req, res) => {
  try {
    const event = await Event.findOne({_id:req.params.id})

      if (!event || !event.image) {
          throw new Error()
      }

      res.set('Content-Type', 'image/png')
      res.send(event.image)
  } catch (e) {
      res.status(404).send({error:true, message:'Profile picture not found'})
  }
})

module.exports = router
