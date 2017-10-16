"use strict";

function Playlist(id, name, write, tracks) {
    this.id = id
    this.name = name
    this.write = write
    this.tracks = tracks
}

module.exports = Playlist