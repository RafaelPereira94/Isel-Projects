"use strict";

const dbUsers = require('./../database/usersDb.js')

module.exports = {
    find,
    authenticate
}

function find(username, cb) {
    dbUsers.findUser(username)
    .then(user => cb(null, user))
    .catch(cb)
}

function authenticate(username, passwd, cb) {
    dbUsers.findUser(username)
    .then(user => {
        if(user == null){
            const err = new Error('User does not exist')
            err.status = 401
            return cb(err)
        }
        if(passwd != user.password){
            const err = new Error('Invalid password')
            err.status = 401
            return cb(err)
        }
        cb(null, user.username)
    })
    .catch(cb)
}