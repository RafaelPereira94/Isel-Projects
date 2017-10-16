"use strict";

const Track = require('./../track.js')
const Playlist = require('./../playlist.js')
const SharedPlaylist = require('./../sharedPlaylist.js')
const parsers = require('./../parsers.js')

const spotifyUri = 'https://api.spotify.com/v1/'

function ArtistsService(httpGetAsJson){

    this.artistsInfo = function(req) {
        return new Promise((resolve, reject) => {
            let path = spotifyUri + 'search?q=' + req.params.id +'&type=artist'
            if(req.query.limit != undefined)
                path += '&limit='+req.query.limit+'&offset='+req.query.offset
            httpGetAsJson(path)
            .then(jsonData => resolve(parsers.parseJsonToArtistInfo(jsonData, req.params.id)))
            .catch(reject)
        })
    }

    this.getPlaylists = function(data) {
        const playlists = []
        data.forEach(item => {
            playlists.push(new Playlist(item.doc._id, item.doc.name, item.doc.tracks))
        })
        return playlists
    }

    this.playlists = function(user, data) {
        const path = spotifyUri + 'tracks/?ids='
        const playlists = []
        const emptyPlaylists = []
        const promises = []
        return new Promise((resolve, reject) => {
            data.forEach(item => {
                if(item.tracks.length == 0){
                    emptyPlaylists.push(new Playlist(item._id, item.name, []))
                }else{
                    playlists.push(new Playlist(item._id, item.name, []))
                    promises.push(httpGetAsJson(path + item.tracks.toString()))
                }
            })
            let i = 0
            Promise.all(promises)
            .then(values => {
                values.forEach(value => {
                    const tracks = []
                    value.tracks.forEach(t => tracks.push(new Track(t.name, t.id, t.preview_url)))
                    playlists[i].tracks = tracks
                    i++
                })
                resolve({'playlists': playlists.concat(emptyPlaylists)})
            })
            .catch(reject)
        })
    }

    this.sharedPlaylists = function(user, data) {
        const path = spotifyUri + 'tracks/?ids='
        const playlists = []
        const emptyPlaylists = []
        const promises = []
        return new Promise((resolve, reject) => {
            data.forEach(item => {
                let write = null
                if(item.write == "true")
                    write = true
                if(item.tracks.length == 0){
                    emptyPlaylists.push(new SharedPlaylist(item._id, item.name, write, []))
                }else{
                    playlists.push(new SharedPlaylist(item._id, item.name, write, []))
                    promises.push(httpGetAsJson(path + item.tracks.toString()))
                }
            })
            let i = 0
            Promise.all(promises)
            .then(values => {
                values.forEach(value => {
                    const tracks = []
                    value.tracks.forEach(t => tracks.push(new Track(t.name, t.id, t.preview_url)))
                    playlists[i].tracks = tracks
                    i++
                })
                resolve({'playlists': playlists.concat(emptyPlaylists)})
            })
            .catch(reject)
        })
    }

    this.artistDetail = function(id, queryParams) {
        return new Promise((resolve, reject) => {
            const pathDetail = spotifyUri + 'artists/' + id
            const pathAlbum = pathDetail + '/albums' + queryParams

            let pr1 = httpGetAsJson(pathDetail)
            let pr2 = httpGetAsJson(pathAlbum)

            Promise.all([pr1,pr2])
            .then(values =>{
                resolve(parsers.parseJsonToArtistDetail(
                    values[0],
                    values[1],
                    id,
                    values[0].timeOutDetail,
                    values[1].timeOutAlbums
                ))
            })
            .catch(reject)
        })
    }

    this.albumDetail = function(query){
        return new Promise((resolve, reject) => {
            const path = spotifyUri + 'albums/' + query
            httpGetAsJson(path)
            .then((jsonData, timeOut) => resolve(parsers.parseJsonToAlbumDetail(jsonData, timeOut)))
            .catch(reject)
        })
    }
}

module.exports = ArtistsService