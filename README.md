# DGdemo


# To test it:

curl -H "Content-type: application/json" -X POST -d '{"name": "BMW", "age": 31, "countryOfResidence": "Canada"}' http://localhost:8080/users
curl -H "Content-type: application/json" -X POST -d '{"name": "BMW", "age": 32, "countryOfResidence": "Canada"}' http://localhost:8080/users
curl -H "Content-type: application/json" -X POST -d '{"name": "BMW", "age": 33, "countryOfResidence": "Canada"}' http://localhost:8080/users
curl -H "Content-type: application/json" -X POST -d '{"name": "BMW", "age": 34, "countryOfResidence": "Canada"}' http://localhost:8080/users


curl http://localhost:8080/users


curl http://localhost:8080/users/BMW
