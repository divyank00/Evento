const mongoose = require('mongoose')

const eventSchema = mongoose.Schema(
    {
        name: {
            type: String,
            required: true,
        },
        image: {
            type: Buffer,
            default : null
        },
        categories: [{
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Category'
        }],
        eventType : {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Type'
        },
        description: {
            type: String,
            required: true,
        },
        noOfSeats : {
            type : Number,
            required : true
        },
        availableSeats : {
            type : Number,
        },
        organizer: {
            orgUser : {
                type: mongoose.Schema.Types.ObjectId,
                required : true,
                ref: 'User'
            },
            orgName : {
                type : String,
                required : true
            },
            orgEmail : {
                type : String,
                required : true
            },
            orgContactNo : {
                type : String,
                required : true
            }
        },
        startTime : {
            type : Date,
            required : true
        },
        endTime : {
            type : Date,
            required : true
        },
        location: {
            latitude: {
                type: String,
                default : null
            },
            longitude: {
                type: String,
                default : null
            },
            city : {
                type : String,
                required : true
            }
        },
        avgRating: {
            type: Number,
            required: true,
            default: 0,
        },
        totalRating: {
            type: Number,
            required: true,
            default: 0,
        },
        totalStar : {
            type: Number,
            required: true,
            default: 0,
        },
        price: {
            type: Number,
            required: true,
            default: 0,
        },
    },
    {
        timestamps: true,
    }
)

const Event = mongoose.model('Event', eventSchema)

module.exports = Event
