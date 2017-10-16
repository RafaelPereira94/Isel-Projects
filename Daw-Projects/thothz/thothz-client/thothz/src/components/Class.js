import React from 'react'
import Fetcher from './Fetcher.js'
import toSingle from './../model/Single.js'
import { Button, Segment, Header, Icon } from 'semantic-ui-react'
import { Link } from 'react-router-dom'
import config from './../config.js'
import fetch from './../Fetch.js'

export default class Class extends React.Component {
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
                  url: body.course_class_url
                  .replace('{course}', this.props.match.params.course)
                  .replace('{semester}', this.props.match.params.semester)
                  .replace('{id}', this.props.match.params.class)
              }
          )
      })
    }

    render(){
        return ( this.state.url === undefined ? <Icon loading name='spinner' /> :
            <Fetcher url={this.state.url} 
                render={(loading, resp, body) => (
                loading ? 
                <Icon loading name='spinner' />
                : 
                <div>
                    <Header as='h5' attached='top' color="violet" size='huge'> {body.properties.id} </Header>
                    <Segment attached>
                        <b> Course </b> {body.properties.course}
                    </Segment>
                    <Segment attached>
                        <b> Semester </b> {body.properties.semester}
                    </Segment>
                    <Segment attached>
                        <b> Max students per group </b> {body.properties.max} 
                    </Segment>
                    <Segment attached>
                        <Button basic color='violet' 
                            as={Link} 
                            to={
                                config.CLASS_STUDENT_LIST
                                .replace(':course', body.properties.course)
                                .replace(':semester', body.properties.semester)
                                .replace(':class', body.properties.id)
                            }>  
                                Students
                        </Button>
                        <Button basic color='violet' 
                            as={Link} 
                            to={
                                config.CLASS_TEACHER_LIST
                                .replace(':course', body.properties.course)
                                .replace(':semester', body.properties.semester)
                                .replace(':class', body.properties.id)
                            }>  
                                Teachers
                        </Button>
                        <Button basic color='violet'
                        as={Link}
                        to={config.GROUP_LIST
                            .replace(':course', body.properties.course)
                            .replace(':semester', body.properties.semester)
                            .replace(':class', body.properties.id)
                        }>
                            Groups
                        </Button>
                    </Segment>
                </div>
            )} transform={toSingle}/>
        )
    }
}