
const configs = {
    API_INDEX: 'http://localhost:8080/',
    COURSE_LIST: '/courses',
    COURSE_DETAIL: '/courses/:course',
    TEACHER_LIST: '/teachers',
    TEACHER_DETAIL: '/teachers/:teacher',
    TEACHER_CLASSES_LIST: '/teachers/:teacher/classes',
    STUDENT_LIST: '/students',
    STUDENT_DETAIL: '/students/:student',
    STUDENT_CLASSES_LIST: '/students/:student/classes',
    COURSE_SEMESTER_CLASS_LIST: '/courses/:course/semesters/:semester/classes',
    COURSE_CLASS_LIST: '/courses/:course/classes',
    CLASS_DETAIL: '/courses/:course/semesters/:semester/classes/:class',
    GROUP_LIST: '/courses/:course/semesters/:semester/classes/:class/groups',
    GROUP_DETAIL: '/courses/:course/semesters/:semester/classes/:class/groups/:group',
    CLASS_STUDENT_LIST: '/courses/:course/semesters/:semester/classes/:class/students',
    CLASS_TEACHER_LIST: '/courses/:course/semesters/:semester/classes/:class/teachers'
}

export default configs