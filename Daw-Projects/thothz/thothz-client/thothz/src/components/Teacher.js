import React from 'react'
import Fetcher from './Fetcher.js'
import toSingle from './../model/Single.js'
import { Button, Segment, Header, Icon } from 'semantic-ui-react'
import { Link } from 'react-router-dom'
import config from './../config.js'
import fetch from './../Fetch.js'

export default class Teacher extends React.Component{
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
          this.setState({url: body.teacher_url.replace('{number}', this.props.match.params.teacher)})
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
                    <Header as='h5' attached='top' color="violet" size='huge'> {body.type}: {body.properties.name} </Header>
                    <Segment attached>
                        <b> Email </b> {body.properties.email}
                    </Segment>
                    <Segment attached>
                        <b> Number </b> {body.properties.number} 
                    </Segment>
                    <Segment attached>
                        <Button basic color='violet' 
                            as={Link} 
                            to={config.TEACHER_CLASSES_LIST.replace(':teacher', body.properties.number)}>  
                                Classes
                        </Button>
                    </Segment>
                </div>
            )} transform={toSingle}/>
        )
    }
}