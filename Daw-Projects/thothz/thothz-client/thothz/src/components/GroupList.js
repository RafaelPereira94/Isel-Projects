import React from 'react'
import Fetcher from './Fetcher.js'
import fetch from './../Fetch.js'
import { List,Segment,Icon,Header,Button } from 'semantic-ui-react'
import config from './../config.js'
import toCollection from './../model/Collection.js'
import {Link} from 'react-router-dom'
import MyForm from './Form.js'

class GroupList extends React.Component{
    constructor(props){
        super(props)
        this.state = {teacher : true}  //efeito de teste como se fosse um teacher ou coordinator autenticado
        this.componentDidMount = this.componentDidMount.bind(this);
        this.deleteGroup = this.deleteGroup.bind(this)
    }

    componentDidMount(){
        fetch(config.API_INDEX,{
            method:'GET',
            headers: {
                'Access-Control-Allow-Origin':'*'
            }
        })
        .then(({resp, body}) => {
            console.log(body)
            this.setState({
                url: body.class_groups_url
                .replace('{course}',this.props.match.params.course)
                .replace('{semester}',this.props.match.params.semester)
                .replace('{class}',this.props.match.params.class),
                teacher: true
                
            })
            console.log(this.state.url)
        })
    }

    deleteGroup(number,event){
        fetch(this.state.url+'/'+number,{
            method:'delete',
        }).then(({resp, body}) => resp.json())
    }

    render(){
        return(
            this.state.url === undefined ? <Icon loading name='spinner'/> :
            <Fetcher url={this.state.url} render={(loading,resp,body)=>( loading ? 
                <Icon loading name='spinner' /> :
                <div>
                    <Header as='h5' attached='top' color="violet" size='huge'>Groups:</Header>
                    <p>This page shows all groups for this class</p>
                    <Segment attached>
                        { body.items && body.items.map((grp,i) =>
                            <div key={i}>
                                <Segment attached key={i}>
                                    <List divided size='medium'>
                                        <List.Item>
                                            <b> Group number: </b>    {grp.properties.number}
                                        </List.Item>
                                    </List>
                                    <Button.Group>
                                    <Button basic color='violet' as={Link} to={
                                        config.GROUP_DETAIL
                                            .replace(':course', grp.properties.course)
                                            .replace(':semester', grp.properties.semester)
                                            .replace(':class', grp.properties.classId)
                                            .replace(':group', grp.properties.number)
                                    }>
                                        <Icon color='violet' name='add circle' />More details
                                    </Button>
                                    
                                    {this.state.teacher &&
                                        <Button negative basic color='red' onClick={this.deleteGroup.bind(this,grp.properties.number)}> 
                                            <Icon color='red' name='delete' /> Delete group.
                                        </Button>
                                    }
                                    </Button.Group>

                                </Segment>
                            </div>
                        )}
                    </Segment>
                    {body.actions.find(act => act.name === 'add-group') &&
                        <div>
                            <MyForm  action={body.actions.find(act => act.name === 'add-group')} />
                        </div>
                    }
                </div>
                )} transform={toCollection} />
        )
    }
}

export default GroupList