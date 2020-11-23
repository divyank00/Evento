const mongoose = require('mongoose')

const bookingSchema = mongoose.Schema(
    {
        userId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User'
        },
        eventId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Event'
        },
        noOfPeople: {
            type: Number,
            required: true
        },
        tickets: [{
            ticketId: {
                type: String,
            },
        }]
    },
    {
        timestamps: true
    })

module.exports = mongoose.models.Booking || mongoose.model('Booking', bookingSchema)