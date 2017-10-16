package pt.isel.daw.model

class Student(
        var number : Int,
        name: String,
        email: String,
        id: Int,
        var password: String
) : User(id, name, email)

