CREATE DATABASE online_quiz;
USE online_quiz;

CREATE TABLE quizzes (
    quiz_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    title TEXT,
    detail TEXT
);

CREATE TABLE questions (
    question_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    quiz_id INTEGER,
    title TEXT,
    detail TEXT,
    picture TEXT
);

CREATE TABLE choices (
    choice_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    question_id INTEGER,
    text TEXT,
    is_answer BOOLEAN
);

CREATE TABLE users (
    user_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name TEXT,
    password TEXT,
    email TEXT
);

CREATE TABLE user_guesses (
    user_id INTEGER,
    quiz_id INTEGER,
    question_id INTEGER,
    choice_id INTEGER,
    PRIMARY KEY(user_id, quiz_id, question_id)
);
