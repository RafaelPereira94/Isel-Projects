"use strict";
const couchDb = require('./couchDbRequest.js')
const usersDb = require('./usersDb.js')
const invitesDb = require('./invitesDb.js')
const dbPath = '/artists_to_bandz_playlists'

function createPlaylist(user, playlistName){
    let newPlaylist
    return new Promise((resolve, reject) => {
        getPlaylists(user)
        .then(playlists => {
            if(playlists.map(p => p.name).includes(playlistName))
                return reject(new Error('Name already in use'))
            return couchDb.post(dbPath, {'name': playlistName, 'tracks': []})
        })
        .then(playlist => {
            newPlaylist = playlist
            user.playlists.push(playlist.id)
            return couchDb.put(usersDb.dbPath + '/' + user.username, user)
        })
        .then(user => resolve(newPlaylist))
        .catch(reject)
    })
}

function getPlaylists(user){
    return new Promise((resolve, reject) => {
        couchDb.getAllIds(dbPath, user.playlists)
        .then(resolve)
        .catch(reject)
    })
}

function renamePlaylist(user, playlistId, playlistName){
    const path = dbPath + '/' + playlistId
    return new Promise((resolve, reject) => {
        getPlaylists(user)
        .then(playlists => {
            if(playlists.map(p => p.name).includes(playlistName))
                return reject(new Error('Name already in use'))
            const playlist = playlists.find(p => p._id == playlistId)
            playlist.name = playlistName
            return couchDb.put(path, playlist)
        })
        .then(resolve)
        .catch(reject)
    })
}

function deletePlaylist(user, playlistId){
    const userDbPath = usersDb.dbPath + '/' + user.username
    const playlistsDbPath = dbPath + '/' + playlistId

    user.playlists.pop(playlistId)
    const p1 = couchDb.put(userDbPath, user)
    const p2 = couchDb.get(playlistsDbPath)
    const p3 = couchDb.getAll(invitesDb.dbPath)

    return new Promise((resolve, reject) => {
        Promise.all([p1, p2, p3])
        .then(values => {
            const invites = []
            values[2].forEach(i => {i._deleted = true; invites.push(i) })
            const p1 = couchDb.postAll(invitesDb.dbPath, invites)
            const p2 = couchDb.del(playlistsDbPath, values[1]._rev)
            return Promise.all([p1, p2])
        })
        .then(resolve)
        .catch(reject)
    })
}

function addTrack(user, playlistId, trackId){
    const path = dbPath + '/' + playlistId
    return new Promise((resolve, reject) => {
        couchDb.get(path)
        .then(playlist => {
            if(playlist.tracks.includes(trackId))
                return reject(new Error('Track already exists in that playlist'))
            playlist.tracks.push(trackId)
            return couchDb.put(path, playlist)
        })
        .then(resolve)
        .catch(reject)
    })
}

function deleteTrack(user, playlistId, trackId){
    const path = dbPath + '/' + playlistId
    return new Promise((resolve, reject) => {
        couchDb.get(path)
        .then(playlist => {
            playlist.tracks = playlist.tracks.filter(id => id != trackId)
            return couchDb.put(path, playlist)
        })
        .then(resolve)
        .catch(reject)
    })
}

function getSharedPlaylists(user){
    const permissions = []
    return new Promise((resolve, reject) => {
       couchDb.getAll(invitesDb.dbPath)
        .then(invites => {
            const invitesIds = []
            invites.forEach(i => {
                if(i.to == user.username && i.accept == 'true'){
                    invitesIds.push(i.playlist)
                    permissions.push(i.write)
                }
            })
            return couchDb.getAllIds(dbPath, invitesIds)
        })
        .then(playlists => {
            for(let i = 0; i < playlists.length; ++i)
                playlists[i].write = permissions[i]
            resolve(playlists)
        })
        .catch(reject)
    })
}

function getAllPlaylists(user){
    return new Promise((resolve, reject) => {
        Promise.all([getPlaylists(user), getSharedPlaylists(user)])
        .then(resolve)
        .catch(reject)
    })
}

module.exports.getPlaylists = getPlaylists
module.exports.getSharedPlaylists = getSharedPlaylists
module.exports.createPlaylist = createPlaylist
module.exports.deleteTrack = deleteTrack
module.exports.deletePlaylist = deletePlaylist
module.exports.addTrack = addTrack
module.exports.renamePlaylist = renamePlaylist
module.exports.getSharedPlaylists = getSharedPlaylists
module.exports.getAllPlaylists = getAllPlaylists
module.exports.dbPath = dbPath