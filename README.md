# DGdemo


# To test it:

curl -H "Content-type: application/json" -X POST -d '{"name": "BMW", "age": 31, "countryOfResidence": "Canada"}' http://172.33.13.3:8080/users

curl -H "Content-type: application/json" -X POST -d '{"name": "BMW", "age": 32, "countryOfResidence": "Canada"}' http://172.33.13.3:8080/users

curl -H "Content-type: application/json" -X POST -d '{"name": "BMW", "age": 33, "countryOfResidence": "Canada"}' http://172.33.13.3:8080/users

curl -H "Content-type: application/json" -X POST -d '{"name": "BMW", "age": 34, "countryOfResidence": "Canada"}' http://172.33.13.3:8080/users


curl http://localhost:8080/users


curl http://localhost:8080/users/BMW


curl -H "Content-type: application/json" -X POST -d '{"name": "BMW", "age": 31, "countryOfResidence": "Canada"}' http://localhost:5678/users

curl http://localhost:5678/users

curl http://localhost:5678/users/BMW


docker logs -f dgspeed


```shell
cat > car1.yaml <<EOF
apiVersion: v1
kind: Pod
metadata:
  name: car1
spec:
  containers:
    - name: car1
      image: jkong85/ditto-dind:1.1
      securityContext:
        privileged: true
      imagePullPolicy: Always
      command: ["/bin/sh"]
      args: ["-c", "while true; do echo hello; sleep 10;done"]
      nodeSelector:
        kubernetes.io/hostname: node1
EOF
```
