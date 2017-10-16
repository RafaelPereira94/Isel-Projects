import React from 'react'
import { Menu, Icon } from 'semantic-ui-react' 
import { NavLink } from 'react-router-dom'
import config from './../config.js'

export default class navbarInstance extends React.Component{

  render(){
    return (
      <Menu stackable size='massive' color='violet'>
        <Menu.Item as={NavLink} exact to='/'>
          <Icon color='violet' name='home'/> Thothz
        </Menu.Item>
        <Menu.Item as={NavLink} exact to={config.COURSE_LIST}>
          Courses
        </Menu.Item>
        <Menu.Item as={NavLink} exact to={config.STUDENT_LIST}>
          Students
        </Menu.Item>
        <Menu.Item as={NavLink} exact to={config.TEACHER_LIST}>
          Teachers
        </Menu.Item>
        <Menu.Item position='right' color='violet'>
          <Icon color='violet' name='user' /> Welcome!
        </Menu.Item>
      </Menu>
    )
  }
}