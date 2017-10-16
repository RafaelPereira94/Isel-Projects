"use strict;"
const AlbumDetail = require('./../model/albumDetail.js')
const Album = require('./../model/album.js')
const ArtistDetail =require('./../model/artistDetail.js')
const ArtistInfo = require('./../model/artistsInfo.js')
const Artist= require('./../model/artist.js')
const Track= require('./../model/track.js')
const ArtistService  = require('./../model/artistsService.js')
const httpGetAsJson = require('./getJsonFromFile.js')

module.exports.testArtistInfo = function(test){
   const artService = new ArtistService(httpGetAsJson.getArtistsInfo)
   artService.artistsInfo('karetus',undefined,(err,artist)=>{
      test.ok(artist instanceof ArtistInfo)
      test.ok(artist.artists[0] instanceof Artist)
      test.equal('Karetus',artist.artists[0].name)
      test.equal('5SVGhU7cTodC79weaAmjYy',artist.artists[0].id)
      test.done()
   })
}
module.exports.testAlbumDetail = function(test){
   const artService = new ArtistService(httpGetAsJson.getAlbumDetail)
   artService.albumDetail('2rBb9rAEuMC8VH9uk7js3e','',(err,album)=>{
      test.ok(album instanceof AlbumDetail)
      test.ok(album.tracks[0] instanceof Track)
      test.equal('The Truth About Love',album.name)
      test.equal('Are We All We Are',album.tracks[0].name)
      test.equal('Blow Me (One Last Kiss)',album.tracks[1].name)
      test.equal('Try',album.tracks[2].name)
      test.done()
   })
}
module.exports.testArtistDetail = function(test){
   const artService = new ArtistService(httpGetAsJson.getArtistDetail)
   artService.artistDetail('0OdUWJ0sBjDrqHygGUXeCF',undefined,(err,artist)=>{
      test.ok(artist  instanceof ArtistDetail)
      test.ok(artist.albums[0]  instanceof Album)
      test.equal('Band of Horses',artist.name)
      test.equal('Why Are You OK',artist.albums[0].name)
      test.equal('Why Are You OK',artist.albums[1].name)
      test.equal('Acoustic at The Ryman (Live)',artist.albums[3].name)
      test.done()
   })
}
