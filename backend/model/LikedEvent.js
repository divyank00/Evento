const mongoose = require('mongoose')

const likedEventSchema = mongoose.Schema(
    {
    userId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
    },
    eventId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Event'
    }
    })

module.exports = mongoose.models.LikedEvent || mongoose.model('LikedEvent', likedEventSchema)