const express = require('express')
const router = express.Router()
const multer = require('multer')
const sharp = require('sharp')
const User = require('../model/User')
const Event = require('../model/Event')
const Booking = require('../model/Booking')
const LikedEvent = require('../model/LikedEvent')
const Review = require('../model/Review')
const shortid = require('shortid')
const bcrypt = require('bcryptjs')
const authenticate = require('../controllers/Authentication')

router.post('/register', async (req, res) => {
  try {
    const userM = await User.find({ email: req.body.email })
    if (userM.length) {
      res.status(400).send({ error: true, message: 'Email already exixts' })
    }
    else {
      req.body.password = await bcrypt.hash(req.body.password, 8)
      const user = new User(req.body)
      await user.save()
      const token = await user.generateAuthToken()
      res.status(200).send({ error: false, message: 'Registered successfully', token, user })
    }

  } catch (e) {
    res.status(500).send({ error: true, message: 'Error in registration' })
  }
})

router.post('/login', async (req, res) => {
  try {
    const user = await User.findByCredentials(req.body.email, req.body.password)
    const token = await user.generateAuthToken()
    res.status(200).send({ error: false, message: 'Logged in successfully', token, user })
  } catch (e) {
    console.log(e)
    res.status(400).send({ error: true, message: 'Invalid credentials' })
  }
})

router.post('/logout', authenticate, async (req, res) => {
  try {
    req.user.tokens = req.user.tokens.filter((token) => {
      return token.token !== req.token
    })
    await req.user.save()

    res.send({ error: false, message: 'Logged out successfully' })

  } catch (e) {
    res.status(500).send({ error: true, message: 'Error in logging out' })
  }
})

router.post('/logoutAll', authenticate, async (req, res) => {
  try {
    req.user.tokens = []
    await req.user.save()
    res.send({ error: false, message: 'Logged out from all devices' })
  } catch (e) {
    res.status(500).send({ error: true, message: 'Error in logging out' })
  }
})

router.get('/me', authenticate, async (req, res) => {
  try {
    res.status(200).send({ error:false, user: req.user})
  }
  catch (e) {
    res.status(500).send({ error: true, message: 'Error in logging out' })
  }
})

router.patch('/update', authenticate, async (req, res) => {
  const updates = Object.keys(req.body)
  const allowedUpdates = ['name', 'password', 'contactNumber', 'lastLocation']
  const isValidOperation = updates.every((update) => allowedUpdates.includes(update))

  if (!isValidOperation) {
    return res.status(400).send({ error: true, message: 'Invalid updates!' })
  }
  try {

    if (updates.includes('password')) {
      req.body.password = await bcrypt.hash(req.body.password, 8)
    }
    updates.forEach((update) => req.user[update] = req.body[update])
    await req.user.save()
    res.send(req.user)
  } catch (e) {
    res.status(500).send({ error: true, message: 'Error in updating' })
  }
})

router.get('/status', authenticate, async (req, res) => {
  res.send({ error: false })
})

router.post('/book', authenticate, async (req, res) => {
  try {
    const event = await Event.findOne({ _id: req.body.eventId })
    if (event) {
      const booking = await Booking.findOne({ userId: req.user._id, eventId: event._id })


      if (event.availableSeats <= 0 || event.availableSeats - req.body.noOfPeople < 0) {
        res.status(400).send({ error: true, message: 'Tickets not available' })
      }
      else if (booking) {
        const noofTickets = req.body.noOfPeople

        const newTickets = []

        for (i = 0; i < noofTickets; i++) {
          var t = shortid.generate();
          booking.tickets = booking.tickets.concat({ ticketId: t })
          newTickets.push({ ticketId: t })
        }

        booking.noOfPeople += noofTickets
        event.availableSeats = event.availableSeats - noofTickets
        await event.save()
        await booking.save()
        res.status(200).send({ error: false, message: 'Event booked successfully', tickets: newTickets })
      }
      else {
        const booking = new Booking(req.body)
        booking.userId = req.user._id
        const noofTickets = req.body.noOfPeople

        const newTickets = []

        for (i = 0; i < noofTickets; i++) {
          var t = shortid.generate();
          booking.tickets = booking.tickets.concat({ ticketId: t })
          newTickets.push({ ticketId: t })
        }

        event.availableSeats = event.availableSeats - noofTickets
        await event.save()
        await booking.save()
        res.status(200).send({ error: false, message: 'Event booked successfully', tickets: newTickets })
      }
    }
    else {
      res.status(500).send({ error: true, message: 'Internal server error' })
    }
  } catch (e) {
    console.log(e)
    res.status(500).send({ error: true, message: 'Internal server error' })
  }
})

