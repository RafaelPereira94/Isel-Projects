"use strict";

const url = require('url')
const ArtistsService = require('./../model/services/artistsService.js')
const httpGetAsJson = require('./../httpGetAsJson.js')
const artistsService = new ArtistsService(httpGetAsJson)

const dbUsers = require('./../model/database/usersDb.js')
const dbPlaylists = require('./../model/database/playlistsDb.js')
const dbInvites = require('./../model/database/invitesDb.js')

const handlers = {}

handlers.search = function(req, res, next) {
    artistsService.artistsInfo(req)
    .then(obj => {
        obj.user = req.user
        obj.title = 'Artists'
        res.render('ArtistsInfoView', obj)
    })
    .catch(next)
}

handlers.artists = function(req, res, next) {
    const data = req.data
    data.user = req.user
    data.title = 'Artists'
    res.render('ArtistDetailView', data)
}

handlers.home = function (req, res, next) {
    res.render('Home', {user: req.user, title: 'Home'})
}

handlers.album = function(req, res, next){
    const data = req.data
    data.user = req.user
    data.title = 'Album'
    if(data.user != undefined){
        dbPlaylists.getAllPlaylists(req.user)
            .then(values => {
                const p1 = artistsService.playlists(req.user, values[0])
                const p2 = artistsService.sharedPlaylists(req.user, values[1])
                return Promise.all([p1, p2])
            })
            .then(values => {
                data.playlists = values[0].playlists.concat(values[1].playlists.filter(pl => pl.write == true))
                return res.render('AlbumDetailView', data)
            })
            .catch(next)
    }
    else
        res.render('AlbumDetailView', data)
}

handlers.getPlaylists = function(req, res, next){
    return new Promise((resolve, reject) => {
        dbPlaylists.getPlaylists(req.user)
        .then(playlistData => {
            resolve(artistsService.getPlaylists(playlistData))
        })
        .catch(reject)
    })
}

handlers.myplaylists = function(req, res, next){
    if(req.user == null){
        const err = new Error('Unauthorized')
        err.status = 401
        next(err)
        return
    }
    dbPlaylists.getAllPlaylists(req.user)
    .then(values => {
        const p1 = artistsService.playlists(req.user, values[0])
        const p2 = artistsService.sharedPlaylists(req.user, values[1])
        return Promise.all([p1, p2])
    })
    .then(data => {
        const playlists = data[0]
        playlists.user = req.user
        playlists.title = 'My playlists'
        playlists.shared = data[1]
        return res.render('UserPlaylists', playlists)
    })
    .catch(next)
}

handlers.createplaylist = function(req, res, next){
    if(req.user == null){
        const err = new Error('Unauthorized')
        err.status = 401
        next(err)
    }
    else{
        dbPlaylists.createPlaylist(req.user, req.params.name)
        .then(data => {
            const playlist = data
            playlist.layout = false
            playlist.name = req.params.name
            res.render('partials/playlist', playlist)
        })
        .catch(next)
    }
}

handlers.deleteTrack = function(req, res, next){
    if(req.user == null){
        const err = new Error('Unauthorized')
        err.status = 401
        next(err)
    }
    else{
        dbPlaylists.deleteTrack(req.user, req.params.idPl, req.params.idTr)
        .then(data => next())
        .catch(next)
    }
}

handlers.addTrack = function(req, res, next){
    if(req.user == null){
        const err = new Error('Unauthorized')
        err.status = 401
        next(err)
    }
    else{
        dbPlaylists.addTrack(req.user, req.params.idPl, req.params.idTr)
        .then(data => next())
        .catch(next)
    }
}

handlers.signup = function(req, res, next){
    if(req.method == 'GET')
        res.render('signup', {title: 'Sign Up'})
    else
        dbUsers.createUser(req.body.username, req.body.password)
        .then(user => res.render('successSignup', {title: 'Sign Up'}))
        .catch(err => {
            err.status = 409
            next(err)
        })
}

handlers.login = function(req, res, next){
    let back = '/myplaylists'
    if(req.header('Referer') != null)
        back = url.parse(req.header('Referer')).path
    res.render('login', {title: 'Login', 'back': back})
}

handlers.logout = function(req, res, next){
    req.user = null
    req.logout()
    res.redirect('/home')
}

handlers.deletePlaylist = function(req, res, next){
    dbPlaylists.deletePlaylist(req.user, req.params.id)
    .then(deleteResponse => next())
    .catch(next)
}

handlers.deleteSharedPlaylist = function(req, res, next){
    dbInvites.declineInvite(req.user.username, req.params.id)
    .then(deleteResponse => next())
    .catch(next)
}

handlers.stopSharingWith = function(req, res, next){
    dbInvites.declineInvite(req.params.toUser, req.params.idPl)
    .then(deleteResponse => next())
    .catch(next)
}

handlers.renamePlaylist = function(req, res, next){
    dbPlaylists.renamePlaylist(req.user, req.body.id, req.body.name)
    .then(renameResponse => next())
    .catch(next)
}

handlers.sharePlaylist = function(req, res, next){
    dbInvites.sendInvite(req.user.username, req.params.idUser, req.params.idPl, req.params.perm)
    .then(sharePlaylist => next())
    .catch(next)
}

handlers.invites = function(req, res, next){
    if(req.user == null){
        const err = new Error('Unauthorized')
        err.status = 401
        next(err)
        return
    }

    Promise.all([dbInvites.getMyInvites(req.user), dbInvites.getSendInvites(req.user.username)])
    .then(values => {
        const data = {}
        data.invites = values[0]
        data.sendInvites = values[1]
        data.user = req.user
        data.title = 'My Invites'
        res.render('Invites', data)
    })
    .catch(next)
}

handlers.acceptShare = function(req, res, next){
    dbInvites.acceptInvite(req.user, req.body.idPl)
    .then(res => next())
    .catch(next)
}

handlers.declineShare = function(req, res, next){
    dbInvites.declineInvite(req.user.username, req.body.idPl)
    .then(res => next())
    .catch(next)
}

handlers.changePermission = function(req, res, next){
    dbInvites.changePermission(req.user, req.body.toUser, req.body.idPl)
    .then(res => next())
    .catch(next)
}

module.exports = handlers