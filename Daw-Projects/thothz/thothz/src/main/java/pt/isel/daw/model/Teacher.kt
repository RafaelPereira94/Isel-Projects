package pt.isel.daw.model


class Teacher(
        val number : Int,
        name: String,
        email: String,
        id: Int,
        var admin: Boolean,
        var password: String
) : User(id, name, email)