router.get('/events/booked/upcoming', authenticate, async (req, res) => {
  try {
    const bookedEvents = await Booking.aggregate([
      {
        $lookup: {
          from: "events",
          localField: "eventId",
          foreignField: "_id",
          as: "eventDetails"
        }
      },
      {
        $unwind: "$eventDetails"
      },
      {
        $match: {
          $expr: {
            $and: [
              {
                $eq: [
                  '$userId', req.user._id
                ]
              },
              {
                $gt: ["$eventDetails.endTime", new Date()]
              }
            ]
          }
        }
      },
      {
        $lookup: {
          from: "likedevents",
          let: { event: '$eventDetails._id', user: req.user._id },
          pipeline: [
            {
              $match: {
                $expr: {
                  $and: [
                    {
                      $eq: ['$eventId', '$$event']
                    },
                    {
                      $eq: ['$userId', '$$user']
                    }
                  ]
                }
              }
            }
          ],
          as: "likedevent"
        }
      },
      {
        $addFields: {
          liked: {
            $cond: { if: { $gt: [{ $size: "$likedevent" }, 0] }, then: true, else: false }
          },
          started: {
            $cond: { if: { $lte: ['$eventDetails.startTime', new Date()] }, then: true, else: false }
          },
          end: {
            $cond: { if: { $lt: ['$eventDetails.endTime', new Date()] }, then: true, else: false }
          },
          booked: true
        }
      },
      {
        $lookup: {
          from: 'reviews',
          localField: 'eventId',
          foreignField: 'event',
          as: 'eventDetails.reviews'
        }
      },
      {
        $sort: {
          'eventDetails.startTime': 1
        }
      }, {
        $project: {
          userId: 0,
          _id: 0,
          'eventDetails.organizer.orgUser': 0,
          'eventDetails.createdAt': 0,
          'eventDetails.updatedAt': 0,
          'eventDetails.__v': 0,
          'eventDetails.image': 0,
          // likedevent: 0,
          createdAt: 0,
          updatedAt: 0,
          __v: 0
        }
      }
    ])

    await res.status(200).send({ error: false, bookedEvents })
  } catch (e) {

    console.log(e)
    res.status(500).send({ error: true, message: 'Internal Server Error' })
  }
})

router.get('/events/booked/completed', authenticate, async (req, res) => {
  try {
    const bookedEvents = await Booking.aggregate([
      {
        $lookup: {
          from: "events",
          localField: "eventId",
          foreignField: "_id",
          as: "eventDetails"
        }
      },
      {
        $unwind: "$eventDetails"
      },
      {
        $match: {
          $expr: {
            $and: [
              {
                $eq: [
                  '$userId', req.user._id
                ]
              },
              {
                $lte: ["$eventDetails.endTime", new Date()]
              }
            ]
          }
        }
      },
      {
        $lookup: {
          from: "likedevents",
          let: { event: '$eventDetails._id', user: req.user._id },
          pipeline: [
            {
              $match: {
                $expr: {
                  $and: [
                    {
                      $eq: ['$eventId', '$$event']
                    },
                    {
                      $eq: ['$userId', '$$user']
                    }
                  ]
                }
              }
            }
          ],
          as: "likedevent"
        }
      },
      {
        $addFields: {
          liked: {
            $cond: { if: { $gt: [{ $size: "$likedevent" }, 0] }, then: true, else: false }
          },
          started: true,
          end: {
            $cond: { if: { $lt: ['$eventDetails.endTime', new Date()] }, then: true, else: false }
          },
          booked: true
        }
      },
      {
        $lookup: {
          from: 'reviews',
          localField: 'eventId',
          foreignField: 'event',
          as: 'eventDetails.reviews'
        }
      },
      {
        $sort: {
          'eventDetails.startTime': -1
        }
      }, {
        $project: {
          userId: 0,
          _id: 0,
          'eventDetails.organizer.orgUser': 0,
          'eventDetails.createdAt': 0,
          'eventDetails.updatedAt': 0,
          'eventDetails.__v': 0,
          'eventDetails.image': 0,
          // likedevent: 0,
          createdAt: 0,
          updatedAt: 0,
          __v: 0
        }
      }
    ])


    await res.status(200).send({ error: false, bookedEvents })
  } catch (e) {

    console.log(e)
    res.status(500).send({ error: true, message: 'Internal Server Error' })
  }
})

