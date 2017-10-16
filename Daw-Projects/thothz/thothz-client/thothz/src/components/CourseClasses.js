import React from 'react'
import { Icon } from 'semantic-ui-react'
import config from './../config.js'
import fetch from './../Fetch.js'
import Classes from './Classes.js'

export default class CourseClasses extends React.Component {
    constructor(props){
        super(props)
        this.state = {}
        this.componentDidMount = this.componentDidMount.bind(this)
    }

    componentDidMount(){
        fetch(config.API_INDEX, {
            method : 'GET',
            headers: {
                'Access-Control-Allow-Origin':'*'
            }
       })
      .then(({resp, body}) => {
          console.log(body)
          this.setState(
              {
                  url: body.course_classes_url
                    .replace('{course}', this.props.match.params.course)
                    .replace('{semester}', 'all')
              }
          )
      })
    }

    render(){
        return ( this.state.url === undefined ? <Icon loading name='spinner' /> : <Classes url={this.state.url} /> )
    }
}