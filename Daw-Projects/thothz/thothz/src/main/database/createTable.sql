DROP DATABASE IF EXISTS thothz;

CREATE DATABASE thothz;

\c thothz;

CREATE SCHEMA daw;

CREATE TABLE daw.user(
  id SERIAL PRIMARY KEY,
  name VARCHAR,
  email VARCHAR UNIQUE,
  password VARCHAR
);

CREATE TABLE daw.student(
  number INTEGER PRIMARY KEY,
  email VARCHAR REFERENCES daw.user(email)
);

CREATE TABLE daw.teacher(
  number INTEGER PRIMARY KEY,
  email VARCHAR REFERENCES daw.user(email),
  admin VARCHAR(1) CHECK(admin = 'T' or admin = 'F')
);

CREATE TABLE daw.course(
  name VARCHAR PRIMARY KEY,
  acronym VARCHAR,
  coordinator INTEGER REFERENCES daw.teacher(number)
);

CREATE TABLE daw.semester(
  name VARCHAR PRIMARY KEY,
  year INTEGER,
  season VARCHAR(1) CHECK(season = 'V' or season = 'I')
);

CREATE TABLE daw.class(
  course VARCHAR REFERENCES daw.course(name),
  semester VARCHAR REFERENCES daw.semester(name),
  id VARCHAR,
  max INTEGER,
  PRIMARY KEY (id, course, semester)
);

CREATE TABLE daw.classStudent(
  classId VARCHAR,
  course VARCHAR,
  semester VARCHAR,
  studentNumber INTEGER REFERENCES daw.student(number),
  FOREIGN KEY (course, semester, classId) REFERENCES daw.class(course, semester, id),
  PRIMARY KEY (classId, course, semester, studentNumber)
);

CREATE TABLE daw.classTeacher(
  classId VARCHAR,
  course VARCHAR,
  semester VARCHAR,
  teacherNumber INTEGER REFERENCES daw.teacher(number),
  FOREIGN KEY (course, semester, classId) REFERENCES daw.class(course, semester, id),
  PRIMARY KEY (classId, course, semester, teacherNumber)
);

CREATE TABLE daw.group(
  classId VARCHAR,
  course VARCHAR,
  semester VARCHAR,
  number INTEGER,
  FOREIGN KEY (classId, course, semester) REFERENCES daw.class(id, course, semester),
  PRIMARY KEY (classId, course, semester, number)
);

CREATE TABLE daw.groupStudent(
  classId VARCHAR,
  course VARCHAR,
  semester VARCHAR,
  groupNumber INTEGER,
  studentNumber INTEGER REFERENCES daw.student(number),
  FOREIGN KEY (classId, course, semester, groupNumber) REFERENCES daw.group(classId, course, semester, number),
  PRIMARY KEY (classId, course, semester, groupNumber, studentNumber)
);