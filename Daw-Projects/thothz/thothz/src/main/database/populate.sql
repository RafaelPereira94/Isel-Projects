insert into daw.user (name, email, password) VALUES 
('rafael','email1', 123),
('ines','email2', 123),
('carmelinda','email3', 123),
('maria','email4', 123),
('rui','email5', 123),
('pedro','email6', 123);

insert into daw.student(number, email) VALUES 
(41666, 'email1'),
(40577, 'email2'),
(41856, 'email4'),
(42574, 'email5');

insert into daw.teacher(number, email, admin) VALUES 
(32589, 'email3', 'T'),
(40256, 'email6', 'F');

insert into daw.course (name, acronym, coordinator) VALUES 
('SoftwareLaboratory', 'LS', 40256),
('Programming', 'PG', 32589);

insert into daw.semester (name, year, season) values
('1415v', 1415, 'V'),
('1415i', 1415, 'I'),
('1516v', 1416, 'V');

insert into daw.class (course, semester, id, max) values
('SoftwareLaboratory', '1415v', 'D1', 3), 
('SoftwareLaboratory', '1415v', 'D2', 3),
('SoftwareLaboratory', '1415v', 'N1', 3),
('Programming', '1415v', 'D1', 2),
('Programming', '1415v', 'D2', 2),
('Programming', '1415v', 'N1', 3),
('Programming', '1415i', 'D1', 3),
('Programming', '1415i', 'D2', 3),
('Programming', '1415i', 'N1', 3),
('SoftwareLaboratory', '1415i', 'D2', 3),
('SoftwareLaboratory', '1516v', 'N1', 2);

insert into daw.classStudent (classId, course, semester, studentNumber) values
('D1', 'SoftwareLaboratory', '1415v', 41666),
('D1', 'SoftwareLaboratory', '1415v', 40577),
('D1', 'SoftwareLaboratory', '1415v', 41856),
('D1', 'SoftwareLaboratory', '1415v', 42574),
('D1', 'Programming', '1415v', 41666),
('D1', 'Programming', '1415v', 40577),
('D1', 'Programming', '1415v', 41856),
('D1', 'Programming', '1415v', 42574);

insert into daw.classTeacher (classId, course, semester, teacherNumber) values
('D1', 'SoftwareLaboratory', '1415v', 32589),
('D1', 'SoftwareLaboratory', '1415v', 40256),
('D1', 'Programming', '1415v', 32589);

insert into daw.group (classId, course, semester, number) values
('D1', 'SoftwareLaboratory', '1415v', 1), 
('D1', 'SoftwareLaboratory', '1415v', 2), 
('D1', 'SoftwareLaboratory', '1415v', 3),
('D1', 'SoftwareLaboratory', '1415v', 4);

insert into daw.groupStudent (classId, course, semester, groupNumber, studentNumber) values
('D1', 'SoftwareLaboratory', '1415v', 1, 41666),
('D1', 'SoftwareLaboratory', '1415v', 1, 40577), 
('D1', 'SoftwareLaboratory', '1415v', 2, 41856),
('D1', 'SoftwareLaboratory', '1415v', 2, 42574);