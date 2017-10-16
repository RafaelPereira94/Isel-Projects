import React from 'react'
import Fetcher from './Fetcher.js'
import { Button, Segment, Header, List, Icon } from 'semantic-ui-react'
import { Link } from 'react-router-dom'
import toCollection from './../model/Collection.js'
import config from './../config.js'

export default class Classes extends React.Component {

    render(){
        return (
            <Fetcher url={this.props.url} render={(loading, resp, body) => (
                loading ? 
                <Icon loading name='spinner' />
                :
                <div>
                    <Header as='h5' attached='top' color="violet" size='huge'> {body.type} </Header>
                    <Segment attached>
                        { body.items && body.items.map((s, i) =>
                            <div key={i}>
                                <Header as='h5' attached='top' color="violet" size='large'> {s.properties.id} </Header>  
                                <Segment attached key={i}>
                                    <List divided size='medium'>
                                        <List.Item>
                                            <b> Semester  </b>    {s.properties.semester}
                                        </List.Item>
                                        <List.Item>
                                            <b> Course  </b>    {s.properties.course} 
                                        </List.Item>
                                    </List>
                                    <Button basic color='violet' as={Link} to={
                                        config.CLASS_DETAIL
                                        .replace(':course', s.properties.course)
                                        .replace(':semester', s.properties.semester)
                                        .replace(':class', s.properties.id)
                                    }>  
                                        <Icon color='violet' name='add circle' />More details
                                    </Button>
                                </Segment>
                            </div>
                        )}
                    </Segment>
                </div>
            )} transform={toCollection}/>     
        )
    }
}