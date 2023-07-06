# stedi

These commands assume that redis and kafka are running locally on ports 6379 and 9092 respectively.

To start this project, run the following commands:

`docker build . -t stedi`

`docker run --env-file ./env.list stedi`