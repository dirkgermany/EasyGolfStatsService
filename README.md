# egsService
EasyGolfStats Service to store data of the EasyGolfStats App

## About
The egsService offers http methods to store data which are created by the EasyGolfStats App (see other project).
This service can only exist in dependency to an implementation of the Cloudstepper API gateway.

## Prerequisites
To use the egsService this prerequisites mus be fullfilled:
* deployment of the basic Cloudstepper services (serviceProvider, userService, configurationService, authenticationService)
* deployment of the egsService
* registration of egsService with configurationService
* permissions for user-rights in authenticationService
* creation of related user(s) with userService

## Usage
The egsService stores the training data in a database.
If needed it can provide the data as files. 
