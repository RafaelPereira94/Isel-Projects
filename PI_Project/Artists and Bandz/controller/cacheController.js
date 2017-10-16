"use strict";

/**
 * Middleware to attent requests
 * Does caching and redirects to artistsController 
 * */
const artistsController = require('./artistsController.js')

const ArtistsService = require('./../model/services/artistsService.js')
const httpGetAsJson = require('./../httpGetAsJson.js')
const artistsService = new ArtistsService(httpGetAsJson)

const cache = require('./../cache/cache.js')

const url = require('url')

const express = require('express')
const router = express.Router()

/**
 * There are to ways to search for an artist or band:
 *  search/{name} or search?name={name}
 */
router.get(
    '/search/:id',
    function(req, resp, next) { next() },
    artistsController['search']
)

/**
 * There are two ways to search for an artist or band:
 *  search/{name} or search?name={name}
 */
router.get(
    '/search',
    function(req, resp, next) {
        req.params.id = req.query.name
        next()
    },
    artistsController['search']
)

router.get(
    '/home',
    function (req, resp, next) {
        next()
    },
    artistsController['home']
)

router.get(
    '/artists/:id',
    function(req, resp, next) {
        let queryParams = '/?limit=20&offset=0'
        if(req.query.limit != undefined)
            queryParams = '?limit=' + req.query.limit + '&offset=' + req.query.offset  
        
        let reqUrl = req.url + queryParams
        const data = cache.get(reqUrl)
        if(data != null){
            req.data = data
            next()
        }
        artistsService.artistDetail(req.params.id, queryParams)
        .then(obj =>{
            cache.put(reqUrl, obj, 7200)
            req.data = obj
            next()
        })
        .catch(next)
    },
    artistsController['artists']
)

router.get(
    '/album/:id',
    function (req, resp, next) {
        const data = cache.get(req.url)
        if(data != null)
            req.data = data

        artistsService.albumDetail(req.params.id)
        .then(obj =>{
            cache.put(req.url, obj, obj.timeOut)
            req.data = obj
            next()
        })
        .catch(next)
    },
    artistsController['album']
)

router.get(
    '/myplaylists',
    function(req, resp, next) {
        next()
    },
    artistsController['myplaylists']
)

router.delete(
    '/myplaylists/:idPl/deleteTrack/:idTr', 
    function(req, resp, next) {
        next()
    },
    artistsController['deleteTrack']
)

router.route('/signup')
.get(function(req, resp, next) {
    artistsController['signup'](req, resp, next)
})
.post(function(req, resp, next) {
    artistsController['signup'](req, resp, next)
})

router.get(
    '/login',
    function(req, resp, next) {
        next()
    },
    artistsController['login']
)

router.post(
    '/myplaylists/:idPl/addTrack/:idTr',
    function(req, resp, next){
        next()
    },
    artistsController['addTrack']
)

router.get(
    '/logout',
    function(req, resp, next) {
        next()
    },
    artistsController['logout']
)

router.post(
    '/createplaylist/:name',
    function (req, res, next){
        next()
    },
    artistsController['createplaylist']
)

router.delete(
    '/deleteplaylist/:id',
    function (req, res, next){
        next()
    },
    artistsController['deletePlaylist']
)

router.delete(
    '/deletesharedplaylist/:id',
    function (req, res, next){
        next()
    },
    artistsController['deleteSharedPlaylist']
)

router.put(
    '/renameplaylist',
    function (req, res, next){
        next()
    },
    artistsController['renamePlaylist']
)

router.post(
    '/sendinvite/:idPl/user/:idUser/permission/:perm',
    function (req, res, next){
        next()
    },
    artistsController['sharePlaylist']
)

router.get(
    '/myinvites',
    function(req, res,next){
        next()
    },
    artistsController['invites']
)

router.put(
    '/acceptinvite',
    function(req, res,next){
        next()
    },
    artistsController['acceptShare']
)

router.put(
    '/declineinvite',
    function(req, res,next){
        next()
    },
    artistsController['declineShare']
)

router.put(
    '/changepermission',
    function(req, res,next){
        next()
    },
    artistsController['changePermission']
)

router.delete(
    '/stopsharing/:idPl/user/:toUser',
    function (req, res, next){
        next()
    },
    artistsController['stopSharingWith']
)

module.exports = router