function ajaxRequest(meth, path, data) {
    return fetch(path, {
        method: meth,
        headers: {'Content-Type': 'application/json'},
        body: data,
        credentials: 'same-origin'
    })
    .then(resp => {
        if(resp.status != 200)
            throw new Error(resp.statusText)
        return resp.text()
    })
}

function tracksHandler(playlistId, trackId, meth){
    if(meth === 'POST'){
        const path = '/myplaylists/' + playlistId + '/addTrack/' + trackId
        ajaxRequest('POST', path)
            .then(data => alertify.success('Track added with success'))
            .catch(err => alertify.error(err.message))
    }
    else if(meth === 'DELETE'){
        const path = '/myplaylists/' + playlistId + '/deleteTrack/' + trackId
        ajaxRequest('DELETE', path)
            .then(data =>{
                let tbody = document.getElementById('tbody-' + playlistId)
                let track = document.getElementById('track-' + trackId)
                tbody.removeChild(track)
                alertify.success('Track deleted with success')
            })
            .catch(err => alertify.error(err.message))
    }
}

function renamePlaylist(id){
    const name = document.getElementById('name-' + id)
    const path = '/renameplaylist'
    const newName = document.getElementById('newname-' + id).value
    ajaxRequest('PUT', path, JSON.stringify({'id': id, 'name': newName}))
        .then(data => {
            alertify.success('Playlist ' + name.innerHTML + ' renamed to ' + newName + ' with success')
            name.innerHTML = newName
        })
        .catch(err => alertify.error(err.message))
}

function deleteplaylist(id){
    const page = document.getElementById('playlists')
    const playlist = document.getElementById(id)

    const path = '/deleteplaylist/' + id
    ajaxRequest('DELETE', path)
        .then(data => {
            page.removeChild(playlist)
            alertify.success('Playlist deleted with success')
        })
        .catch(err => alertify.error(err.message))
}

function createplaylist(){
    const name = document.getElementById('playlistName').value
    const path = '/createplaylist/' + name
    ajaxRequest('POST', path)
        .then(playlist => {
            const page = document.getElementById('playlists')
            page.appendChild(stringToHtml(playlist))
            alertify.success('Playlist created with success')
        })
        .catch(err => alertify.error(err.message))
}

function deletesharedplaylist(id){
    const page = document.getElementById('sharedPlaylists')
    const playlist = document.getElementById(id)

    const path = '/deletesharedplaylist/' + id
    ajaxRequest('DELETE', path)
        .then(data => {
            page.removeChild(playlist)
            alertify.success('Shared playlist deleted with success')
        })
        .catch(err => alertify.error(err.message))
}

function sendInvite(playlistId){
    const username = document.getElementById('user-' + playlistId).value
    const checkbox = document.getElementById('perm-' + playlistId).checked
    const path = '/sendinvite/' + playlistId + '/user/' + username + '/permission/' + checkbox
    ajaxRequest('POST', path)
        .then(data => {
            alertify.success('Playlist shared with success')
        })
        .catch(err => alertify.error(err.message))
}

function handleInvite(accept, playlistId){
    const invites = document.getElementById('invites')
    const myInvite = document.getElementById('invite-' + playlistId)
    if(accept){
        const path = '/acceptinvite'
        ajaxRequest('PUT', path, JSON.stringify({'idPl': playlistId}))
            .then(data => {
                invites.removeChild(myInvite)
                alertify.success('Playlist share accepted with success')
            })
            .catch(err => alertify.error(err.message))
    }else{
        const path = '/declineinvite'
        ajaxRequest('PUT', path, JSON.stringify({'idPl': playlistId}))
            .then(data => {
                invites.removeChild(myInvite)
                alertify.success('Playlist share declined with success')
            })
            .catch(err => alertify.error(err.message))
    }
}

function changePermission(toUser, id){
    const path = '/changepermission'
    ajaxRequest('PUT', path, JSON.stringify({'idPl': id, 'toUser': toUser}))
        .then(data => {
            alertify.success('Permission changed')
        })
        .catch(err => alertify.error(err.message))
}

function stopSharingWith(toUser, id){
    const page = document.getElementById('sendInvites')
    const invite = document.getElementById('invite-' + id)

    const path = '/stopsharing/' + id + '/user/' + toUser
    ajaxRequest('DELETE', path)
        .then(data => {
            page.removeChild(invite)
            alertify.success('Stop sharing the playlist with ' + toUser)
        })
        .catch(err => alertify.error(err.message))
}

function stringToHtml(str) {
    const div = document.createElement('div')
    div.innerHTML = str
    return div.firstChild
}