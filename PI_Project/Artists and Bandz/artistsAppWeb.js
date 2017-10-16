"use strict";

const express = require('express')
const path = require('path')
const expressSession = require('express-session')
const bodyParser = require('body-parser')
const cookieParser = require('cookie-parser')
const passport = require('passport')
const passportStrategy = require('passport-local').Strategy

//middlewares that attend the requests: cacheController, redirects to artistsController
const routes = require('./controller/cacheController')

//configurations for passport
const usersService = require('./model/services/usersService.js')

const server = express()

const hbs = require('hbs')
//path to serve the hbs views
server.set('views', path.join(__dirname, 'views'))
server.set('view engine', 'hbs');
//register all partials in folder partials
hbs.registerPartials(__dirname + '/views/partials')

passport.use(new passportStrategy((username, password, cb) => {
        usersService.authenticate(
            username, 
            password,
            cb
        )
}))
passport.deserializeUser((user, cb) => {
    usersService.find(user, cb)
})
passport.serializeUser((user, cb) => {
    cb(null, user)
})

server.get('/', (req, res) => res.redirect('/home'))

//path to serve static resources
server.use(express.static(path.join(__dirname,'public')))

server.use(cookieParser())
server.use(bodyParser())
server.use(expressSession({ secret: 'keyboard cat' }))

server.use(passport.initialize())
server.use(passport.session())

/**
 * route to process login request
 * uses passport with the Local Strategy
 * redirects to the last page except when that page was the login or signup page
 */
server.post('/login', passport.authenticate('local'), 
    function(req, res){
        if(req.body.back == '/login' || req.body.back == '/signup')
            res.redirect('/myplaylists')
        else
            res.redirect(req.body.back)
    }
)

server.use('/', routes)

//middleware to handle errors
server.use((err, req, resp, next) => {
    const status = err.status || 500
    resp.status(status)
    resp.statusMessage = err.message 
    resp.render('error', {
        'title': 'Oops! Error',
        'message': err.message,
        'status': status
    })
})

//middleware to keep in the same page
server.use((req, resp, next) => {
    if(req.route != null) // if a route wasnt found call next -> 404 not found
        resp.status(200).send()
    else
        next()
})

//middleware to handle 404 errors
server.use((req, resp) => {
    const status = 404
    resp.status(status)
    resp.render('error', {
        'title': 'Oops! Error',
        'message': 'Resource not found',
        'status': status
    })
})


module.exports = server