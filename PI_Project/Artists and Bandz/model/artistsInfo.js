"use strict";

//objecto de dominio
function ArtistsInfo(query, artistArray, total, next, prev, first, last) {
    this.query = query
    this.artists = artistArray
    this.next = next
    this.prev = prev
    this.first = first
    this.last = last
    this.total = total
}

module.exports = ArtistsInfo