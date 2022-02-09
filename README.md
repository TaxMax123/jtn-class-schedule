# Class Schedule

This project was done by [Authors](#authors) as class project for college course "Programing in Java", none of us has
ever worked in Java, everything regarding login is made as proof of concept as we were asked to add that part to out
project as well, so it might not be perfect. Originally we designed project to have microservice architecture, but we
were later notified it had to be monolith, so we combined what we had and reworked some parts to fit in new picture

---

## Table of Contents

* [General info](#general-info)
* [Features](#features)
    * [View](#schedule-view)
    * [Query](#query-available-classrooms)
    * [Reports](#reports-endpoint)
    * [Admin](#admin-endpoint)
* [Technologies & languages](#technologies-&-languages)
* [Requirements](#requirements)
* [Deploy DB](#docker-deploy-database)
* [Project structure](#project-structure)
* [Conclusion](#conclusion)
* [Tips](#problems-encountered)
* [Authors](#authors)

## General info

---
Scheduling application with month view, week view, filters and query form
<br>
Api endpoint for reports, admin activities, login, events
<br>
Api endpoints for reporting and admin activities require Bearer auth to be made
<br>
Database that is used both for storing user data and schedule data is Apache Solr
<br>
For optimal design, even though we should have used only Thymeleaf, we used FullCalendar for our main events display,
which required us to add JS, but we kept it at minimum, only for FullCalendar functionalities
<br>

## Technologies & languages

---

* Java
* HTML 5
* CSS 3
* JavaScript

---

* Docker
* Solr

---

* Maven
* Spring Boot
* Thymeleaf

---

## Requirements

---
Requirements for java are located in [pom.xml](pom.xml) <br>
Most of JS requirements are located in [src/resources/static/js](src/resources/static/js) <br>
Most of CSS requirements are located in [src/resources/static/js](src/resources/static/css) <br>
JS and CSS requirements that are not in their folders are downloaded on runtime To use existing db setup, docker is
required, as well as docker-compose to run deploy

## Features

---

* user-friendly UI with multiple filters [View](#schedule-view)
* query form for classroom availability [Query](#query-available-classrooms)
* events list view per semester [List View](#list-view)
* apu endpoint for login [Login](#login-endpoint)
* api endpoint for statistics report [Reports](#reports-endpoint)
* api endpoint for admin activities [Admin](#admin-endpoint)
* api endpoint for events [Events](#events-endpoint)
* api endpoint for UI [UI](#ui-endpoint)

### Schedule view

---
Schedule view is main UI that uses FullCalendar as base for event display, all events are defined as repeating events,
with repeat times of 0 for single time events <br>
Main HTML template, used for most views, is [calendar](src/resources/templates/calendar.html)

* standard filters:
    * professor
    * classroom
    * department & year
* filters after login only:
    * hide / show courses that professor is holder but not lecturer

### Query available classrooms

---
Query form uses [queryForm](src/resources/templates/queryForm.html) as HTML template

* special form that allow query on database to find which classroom is free
* allows using capacity of classroom as part of query
* allows request to book period by sending mail (mail is templated, requires minimal user input)

### List view

---
List view, as it name suggests, is preview of all events in a list, filtered by semester, ether winter or summer
semester, uses [listCourse](src/resources/templates/listCourses.html) for HTML template

### Login endpoint

---
Login endpoint is used by spring security, [here](src/main/java/jtn/classSchedule/backend/api/UserController.java) is
link to controller, HTML template file is [login](src/resources/templates/login.html)

| endpoint      | method |
|---------------|--------|
| "/user/login" |  GET   |

### Reports endpoint

---
Reporting endpoint requires either Admin or User Bearer token for successful
call, [here](src/main/java/jtn/classSchedule/backend/api/ReportController.java) is link to controller, on success will
return json object

| report by | endpoint          | query params   | method |
|-----------|-------------------|----------------|--------|
| classroom | "/reports/events" | "by=classroom" | GET    |
| weekday   | "/reports/events" | "by=weekday"   | GET    |
| full      | "/reports/events" | -              | Get    |

### Admin endpoint

---
Admin endpoint require Admin Bearer token for successful
call, [here](src/main/java/jtn/classSchedule/backend/api/AdminController.java) is link to controller

| endpoint                                         | header               | body                                                                                    | method | description                   |
|--------------------------------------------------|----------------------|-----------------------------------------------------------------------------------------|--------|-------------------------------|
| "/sys-ad/add-course"                             | Bearer               | [CoursePostDto](src/main/java/jtn/classSchedule/backend/api/dto/CoursePostDto.java)     | POST   | add course                    | 
| "/sys-ad/delete-course/{courseShortNameId}/{id}" | Bearer               | -                                                                                       | DELETE | delete course                 | 
| "/sys-ad/add-user"                               | Bearer               | [RegistrationDto](src/main/java/jtn/classSchedule/backend/api/dto/RegistrationDto.java) | POST   | add user                      | 
| "/sys-ad/update-semester"                        | Bearer               | [Semester](src/main/java/jtn/classSchedule/backend/api/dto/Semester.java)               | POST   | update semester               | 
| "/sys-ad/add-user-token"                         | Bearer, Token, Code  | -                                                                                       | POST   | add user bearer token         | 
| "/sys-ad/remove-user-token"                      | Bearer, Token, Code  | -                                                                                       | POST   | remove user bearer token      | 
| "/sys-ad/remove-all-user-tokens"                 | Bearer, Code         | -                                                                                       | POST   | remove all user bearer tokens |

Header code is base64 encoded AdminToken:Token <br>

- Authentication: "Bearer admin token" <br>
  Token: "token to add or remove" <br>
  Code: "base64 encode AdminToken:Token" <br>

Endpoint for removing all user tokens does not accept token header, so when Code is made " " is used instead of token
User tokens and Admin token are defined in [application properties](src/resources/application.properties) <br>
Endpoint for updating semester writes to [semester.json](src/resources/semester.json), which is file for configuring
start and end time of each semester in this year (winter semester and summer semester)

### Events endpoint

---
Event endpoint is used by FullCallendar to retrieve data on events, in our case since we worked with courses we name it
CourseController, [here](src/main/java/jtn/classSchedule/backend/api/CourseController.java) is a link to it

| endpoint                     | PathVariable                | method | description              |
|------------------------------|-----------------------------|--------|--------------------------|
| "/api/allevents"             | -                           | GET    | all events               | 
| "/api/allevents/{username}"  | User Name (String)          | GET    | all events for user      | 
| "/api/course/{course}"       | Course Name (String)        | GET    | all events for course    | 
| "/api/classroom"             | -                           | GET    | all classrooms           | 
| "/api/classroom/{classroom}" | Classroom Name (String)     | GET    | all events for classroom | 
| "/api/professors"            | -                           | GET    | all professors           | 
| "/api/professor/{professor}" | Professor Name (String)     | GET    | all events for professor |
| "/api/find/{UUID}"           | Solr document UUID (String) | GET    | course by UUID           |

### UI endpoint

---
UI endpoint, as it name describes, is used for processing html templates and returning
them, [here](src/main/java/jtn/classSchedule/backend/api/TimetableController.java) is link to controller <br>
Every endpoint in this collection has `@AuthenticationPrincipal UserDetails`

| endpoint                 | PathVariable            | method | description                                                                                                                              |
|--------------------------|-------------------------|--------|------------------------------------------------------------------------------------------------------------------------------------------|
| "/"                      | -                       | GET    | home page, shows first element from list of 'Smjerovi' selection                                                                         | 
| "/"                      | -                       | GET    | home page for logged in user, requires `@ModalAttribute` [HideAndSeek](src/main/java/jtn/classSchedule/backend/api/dto/HideAndSeek.java) | 
| "/classroom/{classroom}" | Classroom Name (String) | GET    | events filtered by classroom                                                                                                             | 
| "/professor/{professor}  | Professor Name (String) | GET    | events filtered by professor                                                                                                             | 
| "/course/{courseValue}"  | Course Name (String)    | GET    | events filtered by course                                                                                                                |
| "/list/{semester}"       | Semester (String)       | GET    | list view for events by semester                                                                                                         | 

## Docker deploy database

---
Solr image are fixed to version `8.7` <br>
Since this project was originally started on Windows, docker-compose was used for docker deploy, but later we all moved
to Ubuntu but were too lazy to create new docker deploy file <br>
Docker-compose file can be found [here](src/main/java/jtn/classSchedule/backend/config/db) <br>
Before you run docker file you need to create folders that will be used for docker volume and update lines 9 & 22 <br>
First Solr for events is bound to port `8983` and second for users to `8984` <br>
Both containers are started by user `1000:1000` <br>
Once done all you need to do is run `docker-compose up -d --remove-orphan` <br>
We have provided some example for both database:

* [users](src/main/java/jtn/classSchedule/backend/config/db/user_data.json)
* [events](src/main/java/jtn/classSchedule/backend/config/db/timetable_data.json)

## Project structure

Main java file is [Timetable.java](src/main/java/jtn/classSchedule/backend/Timetable.java)

``` 
├── main 
│   └── java 
│       └── jtn 
│           └── classSchedule 
│               └── backend 
│                   ├── api 
│                   │   ├── CourseController.java 
│                   │   ├── dto 
│                   │   │   ├── ClassroomDto.java 
│                   │   │   ├── ClassroomFilterResult.java 
│                   │   │   ├── Classroom.java 
│                   │   │   ├── CourseDto.java 
│                   │   │   ├── CourseList.java 
│                   │   │   ├── CoursePostDto.java 
│                   │   │   ├── Event.java 
│                   │   │   ├── FormSessionDto.java 
│                   │   │   ├── HideAndSeek.java 
│                   │   │   ├── Professor.java 
│                   │   │   ├── QueryFormChoice.java 
│                   │   │   ├── QueryFormOptions.java 
│                   │   │   ├── RecurringEvent.java 
│                   │   │   ├── RegistrationDto.java 
│                   │   │   ├── ReportDateTimeDto.java 
│                   │   │   ├── ReportDto.java 
│                   │   │   ├── Semester.java 
│                   │   │   ├── UserDataDto.java 
│                   │   │   ├── CourseUpdateDto.java 
│                   │   │   └── UserDto.java 
│                   │   ├── QueryFormController.java 
│                   │   ├── ReportController.java 
│                   │   ├── AdminController.java 
│                   │   ├── TimetableController.java 
│                   │   └── UserController.java 
│                   ├── config 
│                   │   ├── db 
│                   │   │   ├── docker-compose.yml 
│                   │   │   ├── timetable_data.json 
│                   │   │   └── user_data.json 
│                   │   ├── login 
│                   │   │   ├── MvcConfig.java 
│                   │   │   └── SecurityConfiguration.java 
│                   │   ├── TimetableSolrConfiguration.java 
│                   │   ├── Token.java 
│                   │   ├── UserSolrConfiguration.java <br>
│                   │   └── UserTokens.java 
│                   ├── persistence 
│                   │   ├── course
│                   │   │   ├── Course.java
│                   │   │   └── CourseRepository.java
│                   │   └── user
│                   │       ├── User.java
│                   │       └── UserRepository.java
│                   ├── response
│                   │   └── CustomResponseEntity.java
│                   ├── service
│                   │   ├── course
│                   │   │   ├── CourseMapper.java
│                   │   │   ├── CourseServiceImpl.java
│                   │   │   └── CourseService.java
│                   │   ├── queryForm
│                   │   │   ├── QueryFormMapper.java
│                   │   │   ├── QueryFormServiceImpl.java
│                   │   │   └── QueryFormService.java
│                   │   ├── report
│                   │   │   ├── ReportServiceImpl.java
│                   │   │   └── ReportService.java
│                   │   ├── systemAdmin
│                   │   │   ├── AdminServiceImpl.java
│                   │   │   └── AdminService.java
│                   │   └── user
│                   │       ├── UserMapper.java
│                   │       ├── UserServiceImpl.java
│                   │       └── UserService.java
│                   └── Timetable.java
└── resources
    ├── application.properties
    ├── semester.json
    ├── static
    │   ├── css
    │   │   ├── 3rdParty
    │   │   │   ├── bootstrap.3.4.1.min.css
    │   │   │   ├── bootstrap.3.4.1.min.css.map
    │   │   │   ├── bootstrap.5.1.1.min.css
    │   │   │   ├── bootstrap.5.1.1.min.css.map
    │   │   │   ├── bootstrap-datepicker.css.map
    │   │   │   ├── bootstrap-datepicker.min.css
    │   │   │   ├── bootstrap-datepicker.standalone.min.css
    │   │   │   ├── bootstrap-datepicker.standalone.min.css.map
    │   │   │   └── fullcalendar.min.css
    │   │   ├── button.css
    │   │   ├── calendar.css
    │   │   ├── form.css
    │   │   ├── homebtn.css
    │   │   ├── listTable.css
    │   │   └── login.css
    │   ├── ec .jpg
    │   ├── eclogo.png
    │   ├── favicon.png
    │   ├── formbimg.jpg
    │   └── js
    │       ├── 3rdParty
    │       │   ├── bootstrap-datepicker.1.9.0.js
    │       │   ├── bootstrap-datepicker.min.js
    │       │   ├── fullcalendar.min.js
    │       │   ├── jquery.3.6.0.min.js
    │       │   ├── moment.2.29.1.min.js
    │       │   └── moment.2.29.1.min.js.map
    │       ├── calendar.js
    │       └── login.js
    └── templates
        ├── calendar.html
        ├── listCourses.html
        ├── login.html
        └── queryForm.html
```

## Conclusion

---
As we ran out of time only thing we left "unfinished" is this readme, we did our best to provide basic overview of this
project, but did not have time to make full how-to guide. Regardless of how good or bad readme is we hope that this
project, for those who find it, will be worthwhile and useful

Before you yell at us for how we did Bearer tokens, know that it was our intention to have tokens that can be forgotten
every time application is stopped, but if you need way to persist it, we think it should not be too much work to save it
to database, also replacing it with JWT should be much easier than what we did, but we wanted to do some code ourselves
not only use annotations

## Problems encountered

In this section we wanted to highlight some problems we encountered and attempted solved, in hopes that it might help
others

* Working with multiple Solr data sources, split solr config into to files
  [Timetable](src/main/java/jtn/classSchedule/backend/config/TimetableSolrConfiguration.java) and
  [User](src/main/java/jtn/classSchedule/backend/config/UserSolrConfiguration.java)

* We had a lot of trouble with blocked resources, this might look straight forward to other, but we found our selves in
  uncharted territory, solution we found can be found in this
  [file](src/main/java/jtn/classSchedule/backend/config/login/SecurityConfiguration.java)

* Reading values from application.properties, again straight forward one but took some time to figure out, all we needed
  was `@Value` on our variable and `@Configuration` on its class, example for this can be found
  in [config](src/main/java/jtn/classSchedule/backend/config) folder, in token / configuration Java files

## Authors

[**Tina**](https://github.com/tina-dragicevic)

- backend developer
- frontend developer
- UI designer

[**J.B.**](https://github.com/jb46692)

- frontend developer
- UI designer
- spring security

[Tax Max](https://github.com/TaxMax123)

- database management
- docker deploy
- software architect
- backend developer