router.get('/events/registered/upcoming', authenticate, async (req, res) => {
  try {
    const registeredEvents = await Event.aggregate([
      {
        $match: {
          $expr: {
            $and: [
              {
                $eq: [
                  '$organizer.orgUser', req.user._id
                ]
              },
              {
                $gt: ["$endTime", new Date()]
              }
            ]
          }
        }
      },
      {
        $lookup: {
          from: "likedevents",
          let: { event: '$_id', user: req.user._id },
          pipeline: [
            {
              $match: {
                $expr: {
                  $and: [
                    {
                      $eq: ['$eventId', '$$event']
                    },
                    {
                      $eq: ['$userId', '$$user']
                    }
                  ]
                }
              }
            }
          ],
          as: "likedevent"
        }
      },
      {
        $lookup: {
          from: 'events',
          localField: '_id',
          foreignField: '_id',
          as: 'eventDetails'
        }
      },
      {
        $unwind: '$eventDetails'
      },
      {
        $lookup: {
          from: 'bookings',
          let: { event: '$eventDetails._id', user: req.user._id },
          pipeline: [
            {
              $match: {
                $expr: {
                  $and: [
                    {
                      $eq: ['$eventId', '$$event']
                    },
                    {
                      $eq: ['$userId', '$$user']
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
        $addFields: {
          liked: {
            $cond: { if: { $gt: [{ $size: "$likedevent" }, 0] }, then: true, else: false }
          },
          started: {
            $cond: { if: { $lte: ['$eventDetails.startTime', new Date()] }, then: true, else: false }
          },
          end: {
            $cond: { if: { $lt: ['$eventDetails.endTime', new Date()] }, then: true, else: false }
          },
          booked: {
            $cond: { if: { $gt: [{ $size: '$booking' }, 0] }, then: true, else: false }
          }
        }
      },
      {
        $lookup: {
          from: 'reviews',
          localField: 'eventDetails._id',
          foreignField: 'event',
          as: 'eventDetails.reviews'
        }
      },
      // {
      //   $set: {
      //     'eventDetails.image': 0,
      //   }
      // },
      {
        $sort: {
          startTime: 1
        }
      },
      {
        $project: { _id: 0, eventDetails: 1, liked: 1, started: 1, end: 1, booked: 1 }
      }
    ])
    res.status(200).send({ error: false, registeredEvents })
  } catch (e) {
    res.status(400).send({ error: true, message: 'Internal Server Error' })
  }
})

router.get('/events/registered/completed', authenticate, async (req, res) => {
  try {
    const registeredEvents = await Event.aggregate([
      {
        $match: {
          $expr: {
            $and: [
              {
                $eq: [
                  '$organizer.orgUser', req.user._id
                ]
              },
              {
                $lte: ["$endTime", new Date()]
              }
            ]
          }
        }
      },
      {
        $lookup: {
          from: "likedevents",
          let: { event: '$_id', user: req.user._id },
          pipeline: [
            {
              $match: {
                $expr: {
                  $and: [
                    {
                      $eq: ['$eventId', '$$event']
                    },
                    {
                      $eq: ['$userId', '$$user']
                    }
                  ]
                }
              }
            }
          ],
          as: "likedevent"
        }
      },
      {
        $lookup: {
          from: 'events',
          localField: '_id',
          foreignField: '_id',
          as: 'eventDetails'
        }
      },
      {
        $unwind: '$eventDetails'
      },
      {
        $lookup: {
          from: 'bookings',
          let: { event: '$eventDetails._id', user: req.user._id },
          pipeline: [
            {
              $match: {
                $expr: {
                  $and: [
                    {
                      $eq: ['$eventId', '$$event']
                    },
                    {
                      $eq: ['$userId', '$$user']
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
        $addFields: {
          liked: {
            $cond: { if: { $gt: [{ $size: "$likedevent" }, 0] }, then: true, else: false }
          },
          started: true,
          end: {
            $cond: { if: { $lt: ['$eventDetails.endTime', new Date()] }, then: true, else: false }
          },
          booked: {
            $cond: { if: { $gt: [{ $size: '$booking' }, 0] }, then: true, else: false }
          }
        }
      },
      {
        $lookup: {
          from: 'reviews',
          localField: 'eventDetails._id',
          foreignField: 'event',
          as: 'eventDetails.reviews'
        }
      },
      // {
      //   $set: {
      //     'eventDetails.image': 0,
      //   }
      // },
      {
        $sort: {
          'startTime': -1
        }
      },
      {
        $project: { _id: 0, eventDetails: 1, liked: 1, started: 1, end: 1, booked: 1 }
      },
    ])
    res.status(200).send({ error: false, registeredEvents })
  } catch (e) {
    console.log(e)
    res.status(400).send({ error: true, message: 'Internal Server Error' })
  }
})

router.post('/events/review', authenticate, async (req, res) => {
  try {
    const event = await Event.findOne({ _id: req.body.eventId })
    const user = await User.findOne({ _id: req.user._id })
    if (event && user) {
      const reviewed = await Review.findOne({ user: user._id, event: event._id })
      if (reviewed) {
        res.status(400).send({ error: true, message: 'Event already reviewed' })
      }
      else {
        const review = new Review({ user: user._id, event: event._id, rating: req.body.rating, comment: req.body.comment })
        await review.save()
        event.totalRating += 1
        event.totalStar += req.body.rating
        event.avgRating = (event.avgRating * (event.totalRating - 1) + req.body.rating) / event.totalRating

        console.log(event.avgRating)
        console.log(event.totalRating)
        await event.save()
        res.status(200).send({ error: false, message: 'Review added' })
      }
    }
    else {
      res.status(400).send({ error: true, message: 'Event not found' })
    }
  } catch (e) {
    console.log(e)
    res.status(500).send({ error: true, message: 'Internal Server Error' })
  }
})

router.post('/events/like', authenticate, async (req, res) => {
  try {
    const event = await Event.findOne({ _id: req.body.eventId })
    if (event) {
      const levent = await LikedEvent.findOne({ eventId: event._id, userId: req.user._id })
      if (!levent) {
        const likedEvent = new LikedEvent({ eventId: event._id, userId: req.user._id })
        await likedEvent.save()
      }
      res.status(200).send({ error: false, message: 'Liked event' })
    }
    else {
      res.status(400).send({ error: true, message: 'Event not found' })
    }
  }
  catch (e) {
    res.status(500).send({ error: true, message: 'Internal Server Error' })
  }
})

router.delete('/events/dislike', authenticate, async (req, res) => {
  try {
    const deletd = await LikedEvent.deleteOne({ userId: req.user._id, eventId: req.body.eventId })
    res.status(200).send({ error: false, message: "Event disliked" })

  } catch (e) {
    res.status(500).send({ error: true, message: "Internal Server error" })
  }
})

router.get('/events/liked', authenticate, async (req, res) => {
  try {
    const likedEvents = await LikedEvent.aggregate([
      {
        $match: {
          userId: req.user._id
        }
      },
      {
        $lookup: {
          from: 'events',
          localField: 'eventId',
          foreignField: '_id',
          as: 'eventDetails'
        }
      },
      {
        $unwind: '$eventDetails'
      },
      {
        $lookup: {
          from: 'reviews',
          localField: 'eventId',
          foreignField: 'event',
          as: 'eventDetails.reviews'
        }
      },
      {
        $lookup: {
          from: 'bookings',
          let: { event: '$eventId', user: req.user._id },
          pipeline: [
            {
              $match: {
                $expr: {
                  $and: [
                    {
                      $eq: ['$eventId', '$$event']
                    },
                    {
                      $eq: ['$userId', '$$user']
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
        $addFields: {
          liked: true,
          started: {
            $cond: { if: { $lte: ['$eventDetails.startTime', new Date()] }, then: true, else: false }
          },
          end: {
            $cond: { if: { $lt: ['$eventDetails.endTime', new Date()] }, then: true, else: false }
          },
          booked: {
            $cond: { if: { $gt: [{ $size: '$booking' }, 0] }, then: true, else: false }
          }
        }
      },
      {
        $sort: {
          'eventDetails.startTime': -1
        }
      },
      // {
      //   $set: {
      //     'eventDetails.image': 0,
      //   }
      // },
      {
        $project: {
          _id: 0,
          userId: 0,
          eventId: 0,
          __v: 0,
          'eventDetails.createdAt': 0,
          'eventDetails.updatedAt': 0,
          'eventDetails.__v': 0,
          booking: 0
        }
      }
    ])
    res.status(200).send({ error: false, likedEvents })
  }
  catch (e) {
    console.log(e)
    res.status(500).send({ error: true, message: 'Internal Server Error' })
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

router.post('/profilepic', authenticate, upload.single('profilepic'), async (req, res) => {
  const buffer = await sharp(req.file.buffer).resize({ width: 250, height: 250 }).png().toBuffer()
  req.user.profilePic = buffer
  await req.user.save()
  res.send({ error: false })
}, (error, req, res, next) => {
  res.status(400).send({ error: true, message: error.message })
})

router.post('/profilepic/:id', upload.single('profilepic'), async (req, res) => {
  const buffer = await sharp(req.file.buffer).resize({ width: 250, height: 250 }).png().toBuffer()
  const user = await User.findOne({_id:req.params.id})
  user.profilePic = buffer
  await user.save()
  res.send({ error: false })
}, (error, req, res, next) => {
  res.status(400).send({ error: true, message: error.message })
})


router.delete('/profilepic', authenticate, async (req, res) => {
  req.user.profilePic = undefined
  await req.user.save()
  res.status(200).send({ error: false })
})

router.get('/profilepic/:id' , async (req, res) => {
  try {
    const user = await User.findById(req.params.id)

    if (!user || !user.profilePic) {
      throw new Error()
    }

    res.set('Content-Type', 'image/png')
    res.send(user.profilePic)
  } catch (e) {
    res.status(404).send({ error: true, message: 'Profile picture not found' })
  }
})

module.exports = router