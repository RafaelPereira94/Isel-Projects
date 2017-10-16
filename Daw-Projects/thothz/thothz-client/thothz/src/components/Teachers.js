import React from 'react'
import Fetcher from './Fetcher'
import TeacherForm from './TeacherForm.js'
import { Button, Segment, Header, List, Icon } from 'semantic-ui-react'
import { Link } from 'react-router-dom'
import toCollection from './../model/Collection.js'
import config from './../config.js'
import fetch from './../Fetch.js'

export default class Teachers extends React.Component{
    constructor(props){
        super(props)
        this.state = {}
        this.componentDidMount = this.componentDidMount.bind(this)
    }

    componentDidMount(){
        fetch(config.API_INDEX, {
            method : 'GET',
            headers: {
                'Access-Control-Allow-Origin': '*'
            }
       })
      .then(({resp, body}) => {
          this.setState({url: body.teachers_url})
      })
    }
    
    render(){
        return ( this.state.url === undefined ? <Icon loading name='spinner' /> :
            <Fetcher url={this.state.url} render={(loading, resp, body) => (
                loading ? 
                <Icon loading name='spinner' />
                :
                <div>
                    <Header as='h5' attached='top' color="violet" size='huge'> {body.type} </Header>
                    <Segment attached>
                        { body.items && body.items.map((s, i) =>
                            <div key={i}>
                                <Header as='h5' attached='top' color="violet" size='large'> {s.properties.name} </Header>  
                                <Segment attached key={i}>
                                    <List divided size='medium'>
                                        <List.Item>
                                            <b> Email </b> {s.properties.email} 
                                        </List.Item>
                                        <List.Item>
                                            <b> Number </b> {s.properties.number} 
                                        </List.Item>
                                    </List>
                                    <Button basic color='violet' 
                                    as={Link} 
                                    to={config.TEACHER_DETAIL.replace(':teacher', s.properties.number)}>  
                                        <Icon color='violet' name='add circle' />More details
                                    </Button>
                                </Segment>
                            </div>
                        )}
                        {body.actions && body.actions.find(act => act.name === 'create-teacher') &&
                            <TeacherForm action = {body.actions.find(act => act.name === 'create-teacher')} />
                        }
                    </Segment>
                </div>
            )} transform={toCollection}/>     
        )
    }
}