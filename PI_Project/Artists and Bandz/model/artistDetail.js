"use strict";

function ArtistDetail(albums, genres, name, image, total, next, prev, first, last, timeOutDetail, timeOutAlbums) {
    this.albums = albums
    this.genres = genres
    this.name = name
    this.image = image
    this.next = next
    this.prev = prev
    this.first = first
    this.last = last
    this.total = total
    this.timeOutDetail = timeOutDetail
    this.timeOutAlbums = timeOutAlbums
}

module.exports = ArtistDetail