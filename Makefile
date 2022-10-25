jc = javac
jr = java
pr = python3.10
detached = screen -dm
visualize = konsole -e
classpath = server/src:server/lib/*:server/bin

sensorcount = 4
sensors = $(shell seq -s " " $(sensorcount))

arg1=localhost
arg2=1

.PHONY: clean sensor sensor client run 

compile:
	@$(jc) -cp $(classpath) server/src/server/Server.java -d server/bin

server: compile
	@$(jr) -cp $(classpath) server.Server 

sensor:
	@$(pr) sensor/sensor.py $(arg1) $(arg2)

client:
	@$(pr) client/client_stub.py

run: compile
	@echo "assuming this is running on Arch Linux with the following packages installed:"
	@echo "konsole, screen, java, python3.10"
	@echo "the running program is running detached using screen. They can be attached to by viewing the list of virtual terminals with 'screen -list' and can be attached to with 'screen -r <id>'"
	@$(detached) $(visualize) $(jr) -cp $(classpath) server.Server
	@$(foreach i, $(sensors), $(detached) $(visualize) $(pr) sensor.py $(arg1) $(i);) 

init: clean
	@mkdir -p server/files
	@echo '{"sensors":{"1":{}, "2":{}, "3":{}, "4":{}}, "users":{"alice":"test", "bob":"test2"}}' > server/files/database.json
	@touch server/files/log.log

clean:
	@rm -rf server/files
	@rm -rf server/bin