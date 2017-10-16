"use strict";
const fs = require('fs')
const path = './cache/views/'
const ext = '.txt'
let cache = {}

const myTimeOut = 7200

function put(key, obj, timeOut){
    if(timeOut == null)
        timeOut = myTimeOut
    const data = new Data(obj, timeOut)
    cache[key] = data
    let filename = replaceAll(key, '/', '')
    filename = replaceAll(filename, '?', '')
    fs.writeFile(path + filename + ext, JSON.stringify(data), (err) => {
        if(err) throw err;
    })
}

function get(key){
    const data = cache[key]
    if(data != null){
        if(hasExpired(cache[key].createdTime > cache[key].timeOut)){
            delete cache[key]
            return null
        }
        return data.obj
    }
    let filename = replaceAll(key, '/', '')
    filename = replaceAll(filename, '?', '')
    fs.readFile(path + filename + ext, (err, file) => {
        if (err)
            return null
        return file.obj
    })
}

function Data(obj, timeOut){
    this.obj = obj
    this.createdTime = new Date().getTime() / 1000
    this.timeOut = timeOut
}

function hasExpired(createdTime, timeOut){
    if((new Date().getTime() / 1000) - createdTime > timeOut)
        return true
    return false
}

function replaceAll(str, find, replace) {
  return str.replace(new RegExp(find.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1"), 'g'), replace);
}

module.exports.put = put
module.exports.get = get

