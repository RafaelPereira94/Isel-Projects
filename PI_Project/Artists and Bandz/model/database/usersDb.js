"use strict";
const couchDb = require('./couchDbRequest.js')
const dbPath = '/artists_to_bandz_users'

function findUser(username){
    const path = dbPath + '/' + username
    return new Promise((resolve, reject) => {
        couchDb.find(path)
        .then(resolve)
        .catch(reject)
    })
}

function createUser(username, password){
    return new Promise((resolve, reject) => {
        findUser(username)
        .then(user => {
            if(user != null)
                reject(new Error('Username already exists'))
            return couchDb.post(dbPath, {
                '_id': username,
                'username':username,
                'password': password,
                'playlists':[]
            })
        })
        .then(resolve)
        .catch(reject)
    })
}

module.exports.createUser = createUser
module.exports.findUser = findUser
module.exports.dbPath = dbPath