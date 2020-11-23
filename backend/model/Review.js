const mongoose = require('mongoose')

const reviewSchema = mongoose.Schema(
    {
        rating: { type: Number, required: true },
        comment: { type: String },
        user: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
        },
        event : {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Event',
        },
        userName : {
            type: String,
        }
    },
    {
        timestamps: true,
    }
)

const Review = mongoose.model('Review', reviewSchema)

module.exports = Review