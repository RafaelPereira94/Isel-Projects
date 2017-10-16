import React from 'react'
import {Header,Icon,Segment,List} from 'semantic-ui-react'
import config from './../config.js'
import Fetcher from './Fetcher.js'
import fetch from './../Fetch.js'
import toCollection from './../model/Collection.js'

//show students and group number.
class GroupDetail extends React.Component{

    constructor(props){
        super(props)
        this.state = {}
    }

    componentDidMount(){
        //assemble students
        fetch(config.API_INDEX,{
            method:'GET',
            headers: {
                'Access-Control-Allow-Origin':'*'
            }
        })
        .then(({resp, body}) => {
            console.log(body)
            this.setState({
                url: body.class_group_students_url
                .replace('{course}',this.props.match.params.course)
                .replace('{semester}',this.props.match.params.semester)
                .replace('{class}',this.props.match.params.class)
                .replace('{number}',this.props.match.params.group),
            })
        })
    }

    render(){
        return(
            
        this.state.url === undefined ? <Icon loading name='spinner'/> : 
            <Fetcher url={this.state.url} render={(loading,resp,body)=>( loading ? 
                <Icon loading name='spinner' /> :
                 <div>
                     <Header as='h4' attached='top' color="violet" size='huge'>Group number: {this.props.match.params.group} </Header>
               
                    <Header as='h5' attached='top' color="violet" size='huge'>Students: </Header> 
                    <Segment attached>
                        {body.items.map((s,i)=>
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
                                </Segment>
                            </div>
                        )}
                    </Segment>
                </div>
            )} transform={toCollection}/>
        )
    }
}

export default GroupDetail