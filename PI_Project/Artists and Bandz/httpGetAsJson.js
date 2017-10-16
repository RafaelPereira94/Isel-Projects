"use strict";

const http = require('https')

/**
 * Does a HTTP get request
 */
module.exports = function(path){
    const promise = new Promise((resolve, reject) => {
        http.get(path, (resp) => {
            let res = ''
            resp.on('error', err => reject(new Error("erro")))
            resp.on('data', chunck => res += chunck.toString())
            resp.on('end', () => resolve(JSON.parse(res)))
        })
    })
    return promise
}