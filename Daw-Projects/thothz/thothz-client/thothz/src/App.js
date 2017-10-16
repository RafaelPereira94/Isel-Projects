import React from 'react'
import Home from './components/Home.js'
import Courses from './components/Courses.js'
import Course from './components/Course.js'
import CourseClasses from './components/CourseClasses.js'
import StudentClasses from './components/StudentClasses.js'
import TeacherClasses from './components/TeacherClasses.js'
import Teachers from './components/Teachers.js'
import Teacher from './components/Teacher.js'
import Navbar from './components/Navbar.js'
import Students from './components/Students.js'
import Student from './components/Student.js'
import Class from './components/Class.js'
import GroupList from './components/GroupList.js'
import ClassStudents from './components/ClassStudents.js'
import ClassTeachers from './components/ClassTeachers.js'
import config from './config.js'
import GroupDetail from './components/GroupDetail.js'

import {
  BrowserRouter as Router,
  Switch,
  Route
} from 'react-router-dom'

const App = () => (
    <Router>
      <div>
        <Navbar />
        <Switch>
          <Route exact path='/' component={Home}/>
          <Route exact path={config.COURSE_LIST} component={Courses}/>
          <Route exact path={config.COURSE_DETAIL} component={Course}/>
          <Route exact path={config.TEACHER_LIST} component={Teachers}/>
          <Route exact path={config.TEACHER_DETAIL} component={Teacher}/>
          <Route exact path={config.STUDENT_LIST} component={Students}/>
          <Route exact path={config.STUDENT_DETAIL} component={Student}/>
          <Route exact path={config.COURSE_CLASS_LIST} component={CourseClasses}/>
          <Route exact path={config.STUDENT_CLASSES_LIST} component={StudentClasses}/>
          <Route exact path={config.TEACHER_CLASSES_LIST} component={TeacherClasses}/>
          <Route exact path={config.CLASS_DETAIL} component={Class}/>
          <Route exact path={config.GROUP_LIST} component={GroupList}/>
          <Route exact path={config.CLASS_STUDENT_LIST} component={ClassStudents}/>
          <Route exact path={config.CLASS_TEACHER_LIST} component={ClassTeachers}/>
          <Route exact path={config.GROUP_DETAIL} component={GroupDetail}/>
        </Switch>
      </div>
    </Router>
)

export default App
