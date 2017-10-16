"use strict";
const couchDb = require('./couchDbRequest.js')
const dbPath = '/artists_to_bandz_invites'
const usersDb = require('./usersDb.js')

function sendInvite(fromUser, toUser, playlistId, permission){
    const body = {
        'from': fromUser,
        'to': toUser,
        'playlist': playlistId,
        'write': permission,
        'accept': 'false'
    }
    return new Promise((resolve, reject) => {
        if(toUser == fromUser)
            return reject(new Error('Cannot share the playlist with yourself'))
        const p1 = usersDb.findUser(toUser)
        const p2 = getSendInvites(fromUser)
        Promise.all([p1, p2])
        .then(values => {
            if(values[0] == null)
                return reject(new Error('User does not exist'))
            const x = values[1].find(i => i.to == toUser && i.from == fromUser && i.playlist == playlistId)
            if(x != null)
                return reject(new Error('Already shared that playlist with ' + toUser))
            return couchDb.post(dbPath, body)
        })
        .then(resolve)
        .catch(reject)
    })
}

function getMyInvites(user){
    return new Promise((resolve, reject) => {
        couchDb.getAll(dbPath)
        .then(invites => {
            const mine = invites.filter(i => i.to == user.username && i.accept == 'false')
            mine.forEach(i => {
                if(i.write == 'false') i.write = null
            })
            resolve(mine)
        })
        .catch(reject)
    })
}

function getSendInvites(user){
    return new Promise((resolve, reject) => {
        couchDb.getAll(dbPath)
        .then(invites => {
            const send = invites.filter(i => i.from == user)
            send.forEach(i => {
                if(i.write == 'false') i.write = null
            })
            resolve(send)
        })
        .catch(reject)
    })
}

function acceptInvite(user, playlistId){
    return new Promise((resolve, reject) => {
        couchDb.getAll(dbPath)
        .then(invites => {
            const invite = invites.find(i => i.to == user.username && i.playlist == playlistId)
            invite.accept = 'true'
            return couchDb.put(dbPath + '/' + invite._id, invite)
        })
        .then(resolve)
        .catch(reject)
    })
}

function declineInvite(user, playlistId){
    return new Promise((resolve, reject) => {
        couchDb.getAll(dbPath)
        .then(invites => {
            const invite = invites.find(i => i.to == user && i.playlist == playlistId)
            return couchDb.del(dbPath + '/' + invite._id, invite._rev)
        })
        .then(resolve)
        .catch(reject)
    })
}

function changePermission(fromUser, toUser, playlistId){
    return new Promise((resolve, reject) => {
        couchDb.getAll(dbPath)
        .then(invites => {
            const invite = invites.find(i => i.from == fromUser.username && i.to == toUser && i.playlist == playlistId)
            if(invite.write == 'true')
                invite.write = 'false'
            else invite.write = 'true' 
            return couchDb.put(dbPath + '/' + invite._id, invite)
        })
        .then(resolve)
        .catch(reject)
    })
}

module.exports.sendInvite = sendInvite
module.exports.getMyInvites = getMyInvites
module.exports.getSendInvites = getSendInvites
module.exports.acceptInvite = acceptInvite
module.exports.declineInvite = declineInvite
module.exports.changePermission = changePermission
module.exports.dbPath = dbPath