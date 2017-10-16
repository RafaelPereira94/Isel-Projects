import React from 'react'
import {Header, Form, Button, Segment} from 'semantic-ui-react'
import fetch from './../Fetch.js'
import qs from 'query-string'

export default class MyForm extends React.Component{

    constructor(props){
        super(props)
        this.post = this.post.bind(this)
        this.onChange = this.onChange.bind(this)
        this.state = {inputs: this.props.action.fields}
    }

    post(e){
        let props = this.state.inputs
        let data = {}

        props.forEach(field => data[field.name] = field.value)

        
       fetch(this.props.action.href, {
            method : 'post',
            headers: {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body : qs.stringify(data)
       })
    }

    onChange(e, {name, value}){
        let ipts = this.state.inputs
        let field = ipts.find(field => field.name === name)
        field.value =  value
        this.setState({inputs : ipts})
    }

    render(){
        const {action} = this.props
        return (
            <div>
                <Header as='h5' attached='top' color="violet" size='huge'> {action.name} </Header>
                    <Segment attached>
                        <Form>
                            {   
                                action.fields && 
                                action.fields.map((field, i) =>
                                    <Form.Input 
                                        key={i}
                                        label={field.name + ':'} 
                                        name={field.name} 
                                        type={field.type} 
                                        value={field.value}
                                        onChange={this.onChange}
                                    />
                                )
                            }
                        </Form>
                        <Button onClick={this.post}> Create </Button>
                    </Segment>
            </div>
        )
    }
}
