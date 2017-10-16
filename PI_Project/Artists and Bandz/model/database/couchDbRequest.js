"use strict";
const http = require('http')

function getOptions(path){
    return {
        url: 'http://127.0.0.1:5984',
        port: 5984,
        path: path,
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    }   
}

function postOptions(path, body){
    return {
        url: 'http://127.0.0.1:5984',
        port: 5984,
        path: path,
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Length': body.length,
            'Content-Type': 'application/json'
        }
    }   
}

function putOptions(path){
    return {
        url: 'http://127.0.0.1:5984',
        port: 5984,
        path: path,
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    }   
}

function deleteOptions(path, rev){
    return {
        url: 'http://127.0.0.1:5984',
        port: 5984,
        path: path + '/?rev=' + rev,
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
        }
    }
}

function request(body, options){
    return new Promise((resolve, reject) =>{
        const req = http.request(options, (resp) => {
            resp.setEncoding('utf8')
            let data = ''
            resp.on('data', (chunk) => data += chunk)
            resp.on('end', () => {
                const resp = JSON.parse(data)
                if(resp.error)
                    return reject(resp.error)
                resolve(resp)
            })
        })
        req.on('error', (err) => {
            reject(err)
        })
        if(body != null)
            req.write(body)
        req.end()
    })
}

module.exports = {
    find,
    get,
    getAll,
    getAllIds,
    post,
    postAll,
    put,
    del
}

function find(path){
    return new Promise((resolve, reject) => {
        request(null, getOptions(path))
        .then(resolve)
        .catch(err => resolve(null))
    })
}

function get(path){
    return new Promise((resolve, reject) => {
        request(null, getOptions(path))
        .then(resolve)
        .catch(reject)
    })
}

function getAll(path){
    const pathAll = path + '/_all_docs?include_docs=true'
    return new Promise((resolve, reject) => {
        request(null, getOptions(pathAll))
        .then(data => resolve(data.rows.map(obj => obj.doc)))
        .catch(reject)
    })
}

function getAllIds(path, ids){
    const pathAll = path + '/_all_docs?include_docs=true'
    const body = JSON.stringify({"keys" : ids})
    return new Promise((resolve, reject) => {
        request(body, postOptions(pathAll, body))
        .then(data => resolve(data.rows.map(obj => obj.doc)))
        .catch(reject)
    })
}

function post(path, body){
    const content = JSON.stringify(body)
    return new Promise((resolve, reject) => {
        request(content, postOptions(path, content))
        .then(resolve)
        .catch(reject)
    })
}

function postAll(path, body){
    const pathAll = path + '/_bulk_docs'
    const content = JSON.stringify({'docs': body})
    return new Promise((resolve, reject) => {
        request(content, postOptions(pathAll, content))
        .then(resolve)
        .catch(reject)
    })
}

function put(path, body){
    return new Promise((resolve, reject) => {
        request(JSON.stringify(body), putOptions(path))
        .then(resolve)
        .catch(reject)
    })
}

function del(path, rev){
    return new Promise((resolve, reject) => {
        request(null, deleteOptions(path, rev))
        .then(resolve)
        .catch(reject)
    })
}

