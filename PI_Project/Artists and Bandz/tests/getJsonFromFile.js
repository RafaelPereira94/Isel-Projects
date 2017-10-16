"use strict";

const albumDet = require('./albumDetail.json');
const artistDet = require('./artistDetail.json');
const artistDetAlb = require('./artistDetailAlbum.json');
const artistInfo = require('./artistInfo.json');

function getAlbumDetail(path, cb){
 if(path != 'https://api.spotify.com/v1/albums/2rBb9rAEuMC8VH9uk7js3e')
 throw new Error('Invalid path')
 cb(null, albumDet)
 }

 function getArtistDetail(path, cb){
     if(path == 'https://api.spotify.com/v1/artists/0OdUWJ0sBjDrqHygGUXeCF')
        cb(null,artistDet)
     else if(path == 'https://api.spotify.com/v1/artists/0OdUWJ0sBjDrqHygGUXeCF/albums?undefined'){
         cb(null,artistDetAlb)
     }
     else{
         throw new Error('Invalid path')
     }
 }

function getArtistsInfo(path, cb){
 if(path != 'https://api.spotify.com/v1/search?q=karetus&undefined&type=artist')
 throw new Error('Invalid path')
 cb(null, artistInfo)
 }
module.exports.getArtistsInfo = getArtistsInfo
module.exports.getArtistDetail = getArtistDetail
module.exports.getAlbumDetail = getAlbumDetail


