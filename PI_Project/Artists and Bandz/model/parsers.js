"use strict";

const ArtistsInfo = require('./artistsInfo.js')
const ArtistDetail = require('./artistDetail.js')
const Artist = require('./artist.js')
const Album = require('./album.js')
const AlbumDetail = require('./albumDetail.js')
const Track = require('./track.js')

const noImage = '/public/images/noImage.png'

module.exports = {
    parseJsonToAlbumDetail,
    parseJsonToArtistInfo,
    parseJsonToArtistDetail
}

function parseJsonToAlbumDetail(stringJson, timeOut){
    const album = stringJson
    let artistsArray = []
    let image = noImage
    if(album.images != null)
        image = album.images[0].url

    for(let i = 0; album.artists[i] != null; ++i){
        const art = album.artists[i]
        artistsArray.push(new Artist(art.id, null, art.name, null, null))
    }

    let tracksArray = []
    for(let i = 0; album.tracks.items[i] != null; ++i){
        const items = album.tracks.items[i]
        tracksArray.push(new Track(items.name, items.id,items.preview_url))
    }
    return new AlbumDetail(album.name, image, artistsArray, tracksArray, timeOut)
}

//parse à string json e construct ArtistInfo
function parseJsonToArtistInfo(stringJson, query){
    const arts = stringJson.artists
    let artistArray = []
    let image = noImage

    for(let i = 0; arts.items[i] != null ; ++i){
        let genresArray = []
        const items = arts.items[i]
        const id = items.id
        if(items.images[0] != null)
            image = items.images[0].url
        const name = items.name
        const detailUrl = '/artists/' + id
        genresArray.push(items.genres)
        artistArray.push(
            new Artist(id, image, name, detailUrl, genresArray)
        )
    }

    const baseUrl = '/search/' + query + '/'
    const artistInf = new ArtistsInfo(query, artistArray, null, null, null, null, null)
    resolveLinks(baseUrl, artistInf, arts)
    return artistInf
}


function parseJsonToArtistDetail(detailStringJson, albumsStringJson, query, timeOutDetail, timeOutAlbums){
    const detail = detailStringJson
    const name = detail.name
    let image = noImage
    if(detail.images[0] != null)
        image = detail.images[0].url

    const albums = albumsStringJson
    let albumsArray = []
    for(let i = 0; albums.items[i] != null; ++i){
        const items = albums.items[i]
        const detailUrl = '/album/' + items.id
        albumsArray.push(
            new Album(items.name, detailUrl)
        )
    }
    const baseUrl = '/artists/' + query + '/'
    const artistDet = new ArtistDetail(albumsArray, detail.genres, name, image, null, null, null, null, null, timeOutDetail, timeOutAlbums)
    resolveLinks(baseUrl, artistDet, albums)
    return artistDet
}

function resolveLinks(baseUrl, obj, item, query){
    const total = item.total
    const limit = item.limit
    const offset = item.offset
    baseUrl += '?limit=' + limit + '&offset='

    //only one page to present
    if(total < limit){
        obj.total = 1
        obj.first = baseUrl + 0
        return
    }
    const next = limit + offset

    let prev = null
    if((offset-limit) >= 0)
        prev = offset - limit
    const last = total - limit

    const firstUrl = baseUrl + 0
    const lastUrl = baseUrl + last

    let nextUrl
    if(next > total)
        nextUrl = lastUrl
    if(next < total)
        nextUrl = baseUrl + next

    let prevUrl
    if(prev < 0)
        prevUrl = null
    if(prev != null){
        if(prev >= 0)
            prevUrl = baseUrl + prev
    }

    obj.total = Math.ceil(total / limit)
    obj.next = nextUrl
    obj.prev = prevUrl
    obj.first = firstUrl
    obj.last = lastUrl
}