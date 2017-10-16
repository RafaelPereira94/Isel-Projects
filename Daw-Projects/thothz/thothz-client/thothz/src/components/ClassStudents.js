import React from 'react'
import Fetcher from './Fetcher'
import MyForm from './Form.js'
import { Button, Segment, Header, List, Icon } from 'semantic-ui-react'
import { Link } from 'react-router-dom'
import toCollection from './../model/Collection.js'
import config from './../config.js'
import fetch from './../Fetch.js'

export default class ClassStudents extends React.Component{
    constructor(props){
        super(props)
        this.state = {}
        this.componentDidMount = this.componentDidMount.bind(this)
        this.deleteStudent = this.deleteStudent.bind(this)
    }

    componentDidMount(){
        fetch(config.API_INDEX, {
            method : 'GET',
            headers: {
                'Access-Control-Allow-Origin': '*'
            }
       })
      .then(({resp, body}) => {
          this.setState({
              url: body.class_students_url
              .replace('{course}', this.props.match.params.course)
              .replace('{semester}', this.props.match.params.semester)
              .replace('{id}', this.props.match.params.class)
          })
      })
    }
    
    deleteStudent(url,evt){
        fetch(url,{method:'delete'})
        .then(({resp, body}) => resp.json())
    }

    render(){
        return ( this.state.url === undefined ? <Icon loading name='spinner' /> :
            <Fetcher url={this.state.url} render={(loading, resp, body) => (
                loading ? 
                <Icon loading name='spinner' />
                :
                <div>
                    <Header as='h5' attached='top' color="violet" size='huge'> Class Students </Header>
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
                                    <Button.Group>
                                    <Button basic color='violet' 
                                    as={Link} 
                                    to={config.STUDENT_DETAIL.replace(':student', s.properties.number)}>  
                                        <Icon color='violet' name='add circle' />More details
                                    </Button>
                                    <Button negative basic color='red' onClick={this.deleteStudent.bind(this,s.actions.find(act => act.name === 'delete-student').href)}> 
                                            <Icon color='red' name='delete' /> Delete Student.
                                        </Button>
                                    </Button.Group>
                                </Segment>
                            </div>
                        )}

                    </Segment>
                    {body.actions.find(act => act.name==='add-student') && 
                        <MyForm action={body.actions.find(act => act.name==='add-student')} />
                    }
                </div>
            )} transform={toCollection}/>     
        )
    }
}